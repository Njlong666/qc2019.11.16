package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.CollectService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Reference
    private CollectService collectService;

    @GetMapping("/addCollect")
    public List<Map<String, Object>> addCollect(String skuId){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> collectList = collectService.addCollect(username, skuId);
        return collectList;
    }
    @GetMapping("/findCollectList")
    public List<Map<String, Object>> findCollectList(){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> collectList = collectService.findCollectList(username);
        return collectList;
    }
    @GetMapping("/deleteCollect")
    public List<Map<String,Object>> deleteCollect(String skuId){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> collectList = collectService.deleteCollect(username, skuId);
        return collectList;
    }
    @GetMapping("/findBySkuId")
    public List<Map<String,Object>> findBySkuId(String skuId){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> skuList = collectService.findBySkuId(username,skuId);
        return skuList;
    }
}
