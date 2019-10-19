package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lenovo
 * @Date: 2019/10/18 15:57
 */
@RestController
@RequestMapping("/userCenter")
public class UserController {

    @Reference
    private UserService userService;

    @GetMapping("/message")
    public User getMyMessage() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(name);
        return user;
    }
}
