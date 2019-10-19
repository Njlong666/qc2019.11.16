package com.itheima.oss.test;

import com.aliyun.oss.OSSClient;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2019/5/29.
 */
public class FileTest {
    /**
     * 测试OSS上传文件
     */
    @Test
    public void testOSSFile() throws IOException{
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAIp54wGjYau9xd";
        String accessKeySecret = "qqUOqL5GLxsmvkcVFvZMVNDQSkvKR1";

        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
/*
       *//* // 上传图片
        InputStream fileinput = new FileInputStream("D:\\workspace-qcb\\qingchengcode\\qingcheng_web_manager\\src\\main\\webapp\\img\\1.jpg");*//*
        ossClient.putObject("qcds", "qc_abc2.jpg", fileinput);
        //https://qcds.oss-cn-beijing.aliyuncs.com/qc_abc1.jpg*/

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
