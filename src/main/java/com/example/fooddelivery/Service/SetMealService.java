package com.example.fooddelivery.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.dto.SetmealDto;
import com.example.fooddelivery.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    void saveSetMeal(SetmealDto setmealDto);

    void removeWithDishByIds(List<String> list);
}
