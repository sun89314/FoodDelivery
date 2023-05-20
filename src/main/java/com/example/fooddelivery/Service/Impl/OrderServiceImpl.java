package com.example.fooddelivery.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.common.BaseContext;
import com.example.fooddelivery.common.CustomException;
import com.example.fooddelivery.entity.*;
import com.example.fooddelivery.Service.*;
import com.example.fooddelivery.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void change( List<ShoppingCart> shoppingCarts){
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCarts.get(0),cart);
        cart.setNumber(cart.getNumber() + 1);
        Connection conn = sqlSessionTemplate.getConnection();
        System.out.println("Current connection: " + conn);
        shoppingCartService.updateById(cart);

    }
    /**
     * 用户下单
     * @param orders
     */
    @Transactional(isolation = Isolation.DEFAULT)
    public void submit(Orders orders,Thread thread1){
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();
        Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        if(isolationLevel != null) System.out.println("Current transaction isolation level: " + isolationLevel);
        Connection conn = sqlSessionTemplate.getConnection();
        System.out.println("Current connection: " + conn);
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        log.info(shoppingCarts.toString());
        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

//        try {
//            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            thread1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        change(shoppingCarts);

//        try {
//            conn.commit();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

            sqlSession.clearCache();


        List<ShoppingCart> shoppingCarts2 =  shoppingCartService.list(wrapper);
        log.info(shoppingCarts2.toString());

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts2.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }
}