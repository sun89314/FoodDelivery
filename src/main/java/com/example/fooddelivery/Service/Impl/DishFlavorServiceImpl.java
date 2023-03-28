package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.DishFlavorService;
import com.example.fooddelivery.entity.DishFlavor;
import com.example.fooddelivery.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
