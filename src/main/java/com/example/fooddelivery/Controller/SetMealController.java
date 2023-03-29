package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.SetMealService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.dto.SetmealDto;
import com.example.fooddelivery.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

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
        return R.success(pageInfo);
    }
}
