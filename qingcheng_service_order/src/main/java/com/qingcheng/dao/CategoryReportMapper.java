package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author: lenovo
 * @Date: 2019/9/20 21:25
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {

    @Select("select oi.`category_id1` categoryId1,oi.`category_id2` categoryId2,oi.`category_id3` categoryId3,DATE_FORMAT(o.`pay_time`,'%Y-%m-%d') countDate,sum(oi.`num`) num,sum(oi.`money`) money " +
            "from tb_order o,tb_order_item oi " +
            "where " +
            "o.id = oi.`order_id` " +
            "and o.`pay_status` = '1' " +
            "and o.`is_delete` = '0' "  +
            "and date_format(o.`pay_time`,'%Y-%m-%d') = #{date} " +
            "group by oi.`category_id1`,oi.`category_id2`,oi.`category_id2`")
    public List<CategoryReport> categoryReport(@Param("date") LocalDate localDate);


    @Select("SELECT category_id1 categoryId1 , c.`name` categoryName, SUM(num) num , SUM(money) money " +
            "FROM tb_category_report cr , v_category1 c " +
            "WHERE cr.`category_id1` = c.id AND cr.count_date >= #{date1} AND cr.count_date <= #{date2} " +
            "GROUP BY category_id1")
    public List<Map<String,Object>> getCategoryId1Report(@Param("date1") String date1, @Param("date2") String date2);
}
