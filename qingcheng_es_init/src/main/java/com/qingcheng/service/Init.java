package com.qingcheng.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Init implements InitializingBean {

    @Autowired
    private SkuMapper skuMapper;


    public void afterPropertiesSet() throws Exception {
        System.out.println("---初始化ES----");

        //1.连接rest接口
        HttpHost http=new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder restClientBuilder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(restClientBuilder);



        PageHelper.startPage(1,5000);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();

        int totalPages = skus.getPages();

        //分页
        for (int i=1;i<=totalPages;i++){
            //2.封装请求对象
            BulkRequest bulkRequest=new BulkRequest();

            PageHelper.startPage(i,5000);
            Page<Sku> skuPage = (Page<Sku>) skuMapper.selectAll();

            List<Sku> list = skuPage.getResult();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //添加SKU
            for (Sku sku:list) {
                IndexRequest indexRequest=new IndexRequest("qingchengsku2","doc",sku.getId());
                Map skuMap=new HashMap();
                skuMap.put("name",sku.getName());
                skuMap.put("brandName",sku.getBrandName());
                skuMap.put("categoryName",sku.getCategoryName());
                skuMap.put("price",sku.getPrice());
                skuMap.put("image",sku.getImage());

                if(sku.getCreateTime()!=null){
                    skuMap.put("createTime",sdf.format(sku.getCreateTime()));
                }

                skuMap.put("saleNum",sku.getSaleNum());
                skuMap.put("commentNum",sku.getCommentNum());

                String specStr = sku.getSpec();

                Map spec = JSON.parseObject(specStr);

                skuMap.put("spec",spec);

                indexRequest.source(skuMap);

                bulkRequest.add(indexRequest);
            }

            //3.获取执行结果
            //IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = bulkResponse.status().getStatus();
            System.out.println(i+":"+status);

        }

        restHighLevelClient.close();
    }
}
