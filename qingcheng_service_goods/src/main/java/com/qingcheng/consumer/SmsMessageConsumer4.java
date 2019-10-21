package com.qingcheng.consumer;

import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.SkuService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.util.List;


public class SmsMessageConsumer4 implements MessageListener {
       //
   @Autowired
    private SkuService skuService;
    public void onMessage(Message message) {
        try {
            byte[] body = message.getBody();
            String jsonString = new String(body);
            List<Sku> skus = skuService.findBySpuId(jsonString);
            //1.连接rest接口
            HttpHost http=new HttpHost("127.0.0.1",9200,"http");
            RestClientBuilder restClientBuilder = RestClient.builder(http);
            RestHighLevelClient restHighLevelClient=new RestHighLevelClient(restClientBuilder);
            //查到所有sku

            BulkRequest bulkRequest=new BulkRequest();
            for(Sku sku:skus){
                DeleteRequest deleteRequest=new DeleteRequest("qingchengsku2","doc",sku.getId());
                      bulkRequest.add(deleteRequest);
            }
            try {
                BulkResponse  bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                int status = bulkResponse.status().getStatus();
                System.out.println(":"+status);
            } catch (IOException e) {

            }
        } catch (Exception e) {

        }

    }
}
