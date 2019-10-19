package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.OrderCalum;
import com.qingcheng.service.order.OrderCalumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author: wy
 * @Date:2019/10/17 21:37
 */

   @RestController
   @RequestMapping("/ordercalum")
public class OrderCalumController {
       @Reference
        private OrderCalumService orderCalumService;
            @GetMapping("/dailyCalum")
        public List<Map> dailyCalum(String date1, String date2){
            return   orderCalumService.getYesterdayCalum(date1,date2);
        }
}
