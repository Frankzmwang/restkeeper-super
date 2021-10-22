package com.itheima.restkeeper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.restkeeper.pojo.SmsChannel;

import java.util.List;
import java.util.Set;

/**
 * @Description： 服务类
 */
public interface ISmsChannelService extends IService<SmsChannel> {

    /***
     * @description 查询通道配置
     * @param channelLabel 通道标识
     * @return 通道
     */
    SmsChannel findChannelByChannelLabel(String channelLabel);

    /***
     * @description 查询通道配置
     * @param channelLabels 通道标识
     * @return 通道
     */
    List<SmsChannel> findChannelInChannelLabel(Set<String> channelLabels);
}
