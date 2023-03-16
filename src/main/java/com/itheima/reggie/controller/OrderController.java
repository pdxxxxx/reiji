package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * @create 2023-03-14 15:46
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    //订单管理
    @Transactional
    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage=new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> collect = records.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);
            Long id = item.getId();
            Orders orders = orderService.getById(id);
//            String number = orders.getNumber();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, id);
            List<OrderDetail> list = orderDetailService.list(wrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(!StringUtils.isEmpty(number),Orders::getNumber,number);

        if(beginTime!=null && endTime!=null){
            queryWrapper.ge(Orders::getOrderTime,beginTime);
            queryWrapper.le(Orders::getOrderTime,beginTime);
        }
        queryWrapper.orderByDesc(Orders::getOrderTime);

      orderService.page(pageInfo,queryWrapper);

        List<Orders> records = pageInfo.getRecords();
        BeanUtils.copyProperties(pageInfo,records,"records");

        List<OrdersDto> collect = records.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            String user = "用户" + item.getUserId();
            ordersDto.setUserName(user);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return R.success(ordersDtoPage);
    }

    @PutMapping
    public R<String> send(@RequestBody Orders order){
        Long id = order.getId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,id);
        Orders one = orderService.getOne(queryWrapper);
        one.setStatus(order.getStatus());
        orderService.updateById(one);
        return R.success("更新状态成功");

    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        Long id = orders.getId();
        Orders orders1 = orderService.getById(id);

        long id1 = IdWorker.getId();
        orders1.setId(id1);

        String number=String.valueOf(IdWorker.getId());
        orders1.setNumber(number);

        orders1.setOrderTime(LocalDateTime.now());
        orders1.setCheckoutTime(LocalDateTime.now());
        orders1.setStatus(2);

        orderService.save(orders1);

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        list = list.stream().map(item -> {
            Long detailId = IdWorker.getId();
            item.setOrderId(id1);
            item.setId(detailId);
            return item;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(list);
        return R.success("再来一单成功");

    }
}
