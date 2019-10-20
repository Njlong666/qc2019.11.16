package com.qingcheng.consumer;

import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.SkuService;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmsMessageConsumer2 implements MessageListener {
           @Autowired
           private SkuService skuService;
     public void onMessage(Message message) {
         try {
             byte[] bytes = message.getBody();
             String jsonString = new String(bytes);

             List<Sku> skus = skuService.findBySpuId(jsonString);
             //更新索引库
             //1.连接rest接口
             HttpHost http=new HttpHost("127.0.0.1",9200,"http");
             RestClientBuilder restClientBuilder = RestClient.builder(http);
             RestHighLevelClient restHighLevelClient=new RestHighLevelClient(restClientBuilder);

             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             BulkRequest bulkRequest=new BulkRequest();
             //添加sku
             for (Sku sku:skus) {
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
             BulkResponse bulkResponse = null;
             try {
                 bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                 int status = bulkResponse.status().getStatus();
                 System.out.println(status);
                 restHighLevelClient.close();
             } catch (IOException e) {
             }
         } catch (ElasticsearchGenerationException e) {

         }

    }
}
