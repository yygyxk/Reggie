package com.kking.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kking.reggie.dto.DishDto;
import com.kking.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
