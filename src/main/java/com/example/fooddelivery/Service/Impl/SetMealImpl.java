package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.SetMealDishService;
import com.example.fooddelivery.Service.SetMealService;
import com.example.fooddelivery.dto.SetmealDto;
import com.example.fooddelivery.entity.Setmeal;
import com.example.fooddelivery.entity.SetmealDish;
import com.example.fooddelivery.mapper.SetMealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;
    @Override
    public void saveSetMeal(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        for(SetmealDish setmealDish:setmealDto.getSetmealDishes()){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setMealDishService.saveBatch(setmealDto.getSetmealDishes());
    }

    @Override
    public void removeWithDishByIds(List<String> list) {
//        this.removeByIds(list.stream().map(Long::parseLong).collect(Collectors.toList()));
        for(String StringId:list){
            Long id = Long.parseLong(StringId);
            this.removeById(id);
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,id);
            setMealDishService.remove(queryWrapper);
//            setMealDishService.remove(id);
        }
    }
}
