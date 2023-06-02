package com.kking.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kking.reggie.common.CustomException;
import com.kking.reggie.entity.Category;
import com.kking.reggie.entity.Dish;
import com.kking.reggie.entity.Setmeal;
import com.kking.reggie.mapper.CategeoryMapper;
import com.kking.reggie.service.CategoryService;
import com.kking.reggie.service.DishService;
import com.kking.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategeoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        /**
         * 根据查询条件，进行分类查询
         */
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        long count = dishService.count(dishLambdaQueryWrapper);
        /**
         * 查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
         */
        if(count > 0){
            //关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除！");
        }

        /**
         * 查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
         */
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            //关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除！");
        }
        //正常删除分类
        super.removeById(id);
    }
}
