package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.itheima.reggie.entity.OrderDetail;
//import com.itheima.reggie.mapper.OrderDetailMapper;
//import com.itheima.reggie.service.OrderDetailService;
import com.example.fooddelivery.Service.OrderDetailService;
import com.example.fooddelivery.entity.OrderDetail;
import com.example.fooddelivery.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}