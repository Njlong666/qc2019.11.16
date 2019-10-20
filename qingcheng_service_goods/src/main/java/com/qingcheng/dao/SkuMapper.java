package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    /**
     * 更新库存
     *
     * @param skuId
     * @param num
     */
    @Select("update tb_sku set num = num - #{num} where id = #{skuId}")
    public void deductionStock(@Param("skuId") String skuId, @Param("num") Integer num);

    /**
     * 更新销量
     *
     * @param skuId
     * @param num
     */
    @Select("update tb_sku set sale_num = sale_num + #{num} where id = #{skuId}")
    public void addSaleNum(@Param("skuId") String skuId, @Param("num") Integer num);
}
