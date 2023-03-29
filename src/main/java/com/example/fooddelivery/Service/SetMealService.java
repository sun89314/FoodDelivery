package com.example.fooddelivery.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.dto.SetmealDto;
import com.example.fooddelivery.entity.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    void saveSetMeal(SetmealDto setmealDto);
}
