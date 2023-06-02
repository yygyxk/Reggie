package com.kking.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kking.reggie.common.R;
import com.kking.reggie.dto.DishDto;
import com.kking.reggie.entity.Category;
import com.kking.reggie.entity.Dish;
import com.kking.reggie.entity.DishFlavor;
import com.kking.reggie.service.CategoryService;
import com.kking.reggie.service.DishFlavorService;
import com.kking.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //分类id
            Long categoryId = item.getCategoryId();
            if (categoryId != null) {
                //根据id查询分类对象
                Category category = categoryService.getById(categoryId);
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

//    /**
//     * 根据条件来查询对应的条件数据
//     * 新增套餐时，选择菜品，需要查询
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加条件，查询状态为1(起售状态)的菜品
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId())
//                .eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件来查询对应的条件数据
     * 新增套餐时，选择菜品以及口味，返回至前端用户页面，需要查询
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtolist = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            List<DishFlavor> list1 = new ArrayList<>();
            Long id = item.getId();
            if (id != null) {
                queryWrapper1.eq(DishFlavor::getDishId, id);
                list1 = dishFlavorService.list(queryWrapper1);
            }
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtolist);
    }
}
