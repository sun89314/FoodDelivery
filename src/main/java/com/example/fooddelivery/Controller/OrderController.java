package com.example.fooddelivery.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.fooddelivery.Service.OrderDetailService;
import com.example.fooddelivery.Service.OrderService;
import com.example.fooddelivery.Service.ShoppingCartService;
import com.example.fooddelivery.Service.UserService;
import com.example.fooddelivery.common.BaseContext;
import com.example.fooddelivery.common.CustomException;
import com.example.fooddelivery.common.R;
import com.example.fooddelivery.dto.OrdersDto;
import com.example.fooddelivery.entity.Employee;
import com.example.fooddelivery.entity.OrderDetail;
import com.example.fooddelivery.entity.Orders;
import com.example.fooddelivery.entity.ShoppingCart;
import com.example.fooddelivery.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.datasource.ConnectionHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
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
    public OrderService orderService;
    @Autowired
    public DataSource dataSource;
    @Autowired
    public UserService userService;
    @Autowired
    public OrderDetailService orderDetailService;
    @Autowired
    public ShoppingCartService shoppingCartService;
    class MyThread extends Thread{
        public List<ShoppingCart> shoppingCarts;

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
     * User submit order
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

    /**
     * using page object to get all employees
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> getPage(int page, int pageSize, String number, String beginTime, String endTime){
//        log.info("分页查询订单信息,page={},pageSize={},number={},BeginTime={},EndTime={}",page,pageSize,number,beginTime,endTime);
        //Create Page object
        Page pageinfo = new Page(page,pageSize);
        //query all the employees, and sort by update time to get the latest employee
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(number), Orders::getId,number);
        queryWrapper.orderByDesc(Orders::getOrderTime);
//        queryWrapper.last("limit " + (page - 1) * pageSize + "," + pageSize);
        queryWrapper.between(!StringUtils.isEmpty(beginTime) && !StringUtils.isEmpty(endTime)
                ,Orders::getOrderTime,beginTime,endTime);
        List<Orders> orders = orderService.list(queryWrapper);
        List<OrdersDto> ordersDtos = new ArrayList<>();
        for (Orders order : orders) {
            OrdersDto ordersDto = new OrdersDto();
            Long userId = order.getUserId();
            User user = userService.getById(userId);
            BeanUtils.copyProperties(order,ordersDto);
            ordersDto.setUserName(user.getName());
            ordersDtos.add(ordersDto);
        }
        pageinfo.setRecords(ordersDtos.subList((page - 1) * pageSize,page * pageSize > ordersDtos.size() ? ordersDtos.size() : page * pageSize));
        pageinfo.setTotal(ordersDtos.size());
//        orderService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }
    @PutMapping()
    public R<String> ChangeStatus(HttpServletRequest request, @RequestBody Orders orders){
//        Orders orders = orderService.getById(id);
//        if(orders == null){
//            throw new CustomException("订单不存在");
//        }
//        orders.setStatus(status);

        orderService.updateById(orders);
        return  R.success("Success");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getUserPage(int page, int pageSize, HttpSession session) {
        Long userId = BaseContext.getCurrentId();
        //Create Page object
        Page pageinfo = new Page(page, pageSize);
        //Find Users most recent Order
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, userId)
                    .orderByDesc(Orders::getOrderTime)
                    .last("limit " + (page - 1) * pageSize + "," + pageSize);
        List<Orders> orders = orderService.list(queryWrapper);
        if(orders.size() == 0){
            return R.success(null);
        }
        List<OrdersDto> ordersDtos = new ArrayList<>();
        for(Orders order : orders){
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order,ordersDto);
            LambdaQueryWrapper<OrderDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
            detailQueryWrapper.eq(OrderDetail::getOrderId,order.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(detailQueryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtos.add(ordersDto);
        }
        //find the order detail
        pageinfo.setRecords(ordersDtos.subList((page - 1) * pageSize,page * pageSize > ordersDtos.size() ? ordersDtos.size() : page * pageSize));
        pageinfo.setTotal(ordersDtos.size());
        return R.success(pageinfo);
    }

}