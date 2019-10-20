package com.qingcheng.dao;

import com.qingcheng.pojo.user.Address;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AddressMapper extends Mapper<Address> {


    @Select("SELECT addr.`id`,addr.`username`,addr.`phone`,addr.`address`,addr.`contact`,addr.`is_default` isDefault,addr.`alias`,pro.`province` provinceid,c.`city` cityid,a.`area` areaid " +
            "FROM tb_address addr,tb_provinces pro,tb_cities c,tb_areas a " +
            "WHERE addr.`provinceid`=pro.`provinceid` AND addr.`cityid`=c.`cityid` AND addr.`areaid`=a.`areaid` " +
            "AND addr.`username` = #{username};")
    public List<Address> getOnesAddressList(@Param("username") String username);
}
