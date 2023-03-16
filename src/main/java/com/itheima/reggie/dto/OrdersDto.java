package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author
 * @create 2023-03-14 16:36
 */
@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
    private int sumNum;
    private String userName;

}