package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page<Dish>> getPage(int page, int pageSize){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam("ids") Long ids){
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Dish::getId,ids).set(Dish::getStatus,status);
        dishService.update(null,updateWrapper);
        return R.success("修改菜品状态成功");
    }
}
