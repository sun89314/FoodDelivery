package com.example.fooddelivery.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.CategoryService;
import com.example.fooddelivery.Service.DishFlavorService;
import com.example.fooddelivery.Service.DishService;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.dto.DishDto;
import com.example.fooddelivery.entity.Category;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    public DishService dishService;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public DishFlavorService dishFlavorService;
    @Autowired
    public RedisTemplate redisTemplate;

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

    /**
     * 返回指定菜品对象给到菜品修改界面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getSingleDish(@PathVariable long id){
        log.info("获取id为,{} 的元素",id);
        DishDto dto = dishService.getByIdWithFlavor(id);
        return R.success(dto);
    }
    /**
     * 根据分类id或者搜索名字去返回菜品列表
     * @param  dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategory(Dish dish){
        List<DishDto> list = null;
        String key = "dish_"+ dish.getCategoryId()+"_"+dish.getStatus();
        list = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if(list != null) return R.success(list);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        list = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);//复制老的
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            dishDto.setFlavors(dishFlavorService.list(queryWrapper1));
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,list,1, TimeUnit.DAYS);


        return R.success(list);
    }
    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
//    @PostMapping
//    public R<List<Dish>> getDishByCategory(@RequestBody DishDto dishDto){
//        Dish dish = new Dish();
//        BeanUtils.copyProperties(dishDto,dish);
//        dishService.save(dish);
//        List<DishFlavor> flavors = dishDto.getFlavors();
//        for(DishFlavor flavor:flavors){
//            flavor.setDishId(dish.getId());
//        }
//        dishFlavorService.saveBatch(flavors);
//        return R.success(Arrays.asList(dish));
//    }



    /**
     * 启用禁用菜品,接受一个或者多个id的字符串
     * @param status
     * @param s
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam("ids") String s){
//        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        String[] ids = s.split(",");
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids).set(Dish::getStatus,status);

        dishService.update(null,updateWrapper);
        return R.success("修改菜品状态成功");
    }
    /**
     * 删除菜品,接受一个或者多个id的字符串
     * @param s
     * @return
     */
    @DeleteMapping
    public R<String> deleteDishes(@RequestParam("ids") String s){
//        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        String[] ids = s.split(",");
        List<String> list = Arrays.asList(ids);
        dishService.removeDishes(list);
        return R.success("Success");
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
