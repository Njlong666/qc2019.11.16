package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderCalumService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import com.qingcheng.entity.Result;

@Component
public class OrderTask {

    //@Scheduled(cron = "0/5 * * * * ?")
    public void orderTimeOutLogic(){
        System.out.println(new Date());
    }


    @Reference
    private CategoryReportService categoryReportService;


    @Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(cron = "0/10 * * * * ?")
    public void createCategoryReportDate(){
        System.out.println("生成类目统计数据");
        categoryReportService.createData();
    }

    /*配置task标签*/
    @Reference
    private OrderCalumService orderCalumService;
    /*每天的数据*/
    @Scheduled(cron = "0 0 1 * * ?")
    public void saveOrderReportData(){
        System.out.println("订单数据生成...");
        orderCalumService.saveOrderCalum();
    }


}
