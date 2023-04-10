package com.example.fooddelivery.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.fooddelivery.Service.OrderService;
import com.example.fooddelivery.Service.ShoppingCartService;
import com.example.fooddelivery.common.BaseContext;
import com.example.fooddelivery.common.CustomException;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.entity.Orders;
import com.example.fooddelivery.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        //获得当前用户id
//        Long userId = BaseContext.getCurrentId();
//
//        //查询当前用户的购物车数据
//        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(ShoppingCart::getUserId,userId);
//        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
//        Thread thread = new Thread(){
//            public void run(){
//                synchronized (this) {
//                    try {
//                        wait();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    ShoppingCart cart = new ShoppingCart();
//                    BeanUtils.copyProperties(shoppingCarts.get(0),cart);
//                    cart.setNumber(cart.getNumber() + 1);
//                    shoppingCartService.updateById(cart);
//                }
//            }
//        };
//        thread.start();
//        try {
//            sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        orderService.submit(orders);



        return R.success("下单成功");
    }
}