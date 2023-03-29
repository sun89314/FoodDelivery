package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.CategoryService;
import com.example.fooddelivery.Service.SetMealDishService;
import com.example.fooddelivery.Service.SetMealService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.dto.SetmealDto;
import com.example.fooddelivery.entity.Category;
import com.example.fooddelivery.entity.Setmeal;
import com.example.fooddelivery.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetMeal(@RequestBody SetmealDto setmealDto){
        setMealService.saveSetMeal(setmealDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize){
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        setMealService.page(pageInfo,queryWrapper);
        Page finalPage = new Page(page, pageSize);
        BeanUtils.copyProperties(pageInfo,finalPage);
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();
        for(Setmeal setmeal:records){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
            List<SetmealDish> setmealDishes = setMealDishService.list(setmealDishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            setmealDtos.add(setmealDto);
        }
        finalPage.setRecords(setmealDtos);
        return R.success(finalPage);
    }
}
