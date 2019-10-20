package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.CollectService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartService cartService;

    /**
     * 查询收藏列表
     *
     * @param username 用户名
     * @return 收藏列表
     */

    @Override
    public List<Map<String, Object>> findCollectList(String username) {

        System.out.println("从redis中提取我的收藏" + username);
        List<Map<String, Object>> collectList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).get(username);
        if (collectList == null) {
            collectList = new ArrayList<>();
        }
        return collectList;
    }

    /**
     * 查询收藏列表
     *
     * @param username 用户名
     * @param skuId    用户所收藏的商品id
     * @return 收藏列表
     */
    @Override
    public List<Map<String, Object>> addCollect(String username, String skuId) {
        List<Map<String, Object>> cartList = cartService.findCartList(username);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Boolean flag=true;
        if (cartList == null) {
            resultList = new ArrayList<>();
        } else {
            resultList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).get(username);
            if (resultList == null) {
                resultList = new ArrayList<>();
            }

            for (Map<String, Object> map : resultList) {
                OrderItem item =(OrderItem) map.get("item");
                if (item.getSkuId().equals(skuId)){
                    flag=false;
                    break;
                }
            }
          if (flag){
              for (Map<String, Object> map : cartList) {
                  OrderItem item = (OrderItem) map.get("item");
                  if (item.getSkuId().equals(skuId)) {
                      resultList.add(map);
                      redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).put(username, resultList);
                      break;
                  }
              }
          }

        }
            return resultList;
        }

    /**
     * 删除查询列表
     * @param username 用户名
     * @param skuId    用户所收藏的商品id
     * @return 收藏列表
     */
    @Override
    public List<Map<String, Object>> deleteCollect(String username, String skuId) {

        List<Map<String, Object>>  resultList=(List<Map<String, Object>>)redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).get(username);
        if (resultList==null){
            resultList = new ArrayList<>();
        }else {
            for (Map<String, Object> map : resultList) {
                OrderItem item = (OrderItem) map.get("item");
                if (item.getSkuId().equals(skuId)){
                   resultList.remove(map);
                    break;
                }
            }
        }
        redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).put(username, resultList);

          return resultList ;
    }

    /**
     * 删除查询列表
     * @param username 用户名
     * @param skuId    用户所收藏的商品id
     * @return 收藏列表
     */
    @Override
    public List<Map<String, Object>> findBySkuId(String username, String skuId) {
        List<Map<String, Object>> resultList = (List<Map<String, Object>>)redisTemplate.boundHashOps(CacheKey.COLLECT_LIST).get(username);
         List findSkuList=new ArrayList();
        if (resultList==null){
            resultList=new ArrayList<>();
        }
        for (Map<String, Object> map : resultList) {
            OrderItem item =(OrderItem) map.get("item");
            if (item.getSkuId().equals(skuId)){
                findSkuList.add(map);
            }

        }
        return findSkuList;
    }


}

