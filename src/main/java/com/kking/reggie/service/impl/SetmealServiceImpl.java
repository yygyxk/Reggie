package com.kking.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kking.reggie.common.CustomException;
import com.kking.reggie.dto.SetmealDto;
import com.kking.reggie.entity.Setmeal;
import com.kking.reggie.entity.SetmealDish;
import com.kking.reggie.mapper.SetmealMapper;
import com.kking.reggie.service.SetmealDishService;
import com.kking.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(collect);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，停售才可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        long count = this.count(queryWrapper);
        //不能删，抛异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //先删除套餐表中的数据--setmeal
        this.removeByIds(ids);
        //删除关系表中的数据--setmeal_dish
        LambdaQueryWrapper<SetmealDish> setmealDish = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<SetmealDish> in = setmealDish.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(in);
    }
}
