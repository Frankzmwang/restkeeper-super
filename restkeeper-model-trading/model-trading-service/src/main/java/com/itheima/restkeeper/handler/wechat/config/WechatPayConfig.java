package com.itheima.restkeeper.handler.wechat.config;

import com.alibaba.fastjson.JSONArray;
import com.itheima.restkeeper.constant.TradingConstant;
import com.itheima.restkeeper.handler.wechat.client.WechatPayClient;
import com.itheima.restkeeper.pojo.PayChannel;
import com.itheima.restkeeper.req.OtherConfigVo;
import com.itheima.restkeeper.req.PayChannelVo;
import com.itheima.restkeeper.service.IPayChannelService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WeChatPayConfig.java
 * @Description 微信配置类
 */
public class WechatPayConfig {

    @Autowired
    IPayChannelService payChannelService;

    @Autowired
    RedissonClient redissonClient;


    /***
     * @description 支付配置文件初始化
     */
    @PostConstruct
    public void InitWechatPayConfig() {
        //1、查询所有商户支付配置列表
        List<PayChannel> payChannels = payChannelService.findPayChannelList(TradingConstant.TRADING_CHANNEL_WECHAT_PAY);
        List<PayChannelVo> payChannelVos = BeanConv.toBeanList(payChannels,PayChannelVo.class);
        if (!EmptyUtil.isNullOrEmpty(payChannelVos)){
            payChannelVos.forEach(n->{
                List <OtherConfigVo> otherConfigVos = JSONArray.parseArray(n.getOtherConfig(),OtherConfigVo.class);
                n.setOtherConfigs(otherConfigVos);
                RBucket<PayChannelVo> alipayChannel = redissonClient.getBucket("pay:alipay:channel:"+n.getEnterpriseId());
                alipayChannel.set(n);
            });
        }
    }

    /***
     * @description 初始化或更新配置进入redis
     * @param payChannelVo 配置信息
     * @return
     */
    public WechatPayClient createOrUpdateClient(PayChannelVo payChannelVo) throws UnsupportedEncodingException {
        //1、缓存配置
        RBucket<PayChannelVo> alipayChannel = redissonClient
                .getBucket("pay:alipay:channel:"+payChannelVo.getEnterpriseId());
        alipayChannel.set(payChannelVo);
        List<OtherConfigVo> otherConfigs = payChannelVo.getOtherConfigs();
        //2、转换其他属性为map结构
        Map<String,String> otherConfigMap = new HashMap<>();
        otherConfigs.forEach(n->{
            otherConfigMap.put(n.getConfigKey(),n.getConfigValue());
        });
        return WechatPayClient.builder()
            .appid(payChannelVo.getAppId())
            .mchId(otherConfigMap.get("mchId"))
            .mchSerialNo(otherConfigMap.get("mchSerialNo"))
            .apiV3Key(otherConfigMap.get("apiV3Key"))
            .privateKey(otherConfigMap.get("privateKey"))
            .build();
    }


}
