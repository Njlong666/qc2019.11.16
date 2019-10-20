package com.qingcheng.service.user;

import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.user.History;

import java.util.List;

/**
 * @author hujialei
 */
public interface HistoryService {

    /**
     * 添加浏览记录
     *
     * @param history 对象
     */
    void addHistory(History history);

    /**
     *  根据用户名查所有的浏览记录
     * @param name  用户名
     * @return  集合
     */
    List<Sku> getHistory(String name);

}
