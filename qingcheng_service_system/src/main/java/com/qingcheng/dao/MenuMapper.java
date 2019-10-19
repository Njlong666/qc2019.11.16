package com.qingcheng.dao;

import com.qingcheng.pojo.system.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {


    @Select(" SELECT * FROM tb_menu WHERE id IN( " +
            " SELECT parent_id FROM tb_menu WHERE id IN( " +
            " SELECT  m.`parent_id` FROM tb_menu m " +
            " INNER JOIN tb_resource_menu rm ON m.id = rm.`menu_id` " +
            " INNER JOIN tb_resource r ON rm.`resource_id` = r.`id` " +
            " INNER JOIN tb_role_resource rr ON r.`id` =  rr.`resource_id` " +
            " INNER JOIN tb_role ON rr.`role_id` = tb_role.`id` " +
            " INNER JOIN tb_admin_role ar ON tb_role.`id` = ar.`role_id` " +
            " INNER JOIN tb_admin a ON ar.`admin_id` = a.`id` " +
            " WHERE a.`login_name` = #{name} " +
            " ) " +
            ") " +
            " UNION " +
            " SELECT * FROM tb_menu WHERE id IN( " +
            " SELECT  m.`parent_id` FROM tb_menu m " +
            " INNER JOIN tb_resource_menu rm ON m.id = rm.`menu_id` " +
            " INNER JOIN tb_resource r ON rm.`resource_id` = r.`id` " +
            " INNER JOIN tb_role_resource rr ON r.`id` =  rr.`resource_id` " +
            " INNER JOIN tb_role ON rr.`role_id` = tb_role.`id` " +
            " INNER JOIN tb_admin_role ar ON tb_role.`id` = ar.`role_id` " +
            " INNER JOIN tb_admin a ON ar.`admin_id` = a.`id` " +
            " WHERE a.`login_name` = #{name} " +
            ") " +
            " UNION " +
            " SELECT  * FROM tb_menu m WHERE id IN( " +
            " SELECT rm.`menu_id` FROM tb_resource_menu rm " +
            " INNER JOIN tb_resource r ON rm.`resource_id` = r.`id` " +
            " INNER JOIN tb_role_resource rr ON r.`id` =  rr.`resource_id` " +
            " INNER JOIN tb_role ON rr.`role_id` = tb_role.`id` " +
            " INNER JOIN tb_admin_role ar ON tb_role.`id` = ar.`role_id` " +
            " INNER JOIN tb_admin a ON ar.`admin_id` = a.`id` " +
            " WHERE a.`login_name` = #{name} " +
            ")")
    public List<Menu> getMenuByAdminName(@Param("name") String name);

}
