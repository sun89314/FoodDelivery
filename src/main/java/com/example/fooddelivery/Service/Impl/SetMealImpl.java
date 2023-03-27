package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.SetMealService;
import com.example.fooddelivery.entity.Setmeal;
import com.example.fooddelivery.mapper.SetMealMapper;
import org.springframework.stereotype.Service;

@Service
public class SetMealImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
}
