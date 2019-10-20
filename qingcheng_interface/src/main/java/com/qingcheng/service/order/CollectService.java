package com.qingcheng.service.order;

import java.util.List;
import java.util.Map;

public interface CollectService {

    //查询所有收藏
    List<Map<String,Object>> findCollectList(String username);
    //添加收藏
    List<Map<String,Object>> addCollect(String username, String skuId);
    //删除收藏
    List<Map<String,Object>> deleteCollect(String username,String skuId);
    //根据skuId查询缓存
    List<Map<String,Object>> findBySkuId(String username,String skuId);


}
