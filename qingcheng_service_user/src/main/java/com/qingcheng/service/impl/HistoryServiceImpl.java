package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.HistoryMapper;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.user.History;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.user.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hujialei
 */
@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Reference
    private SkuService skuService;

    @Override
    public void addHistory(History history) {
        historyMapper.insertSelective(history);
    }

    @Override
    public List<Sku> getHistory(String name) {
//        创建条件
        Example example = new Example(History.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", name);
//        查询
        List<History> historyList = historyMapper.selectByExample(example);

        List<Sku> skuList = new ArrayList<>();
        for (History history : historyList) {
            Sku sku = skuService.findById(history.getId());
            skuList.add(sku);
        }

        return skuList;
    }
}
