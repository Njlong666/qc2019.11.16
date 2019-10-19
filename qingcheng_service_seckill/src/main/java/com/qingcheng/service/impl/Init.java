package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.util.DateUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

//@Component
public class Init implements InitializingBean {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Reference
    private SkuService skuService;

    public void afterPropertiesSet() throws Exception {
        System.out.println("---初始化秒杀商品信息----");

        Random random=new Random();

        List<Date> dateMenus = DateUtil.getDateMenus2();

        for (Date startTime : dateMenus) {

            List<Sku> list = skuService.getRoundRows(random.nextInt(80));

            for (Sku sku:list){
                SeckillGoods goods = new SeckillGoods();
                goods.setGoodsId(Long.parseLong(sku.getSpuId()));
                goods.setItemId(Long.parseLong(sku.getId()));
                goods.setTitle(sku.getName());
                goods.setSmallPic(sku.getImage());
                goods.setPrice(new BigDecimal(sku.getPrice()/100));
                goods.setCostPrice(new BigDecimal(sku.getPrice()/200));
                goods.setStatus("1");

                goods.setNum(100);
                goods.setStockCount(random.nextInt(80));

                goods.setCreateTime(new Date());
                goods.setCheckTime(new Date());

                goods.setStartTime(startTime);
                goods.setEndTime(DateUtil.addDateMinutes(startTime,119));

                seckillGoodsMapper.insert(goods);

            }


        }

    }
}
