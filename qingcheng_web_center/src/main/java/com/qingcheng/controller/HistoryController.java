package com.qingcheng.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.user.HistoryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hujialei
 */
@RestController
@RequestMapping("/history")
public class HistoryController {

    @Reference
    private HistoryService historyService;

    @GetMapping("/getHistory")
    public List<Sku> getHistory() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (name != null && !"anonymousUser".equals(name)) {
            return historyService.getHistory(name);
        } else {
            return null;
        }

    }
}
