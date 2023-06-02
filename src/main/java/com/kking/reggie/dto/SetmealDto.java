package com.kking.reggie.dto;


import com.kking.reggie.entity.Setmeal;
import com.kking.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
