package com.itheima.restkeeper.handler.baidu.config;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.http.DefaultRetryPolicy;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AlipayConfig.java
 * @Description 支付宝配置类
 */
@Slf4j
@Configuration
public class BaiduSmsConfig {

    @Autowired
    ISmsChannelService smsChannelService;

    private static Map<String, SmsClient> clientHashMap =new ConcurrentHashMap<>();

    /***
     * @description 初始化短信配置
     */
    @PostConstruct
    public void initSmsConfig() {
        //查询百度云SMS的配置信息
        SmsChannel smsChannel = smsChannelService.findChannelByChannelLabel(SuperConstant.BAIDU_SMS);
        if (EmptyUtil.isNullOrEmpty(smsChannel)){
            log.warn("百度云SMS的未配置");
            return;
        }
        this.createOrUpdateClient(smsChannel);
    }

    public void createOrUpdateClient( SmsChannel smsChannel){
        //百度云配置
        SmsClientConfiguration config = new SmsClientConfiguration()
            //站点
            .withEndpoint(smsChannel.getDomain())
            //百度云密钥
            .withCredentials(new DefaultBceCredentials(smsChannel.getAccessKeyId(), smsChannel.getAccessKeySecret()))
            // 设置HTTP最大连接数为10
            .withMaxConnections(10)
            // 设置TCP连接超时为5000毫秒
            .withConnectionTimeoutInMillis(5000)
            // 设置默重试最大的重试次数为3
            .withRetryPolicy(new DefaultRetryPolicy())
            //设置Socket传输数据超时的时间为2000毫秒
            .withSocketTimeoutInMillis(2000);
        SmsClient client = new SmsClient(config);
        if (clientHashMap.containsKey("baiduSmsClient")){
            clientHashMap.replace("baiduSmsClient",client);
        }else {
            clientHashMap.put("baiduSmsClient",client);
        }
    }

    /***
     * @description 移除配置
     * @return
     */
    public void removeClient(){
        clientHashMap.clear();
    }

    /***
     * @description 中获得配置
     * @return
     */
    public SmsClient queryClient(){
        return clientHashMap.get("baiduSmsClient");
    }


}
