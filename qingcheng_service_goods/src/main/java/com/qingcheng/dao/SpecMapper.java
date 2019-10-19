package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {


    @Select("SELECT DISTINCT s.`name`,s.`options` FROM tb_spec s " +
            "INNER JOIN tb_category c ON  s.`template_id` = c.`template_id` " +
            "WHERE c.`name` = #{name} ORDER BY s.`seq`")
    public List<Map> findListByCategoryName(@Param("name") String categoryName);

}
