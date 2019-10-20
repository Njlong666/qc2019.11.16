package com.qingcheng.service.order;

import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.center.OrderAndItems;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {

    public List<Order> findAll();

    public PageResult<Order> findPage(int page, int size);

    public List<Order> findList(Map<String, Object> searchMap);

    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);

    public Order findById(String id);

    public Map<String, Object> add(Order order);

    public void update(Order order);

    public void delete(String id);

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param transactionId
     */
    public void updatePayStatus(String orderId, String transactionId);


    /**
     * 根据用户名查询所有订单及其订单详情信息,分页展示
     *
     * @param username
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderAndItems> findOnesOrderAndItems(String username, int page, int size);


    /**
     * 根据用户名查询待付款订单,分页展示
     * @param username
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderAndItems> findOnesWaitToPayOrderAndItems(String username, int page, int size);


    /**
     * 根据用户名查询代发货订单
     * @param username
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderAndItems> findOnesWaitToSendOrderAndItems(String username,int page,int size);


    /**
     * 根据用户名查询待收货订单
     * @param username
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderAndItems> findOnesWaitToReceiveOrderAndItems(String username,int page,int size);


    /**
     * 根据用户名查询带评价订单
     * @param username
     * @param page
     * @param size
     * @return
     */
    public PageResult<OrderAndItems> findOnesWaitToEvaluateOrderAndItems(String username,int page,int size);


    public Result confirmReceive(String id);


    public OrderAndItems findOrderAndItemsByOrderId(String id);

    public Result removeOrder(String orderId);
}
