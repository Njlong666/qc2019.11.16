package com.qingcheng.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;


/****
 *
 *order 监听
 *
 * @Author:itheima
 * @Description:
 *****/
public class OrderMessageListener implements MessageListener {


    @Reference
    private OrderService orderService;

    @Override
    public void onMessage(Message message) {

        String orderId = new String(message.getBody());
        System.out.println(orderId+"...++...");
        orderService.rollBackOrder(orderId);
    }
}
