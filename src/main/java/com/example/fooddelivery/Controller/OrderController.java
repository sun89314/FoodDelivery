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
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.datasource.ConnectionHolder;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    static Object lock = new Object();
    @Autowired
    private OrderService orderService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ShoppingCartService shoppingCartService;
    class MyThread extends Thread{
        private List<ShoppingCart> shoppingCarts;

        public MyThread(List<ShoppingCart> shoppingCarts ) {
            this.shoppingCarts = shoppingCarts;
        }
        @Transactional
        public void run(){

            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(shoppingCarts.get(0),cart);
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);

        }
    };

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单:{}",orders);
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();
        Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        if(isolationLevel != null)System.out.println("Current transaction isolation level: " + isolationLevel);

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        Thread thread = new MyThread(shoppingCarts);
//        thread.start();
//        try {
//            sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        orderService.submit(orders,thread);



        return R.success("下单成功");
    }
}