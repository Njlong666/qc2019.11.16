package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyun.oss.OSSClient;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.*;
import com.qingcheng.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
}
