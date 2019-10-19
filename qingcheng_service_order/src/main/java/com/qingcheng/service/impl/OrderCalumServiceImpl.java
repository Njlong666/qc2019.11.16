package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.OrderCalumMapper;
import com.qingcheng.pojo.order.OrderCalum;
import com.qingcheng.service.order.OrderCalumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author: wy
 * @Date:2019/9/24 16:11
 */
 @Service(interfaceClass = OrderCalumService.class)
 @Transactional
public class OrderCalumServiceImpl implements OrderCalumService {

    @Autowired
    private OrderCalumMapper orderCalumMapper;

    public List<OrderCalum> getOrderCalum(LocalDate date){
        return orderCalumMapper .orderReport(date);
    }

    //昨日订单交易报告记录
    public void saveOrderCalum(){
        LocalDate localDate = LocalDate.now().minusDays(1);

        List<OrderCalum> orderCalum = getOrderCalum(localDate);
        if(orderCalum!=null) {
            orderCalumMapper.insert(orderCalum.get(0));
        }
    }

        public List<Map> getYesterdayCalum(String date1, String date2){

           return  orderCalumMapper.getOrderCalums(date1,date2);

        }


}
