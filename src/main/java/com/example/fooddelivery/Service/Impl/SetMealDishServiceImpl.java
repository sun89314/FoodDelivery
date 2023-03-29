package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.SetMealDishService;
import com.example.fooddelivery.entity.SetmealDish;
import com.example.fooddelivery.mapper.SetMealDishMapper;
import org.springframework.stereotype.Service;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper,SetmealDish> implements SetMealDishService {
}
