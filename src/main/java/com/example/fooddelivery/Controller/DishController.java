package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.CategoryService;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.dto.DishDto;
import com.example.fooddelivery.entity.Category;
import com.example.fooddelivery.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取菜品分页内容
     * 关键点1：页面中需要的元素不可能每次都正好对应数据库中的元素，所以需要额外创建一个data transfer object，继承原来的类
     * 同时在这个新类中进行两表链接的操作
     * 关键点二：如这边的菜品分类中需要用分类id去链接另一个表的分类名，这时候就需要查出id后，
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> getPage(int page, int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //dishdto里面有菜品分类
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝,忽略records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        for(Dish dish: records){
            //records里面存的是真正的对象信息，其他的都是page类的信息
            //遍历每一个类就是遍历数据库重的每一行，获取到每一个菜品的分类id后，在第二张表里面去查询类名
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            list.add(dishDto);
        }
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }
    @GetMapping("/{id}")
    public R<DishDto> getSingleDish(@PathVariable long id){
        log.info("获取id为,{} 的元素",id);
        DishDto dto = dishService.getByIdWithFlavor(id);
        return R.success(dto);
    }
//    @PostMapping("/{id}")
//    public R<String> update(@PathVariable int id){
//
//        return R.success("修改成功");
//    }
    /**
     * 启用禁用菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam("ids") Long ids){
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Dish::getId,ids).set(Dish::getStatus,status);
        dishService.update(null,updateWrapper);
        return R.success("修改菜品状态成功");
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功");
    }
}
