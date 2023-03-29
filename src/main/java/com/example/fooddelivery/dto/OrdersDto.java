package com.example.fooddelivery.dto;


import com.example.fooddelivery.entity.OrderDetail;
import com.example.fooddelivery.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
