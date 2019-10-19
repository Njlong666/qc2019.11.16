package com.qingcheng.pojo.order;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wy
 * @Date:2019/9/24 15:35
 */
 @Table(name = "order_calumniate")
public class OrderCalum implements Serializable {
   /* id INT PRIMARY KEY AUTO_INCREMENT,
    orderNum1 INT DEFAULT NULL COMMENT '下单人数',
    orderCount1 INT DEFAULT NULL COMMENT '订单数',
    orderTotal1 INT DEFAULT NULL COMMENT '下单件数',
    orderCount3 INT DEFAULT NULL COMMENT '有效订单数',
    payMoney2 INT DEFAULT NULL COMMENT '付款金额',
    orderMoney INT DEFAULT NULL COMMENT '下单金额',
    orderCount2 INT DEFAULT NULL COMMENT '付款人数',
    orderTotal3 INT DEFAULT NULL COMMENT '有效件数',
    returnMoney INT DEFAULT NULL COMMENT '退款金额',
    orderTotal2 INT DEFAULT NULL COMMENT '付款件数',
    ordreNum2 INT DEFAULT NULL COMMENT '付款人数',
    create_time DATE  COMMENT '创建时间'*/
     @Id
    private Integer id;
    private Integer orderNum1;
    private Integer orderCount1;
    private Integer orderTotal1;
    private Integer orderCount3;
    private Integer payMoney2;
    private Integer orderMoney;
    private Integer orderCount2;
    private Integer orderTotal3;
    private Integer orderTotal2;
    private Integer orderNum2;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderNum1() {
        return orderNum1;
    }

    public void setOrderNum1(Integer orderNum1) {
        this.orderNum1 = orderNum1;
    }

    public Integer getOrderCount1() {
        return orderCount1;
    }

    public void setOrderCount1(Integer orderCount1) {
        this.orderCount1 = orderCount1;
    }

    public Integer getOrderTotal1() {
        return orderTotal1;
    }

    public void setOrderTotal1(Integer orderTotal1) {
        this.orderTotal1 = orderTotal1;
    }

    public Integer getOrderCount3() {
        return orderCount3;
    }

    public void setOrderCount3(Integer orderCount3) {
        this.orderCount3 = orderCount3;
    }

    public Integer getPayMoney2() {
        return payMoney2;
    }

    public void setPayMoney2(Integer payMoney2) {
        this.payMoney2 = payMoney2;
    }

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getOrderCount2() {
        return orderCount2;
    }

    public void setOrderCount2(Integer orderCount2) {
        this.orderCount2 = orderCount2;
    }

    public Integer getOrderTotal3() {
        return orderTotal3;
    }

    public void setOrderTotal3(Integer orderTotal3) {
        this.orderTotal3 = orderTotal3;
    }

    public Integer getOrderTotal2() {
        return orderTotal2;
    }

    public void setOrderTotal2(Integer orderTotal2) {
        this.orderTotal2 = orderTotal2;
    }

    public Integer getOrderNum2() {
        return orderNum2;
    }

    public void setOrderNum2(Integer orderNum2) {
        this.orderNum2 = orderNum2;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
