package com.qingcheng.consumer;

import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.SkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.List;
import java.util.Map;


public class SmsMessageConsumer3 implements MessageListener {

       @Autowired
        private SkuService skuService;

    @Value("${pagePath}")
     private  String pagePath;
    @Override
    public void onMessage(Message message) {
       //下架，静态页面删除
        byte[] body = message.getBody();
        String jsonString = new String(body);

           //查到所有sku
        List<Sku> skus = skuService.findBySpuId(jsonString);
         /*io流的使用*/
          for(Sku sku:skus){
              File file=new File(pagePath,sku.getId()+".html");
              if(file.exists()&&file.isFile()){
                  file.delete();
              }
          }

    }
}
