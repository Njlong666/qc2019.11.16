package com.qingcheng.dao;

import com.qingcheng.pojo.order.OrderCalum;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author: wy
 * @Date:2019/9/24 16:01
 */
public interface OrderCalumMapper extends Mapper<OrderCalum> {
    //每一天的交易各个数据统计(定时调度)
    @Select("SELECT COUNT(o.username) orderNum1,COUNT(o.id) orderCount1, SUM(o.total_num) orderTotal1,COUNT(o2.id) orderCount3,COUNT(o1.`id`) orderCount2,SUM(o2.total_num) orderTotal3,SUM(o.`total_money`) orderMoney,SUM(r.return_money) returnMoney,COUNT(o1.total_num) orderTotal2,SUM(o1.pay_money) payMoney2,COUNT(o1.username) orderNum2 ,DATE_FORMAT(o.`pay_time`,'%Y-%m-%d') createTime " +
            "       FROM tb_order o LEFT JOIN tb_order_item oi ON o.id=oi.`order_id`  " +
            "       LEFT JOIN order1 o1 ON o1.id=oi.order_id  " +
            "       LEFT JOIN order2 o2  ON o2.id=oi.order_id " +
            "       LEFT JOIN tb_return_order r ON r.order_id=o.`id`  " +
            "       AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')=#{date} ")
    List<OrderCalum> orderReport(@Param("date") LocalDate date);



          @Select("SELECT order_num1 orderNum1, pay_money2 payMoney2,return_money returnMoney, order_num2 orderNum2, order_total2 orderTotal2  " +
                  "           FROM order_calumniate WHERE create_time>= #{date1}  AND create_time<= #{date2} ")
       List<Map>  getOrderCalums(@Param("date1") String date1,@Param("date2") String date2);

}
