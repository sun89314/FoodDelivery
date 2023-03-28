package com.example.fooddelivery.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.dto.DishDto;
import com.example.fooddelivery.entity.Dish;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时添加菜品的风味
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
