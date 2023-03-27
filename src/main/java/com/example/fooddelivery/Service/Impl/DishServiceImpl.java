package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.mapper.DishMapper;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
