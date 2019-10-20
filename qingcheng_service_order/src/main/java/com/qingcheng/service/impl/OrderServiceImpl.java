package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.center.OrderAndItems;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderLog;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.pay.WeixinPayService;
import com.qingcheng.util.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Autowired
    private CartService cartService;

    @Reference
    private SkuService skuService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增
     * @param order
     */
    @Transactional
    public Map<String,Object> add(Order order) {
        //1.获取选中的购物车
        List<Map<String, Object>> cartList = cartService.findNewOrderItemList(order.getUsername());
        List<OrderItem> orderItemList = cartList.stream().filter(cart -> (boolean) cart.get("checked"))
                .map(cart -> (OrderItem) cart.get("item"))
                .collect(Collectors.toList());

        //2.扣减库存
        if(!skuService.deductionStock(orderItemList)){
            throw  new RuntimeException("库存不足！");
        }
        try {
            //3.保存订单主表
            order.setId( idWorker.nextId()+"" );

            IntStream numStream = orderItemList.stream().mapToInt(OrderItem::getNum);
            IntStream moneytStream = orderItemList.stream().mapToInt(OrderItem::getMoney);
            int totalNum = numStream.sum();
            int totalMoney = moneytStream.sum();
            int preMoney = cartService.preferential(order.getUsername());  //满减优惠金额

            order.setTotalNum(totalNum);//总数量
            order.setTotalMoney(totalMoney); //总金额
            order.setPreMoney(preMoney);//优惠金额
            order.setPayMoney( totalMoney-preMoney );//支付金额
            order.setCreateTime(new Date());//订单创建时间
            order.setOrderStatus("0");//订单状态
            order.setPayStatus("0");//支付状态
            order.setConsignStatus("0");//发货状态

            orderMapper.insert(order);


            //4.保存订单明细表
            //打折比例
            double proportion= (double)order.getPayMoney()/totalMoney;

            for(OrderItem orderItem:orderItemList ){
                orderItem.setId(idWorker.nextId()+"" );
                orderItem.setOrderId( order.getId() );
                orderItem.setPayMoney((int)(orderItem.getMoney()*proportion)  );

                orderItemMapper.insert(orderItem);
            }

            //调用延迟消息发送方法
            sendDelayMessage(order.getId());


        } catch (Exception e) {
            e.printStackTrace();
            //发送回滚消息
            rabbitTemplate.convertAndSend("","queue.skuback", JSON.toJSONString(orderItemList));
            throw new RuntimeException("创建订单失败");
        }

        //5.清除购物车
        cartService.deleteCheckedCart( order.getUsername()  );

        Map map=new HashMap();
        map.put("ordersn",order.getId());
        map.put("money",order.getPayMoney());
        return map;
    }

    /**
     * 修改
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    @Autowired
    private OrderLogMapper orderLogMapper;

    @Override
    @Transactional
    public void updatePayStatus(String orderId, String transactionId) {
        System.out.println("调用修改订单状态");
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order!=null && "0".equals( order.getPayStatus() )){
            //修改订单状态等信息
            order.setPayStatus("1");//支付状态
            order.setOrderStatus("1");//订单状态
            order.setUpdateTime(new Date());//修改日期
            order.setPayTime(new Date());//支付日期
            order.setTransactionId(transactionId);//交易流水号
            orderMapper.updateByPrimaryKeySelective(order);//修改

            //记录订单日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId( idWorker.nextId()+"" );
            orderLog.setOperater("system");//系统
            orderLog.setOperateTime(new Date());//操作时间
            orderLog.setOrderStatus("1");//订单状态
            orderLog.setPayStatus("1");//支付状态
            orderLog.setRemarks("支付流水号："+transactionId);//备注
            orderLog.setOrderId(orderId);
            orderLogMapper.insert(orderLog);

        }


    }

    /**
     * 根据用户名查找所有订单及订单详情信息
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderAndItems> findOnesOrderAndItems(String username, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("isDelete","0");
        example.setOrderByClause("create_time desc");
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        List<OrderAndItems> list = new ArrayList<>();
        for (Order order : orders) {
            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId",order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
            OrderAndItems orderAndItems = new OrderAndItems();
            orderAndItems.setOrder(order);
            orderAndItems.setList(orderItems);
            list.add(orderAndItems);
        }
        return new PageResult<OrderAndItems>(orders.getTotal(),list);
    }

    /**
     * 待付款订单
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderAndItems> findOnesWaitToPayOrderAndItems(String username, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("orderStatus","0");
        criteria.andEqualTo("isDelete","0");
        example.setOrderByClause("create_time desc");
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        List<OrderAndItems> list = new ArrayList<>();
        for (Order order : orders) {
            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId",order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
            OrderAndItems orderAndItems = new OrderAndItems();
            orderAndItems.setOrder(order);
            orderAndItems.setList(orderItems);
            list.add(orderAndItems);
        }
        return new PageResult<OrderAndItems>(orders.getTotal(),list);

    }

    /**
     * 代发货订单
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderAndItems> findOnesWaitToSendOrderAndItems(String username, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("orderStatus","1");
        criteria.andEqualTo("isDelete","0");
        example.setOrderByClause("create_time desc");
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        List<OrderAndItems> list = new ArrayList<>();
        for (Order order : orders) {
            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId",order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
            OrderAndItems orderAndItems = new OrderAndItems();
            orderAndItems.setOrder(order);
            orderAndItems.setList(orderItems);
            list.add(orderAndItems);
        }
        return new PageResult<OrderAndItems>(orders.getTotal(),list);
    }

    /**
     * 待收货
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderAndItems> findOnesWaitToReceiveOrderAndItems(String username, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("orderStatus","2");
        criteria.andEqualTo("isDelete","0");
        example.setOrderByClause("create_time desc");
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        List<OrderAndItems> list = new ArrayList<>();
        for (Order order : orders) {
            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId",order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
            OrderAndItems orderAndItems = new OrderAndItems();
            orderAndItems.setOrder(order);
            orderAndItems.setList(orderItems);
            list.add(orderAndItems);
        }
        return new PageResult<OrderAndItems>(orders.getTotal(),list);
    }

    /**
     * 待评价
     * @param username
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<OrderAndItems> findOnesWaitToEvaluateOrderAndItems(String username, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("isDelete","0");
        criteria.andEqualTo("orderStatus","3");
        criteria.andEqualTo("buyerRate","0");
        example.setOrderByClause("create_time desc");
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        List<OrderAndItems> list = new ArrayList<>();
        for (Order order : orders) {
            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId",order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
            OrderAndItems orderAndItems = new OrderAndItems();
            orderAndItems.setOrder(order);
            orderAndItems.setList(orderItems);
            list.add(orderAndItems);
        }
        return new PageResult<OrderAndItems>(orders.getTotal(),list);
    }

    /**
     * 确认收货
     * @param id
     * @return
     */
    @Override
    public Result confirmReceive(String id) {
        try {
            Order order = new Order();
            order.setId(id);
            order.setOrderStatus("3");
            update(order);
            return new Result(0,"收货成功!");
        } catch (Exception e) {
            return new Result(1,"收货失败!");
        }
    }

    /**
     * 根据订单id查询订单和订单详情
     * @param id
     * @return
     */
    @Override
    public OrderAndItems findOrderAndItemsByOrderId(String id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        OrderAndItems orderAndItems = new OrderAndItems();
        orderAndItems.setOrder(order);
        orderAndItems.setList(orderItems);
        return orderAndItems;
    }

    @Override
    public Result removeOrder(String orderId) {
        try {
            Order order = orderMapper.selectByPrimaryKey(orderId);
            order.setIsDelete("1");
            orderMapper.updateByPrimaryKeySelective(order);
            return new Result(0,"订单删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1,"订单删除失败!");
        }
    }


    /*****
     *
     *      订单超时回滚
     *
     * @param orderId
     */
    @Reference
    private WeixinPayService weixinPayService;

    @Override
    public void rollBackOrder(String orderId) {


        // 使用微信查询订单api
        Map<String,String> map = weixinPayService.queryPayStatus(orderId);
        System.out.println("///////orderId"+map.get("return_code"));
        /***
         * 返回状态码	return_code		SUCCESS
         * 业务结果	    result_code		SUCCESS
         * 交易状态	    trade_state     NOTPAY—未支付
         */
        if ("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code") ) && "NOTPAY".equals(map.get("trade_state"))){
            //关闭微信订单
            Map<String,String> closePay = weixinPayService.closePay(orderId);
        }


        /*****
         *
         * 交易状态	    trade_state     CLOSED—已关闭
         *
         */
        if ("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code") ) && "CLOSED".equals(map.get("trade_state"))){
            //修改以关闭的订单状态
            Order order = new Order();
            order.setId(orderId);
            //订单状态为 4 关闭状态
            order.setOrderStatus("4");
            orderMapper.updateByPrimaryKeySelective(order);


            //恢复商品表库存
            Example example = new Example(OrderItem.class);
            example.createCriteria().andEqualTo("orderId",orderId);
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example);


            for (OrderItem orderItem : orderItems) {
                orderItem.setNum(orderItem.getNum());
            }
            skuService.deductionStock(orderItems);


            //记录订单日志
            OrderLog orderLog=new OrderLog();
            orderLog.setId( idWorker.nextId()+"" );
            orderLog.setOperater("system");//系统
            orderLog.setOperateTime(new Date());//操作时间
            orderLog.setOrderStatus("2");//订单状态
            orderLog.setPayStatus("0");//支付状态
            orderLog.setRemarks("支付超时!!");//备注
            orderLog.setConsignStatus("0");//发货状态
            orderLog.setOrderId(orderId);
            orderLogMapper.insert(orderLog);
        }
    }


    /*****
     *
     * 延时消息发送
     * @param orderId
     *
     */
    public void sendDelayMessage(String orderId){
        rabbitTemplate.convertAndSend(
                "exchange.delay.order.begin", "delay", orderId,
                message -> {
                    //   设置有效期
                    message.getMessageProperties().setExpiration(String.valueOf(60000));
                    return message;
                });
    }
    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andLike("payType","%"+searchMap.get("payType")+"%");
            }
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
            }
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
            }
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
            }
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
            }
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
            }
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
            }
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
            }
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andLike("sourceType","%"+searchMap.get("sourceType")+"%");
            }
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
            }
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andLike("orderStatus","%"+searchMap.get("orderStatus")+"%");
            }
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andLike("payStatus","%"+searchMap.get("payStatus")+"%");
            }
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andLike("consignStatus","%"+searchMap.get("consignStatus")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
