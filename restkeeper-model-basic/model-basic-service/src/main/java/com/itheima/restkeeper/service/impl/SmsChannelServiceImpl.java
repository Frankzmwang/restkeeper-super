package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.mapper.SmsChannelMapper;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Description： 服务实现类
 */
@Service
public class SmsChannelServiceImpl extends ServiceImpl<SmsChannelMapper, SmsChannel> implements ISmsChannelService {

    @Override
    public SmsChannel findChannelByChannelLabel(String channelLabel) {
        QueryWrapper<SmsChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SmsChannel::getEnableFlag, SuperConstant.YES)
                .eq(SmsChannel::getChannelLabel,channelLabel);
        return getOne(queryWrapper);
    }

    @Override
    public List<SmsChannel> findChannelInChannelLabel(Set<String> channelLabels) {
        QueryWrapper<SmsChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SmsChannel::getEnableFlag, SuperConstant.YES)
                .in(SmsChannel::getChannelLabel,channelLabels);
        return list(queryWrapper);
    }
}
