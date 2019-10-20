package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyun.oss.OSSClient;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.user.*;
import com.qingcheng.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: lenovo
 * @Date: 2019/10/18 15:57
 */
@RestController
@RequestMapping("/userCenter")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private ProvincesService provincesService;

    @Reference
    private CitiesService citiesService;

    @Reference
    private AreasService areasService;


    @Reference
    private AddressService addressService;

    @Autowired
    private OSSClient ossClient;

    @GetMapping("/message")
    public User getMyMessage() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(name);
        return user;
    }

    @GetMapping("/provinces")
    public List<Provinces> getProvinces() {
        return provincesService.findAll();
    }

    @GetMapping("/cities")
    public List<Cities> getCitiesByProvinceId(String provinceId) {
        List<Cities> cities = citiesService.getCitiesByProvinceId(provinceId);
        return cities;
    }

    @GetMapping("/areas")
    public List<Areas> getAreasByCityId(String cityId) {
        List<Areas> areas = areasService.getAreasByCityId(cityId);
        return areas;
    }



    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file, String folder){
        String bucketName = "qingcheng-xzx";
        String fileName= folder+"/"+ UUID.randomUUID()+"_"+file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://"+bucketName+"."+ ossClient.getEndpoint().toString().replace("http://","") +"/"+fileName;
    }

    @PostMapping("/updateMsg")
    public Result updateMsg(@RequestBody User user) {

        try {
            userService.update(user);
            return new Result(0,"信息添加修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1,"信息添加修改失败!");
        }
    }

    @GetMapping("/getAddressList")
    public List<Address> getOnesAddressList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.getOnesAddressList(name);
    }

    @GetMapping("/getAddress")
    public Address getAddress(Integer id) {
        return addressService.getAddress(id);
    }

    @GetMapping("/removeAddress")
    public Result removeAddress(Integer id) {
        try {
            addressService.delete(id);
            return new Result(0,"地址删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1,"地址删除失败");
        }
    }

    @PostMapping("/saveAddress")
    public Result saveAddress(@RequestBody Address address) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            address.setUsername(name);
            addressService.add(address);
            return new Result(0,"地址添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1,"地址添加失败!");
        }
    }

    @PostMapping("/updateAddress")
    public Result updateAddress(@RequestBody Address address) {
        try {
            addressService.update(address);
            return new Result(0,"数据修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(1,"数据修改失败!");
        }
    }

    @GetMapping("/setIsDefault")
    public Result setIsDefault(Integer id) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = addressService.setDefault(name, id);
        return result;
    }

    @PostMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> msgMap) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String oldPwd = msgMap.get("oldPwd");
        String newPwd1 = msgMap.get("newPwd1");
        String newPwd2 = msgMap.get("newPwd2");
        User user = userService.findById(name);
        String password = user.getPassword();
        boolean flag = BCrypt.checkpw(oldPwd, password);
        if (flag) {
            //原密码输入正确
            if (newPwd1.equals(newPwd2)) {
                //新密码正确
                //修改数据库
                String gensalt = BCrypt.gensalt();
                String newPassword = BCrypt.hashpw(newPwd1, gensalt);
                user.setPassword(newPassword);
                userService.update(user);
                return new Result(0, "密码修改成功!");
            } else {
                //新密码不一致
                return new Result(1, "新密码不一致!");
            }
        } else {
            //原密码错误
            return new Result(1, "原密码输入错误!");
        }
    }
}
