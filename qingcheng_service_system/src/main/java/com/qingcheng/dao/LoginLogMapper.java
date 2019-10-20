package com.qingcheng.dao;

import com.qingcheng.pojo.system.LoginLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface LoginLogMapper extends Mapper<LoginLog> {

    @Select("select * from tb_login_log where login_name = #{name} order by login_time desc")
    public List<LoginLog> findByNameSort(@Param("name") String name);
}
