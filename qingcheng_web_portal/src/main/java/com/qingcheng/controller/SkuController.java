package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.user.History;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.user.HistoryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {

    @Reference
    private SkuService skuService;

    @Reference
    private HistoryService historyService;

    @GetMapping("/price")
    public Integer price(String id) {
        try {
//        获取登录用户名
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
            String name = "18355369501";
            if (name != null && !"anonymousUser".equals(name)) {
                historyService.addHistory(new History(name, id));
            }
        } finally {
            return skuService.findPrice(id);
        }

    }

}
