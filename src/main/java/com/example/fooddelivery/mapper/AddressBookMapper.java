package com.example.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.fooddelivery.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
