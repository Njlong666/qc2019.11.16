package com.qingcheng.pojo.center;

import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author: lenovo
 * @Date: 2019/10/18 22:24
 */
public class OrderAndItems implements Serializable {
    private Order order;

    private List<OrderItem> list;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getList() {
        return list;
    }

    public void setList(List<OrderItem> list) {
        this.list = list;
    }
}
