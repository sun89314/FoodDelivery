package com.example.fooddelivery.dto;


import com.example.fooddelivery.entity.Setmeal;
import com.example.fooddelivery.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
