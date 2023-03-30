package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.AddressBookService;
import com.example.fooddelivery.entity.AddressBook;
import com.example.fooddelivery.mapper.AddressBookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
