package com.qingcheng.es;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.pojo.goods.Sku;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.client.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * 初始化ES数据
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath*: applicationContext*.xml")
public class ImportSkuToES {

    @Autowired
    SkuMapper skuMapper;

    @Test
    public void importData() throws Exception{

        //1.连接rest接口
        HttpHost http=new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder restClientBuilder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(restClientBuilder);

        //2.封装请求对象
        BulkRequest bulkRequest=new BulkRequest();

        PageHelper.startPage(1,1000);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();

        int totalPages = skus.getPages();

        for (int i=1;i<=totalPages;i++){

            PageHelper.startPage(i,1000);
            Page<Sku> skuPage = (Page<Sku>) skuMapper.selectAll();

            List<Sku> list = skuPage.getResult();

            IndexRequest indexRequest=new IndexRequest("qcsku","doc","5");
            Map skuMap=new HashMap();
            skuMap.put("name","华为mete20 pro");
            skuMap.put("brandName","华为");
            skuMap.put("categoryName","手机");
            skuMap.put("price",1010221);
            skuMap.put("createTime","2019-05-01");
            skuMap.put("saleNum",101021);
            skuMap.put("commentNum",10102321);
            Map spec=new HashMap();
            spec.put("网络制式","移动4G");
            spec.put("屏幕尺寸","5");
            skuMap.put("spec",spec);
            indexRequest.source(skuMap);

            bulkRequest.add(indexRequest);
            //3.获取执行结果
            //IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = bulkResponse.status().getStatus();
            System.out.println(i+":"+status);

        }


        restHighLevelClient.close();

    }


}
