package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {



    @Select("SELECT b.`name`,b.`image` FROM tb_brand b " +
            "INNER JOIN tb_category_brand cb ON b.`id` = cb.`brand_id` " +
            "INNER JOIN tb_category c ON cb.`category_id` = c.`id` " +
            "WHERE c.`name` = #{name} ORDER BY b.seq")
    public List<Map> findListByCategoryName(@Param("name") String categoryName);
}
