package com.itheima.restkeeper.handler.alipay.config;

import com.alipay.easysdk.kernel.Config;
import com.itheima.restkeeper.constant.TradingCacheConstant;
import com.itheima.restkeeper.pojo.TradingSetting;
import com.itheima.restkeeper.service.ITradingSettingService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @ClassName AlipayConfig.java
 * @Description 支付宝配置类
 */
@Configuration
@Slf4j
public class AlipayConfig {

    @Autowired
    ITradingSettingService tradingSettingService;

    @Autowired
    RedissonClient redissonClient;

    /***
     * @description 支付配置文件初始化
     */
    @PostConstruct
    public void InitAliPayConfig() {
        //1、查询所有商户支付配置列表
        List<TradingSetting> tradingSettingList = tradingSettingService.findTradingSettingList();
        //2、循环注册到redis中
        for (TradingSetting tradingSetting : tradingSettingList) {
            createOrUpdateConfigToRedis(tradingSetting);
        }
    }

    /***
     * @description 初始化或更新配置进入redis
     * @param tradingSetting 配置信息
     * @return
     */
    public void createOrUpdateConfigToRedis(TradingSetting tradingSetting){
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";
        config.appId = tradingSetting.getAlipayAppid();
        //2.1、配置应用私钥
        config.merchantPrivateKey = tradingSetting.getAlipayMerchantPrivateKey();
        //2.2、配置支付宝公钥
        config.alipayPublicKey = tradingSetting.getAlipayPublicKey();
        //2.3、可设置异步通知接收服务地址（可选）
        //config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";
        //2.4、设置AES密钥，调用AES加解密相关接口时需要（可选）
        config.encryptKey = tradingSetting.getAlipayEncryptKey();
        //2.5、配置信息放入redis中
        String key = TradingCacheConstant.ALI_PAY + tradingSetting.getEnterpriseId();
        RBucket<Config> bucket = redissonClient.getBucket(key);
        bucket.set(config);
    }

    /***
     * @description 从redis中删除配置信息
     * @param tradingSetting 配置信息
     * @return
     */
    public void removeConfigToRedis(TradingSetting tradingSetting){
        //2.5、配置信息从redis中移除
        String key = TradingCacheConstant.ALI_PAY + tradingSetting.getEnterpriseId();
        RBucket<Config> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }
}
