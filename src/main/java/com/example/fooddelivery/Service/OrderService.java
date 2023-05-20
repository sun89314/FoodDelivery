package com.example.fooddelivery.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.entity.Orders;
//import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     *
     * @param orders
     * @param thread
     */
    public void submit(Orders orders, Thread thread);
}
