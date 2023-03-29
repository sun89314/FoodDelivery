package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.Service.DishFlavorService;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.dto.DishDto;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.DishFlavor;
import com.example.fooddelivery.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 一个类存两张表的数据
     * 关键点1：存了一行数据后如何获取数据的自动生成id？----会直接返回给类中
     * 关键点2：saveBatch来存储多个数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor:flavors){
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询2表内容，返回dishdto类，回显给服务器
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //两者都赋值给dto对象
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     *  同时更新两张表
     *  关键点1： 更新的方式可以是全删后重新加
     *  关键点2：可以直接updatedto元素
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //先更新dish，不用担心字段不符合
        this.updateById(dishDto);
        //先清理口味数据，再添加口味数据
        //这边不能使用removeByid因为是根据dishflavor表中的dishid列不是dishflavorid 列去删除的。
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public void removeDishes(List<String> list) {
        this.removeBatchByIds(list);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,list);
        dishFlavorService.remove(queryWrapper);
    }
}
