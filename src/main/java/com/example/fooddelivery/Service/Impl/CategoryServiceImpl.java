package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.CategoryService;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.Service.SetMealService;
import com.example.fooddelivery.common.CustomException;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.Category;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Setmeal;
import com.example.fooddelivery.mapper.CategoryMapper;
import com.example.fooddelivery.mapper.SetMealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;
    /**
     * 根据id删除分类，如果分类没有关联菜品
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper<>();
        dishqueryWrapper.eq(Dish::getCategoryId,id);
        long count = dishService.count(dishqueryWrapper);
        if(count > 0){
            //抛出业务异常
            throw new CustomException("当前分类关联了菜品");
        }
        //查询当前分类是否关联套餐
        LambdaQueryWrapper<Setmeal> mealQueryWrapper = new LambdaQueryWrapper<>();
        mealQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setMealService.count(mealQueryWrapper);
        if(count2 > 0){
            //抛出业务异常
            throw new CustomException("当前分类关联了套餐");
        }
        //正常删除
        super.removeById(id);


    }
}
