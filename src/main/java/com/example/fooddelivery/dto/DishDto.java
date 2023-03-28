package com.example.fooddelivery.dto;

import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
