package com.example.fooddelivery.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.entity.Category;
import org.springframework.stereotype.Service;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
