package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.center.OrderAndItems;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.order.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: lenovo
 * @Date: 2019/10/18 16:35
 */
@RestController
@RequestMapping("/orderCenter")
public class OrderController {

    @Reference
    private OrderService orderService;


    @RequestMapping("/getAllOrders")
    public PageResult<OrderAndItems> getAllOrders(Integer page,Integer size) {
        if (page == null) {
            page = 1;
        }
        if(size == null) {
            size = 2;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<OrderAndItems> orderAndItems = orderService.findOnesOrderAndItems(name, page, size);
        return orderAndItems;
    }

    @RequestMapping("/getWaitToPayOrders")
    public PageResult<OrderAndItems> getWaitToPayOrders(Integer page,Integer size) {
        if (page == null) {
            page = 1;
        }
        if(size == null) {
            size = 2;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<OrderAndItems> orderAndItems = orderService.findOnesWaitToPayOrderAndItems(name, page, size);
        return orderAndItems;
    }

    @RequestMapping("/getWaitToSendOrders")
    public PageResult<OrderAndItems> getWaitToSendOrders(Integer page,Integer size) {
        if (page == null) {
            page = 1;
        }
        if(size == null) {
            size = 2;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<OrderAndItems> orderAndItems = orderService.findOnesWaitToSendOrderAndItems(name, page, size);
        return orderAndItems;
    }

    @RequestMapping("/getWaitToReceiveOrders")
    public PageResult<OrderAndItems> getWaitToReceiveOrders(Integer page,Integer size) {
        if (page == null) {
            page = 1;
        }
        if(size == null) {
            size = 2;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<OrderAndItems> orderAndItems = orderService.findOnesWaitToReceiveOrderAndItems(name, page, size);
        return orderAndItems;
    }

    @RequestMapping("/getWaitToEvaluateOrders")
    public PageResult<OrderAndItems> getWaitToEvaluateOrders(Integer page,Integer size) {
        if (page == null) {
            page = 1;
        }
        if(size == null) {
            size = 2;
        }
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<OrderAndItems> orderAndItems = orderService.findOnesWaitToEvaluateOrderAndItems(name, page, size);
        return orderAndItems;
    }


    @GetMapping("/confirm")
    public Result confirmReceive(String id) {
        return orderService.confirmReceive(id);
    }
}
