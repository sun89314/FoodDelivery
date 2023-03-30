package com.example.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.fooddelivery.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}