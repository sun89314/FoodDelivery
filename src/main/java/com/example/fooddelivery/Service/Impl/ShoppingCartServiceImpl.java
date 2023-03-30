package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.fooddelivery.Service.ShoppingCartService;
import com.example.fooddelivery.entity.ShoppingCart;
import com.example.fooddelivery.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
