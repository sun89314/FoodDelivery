package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.fooddelivery.Service.UserService;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
