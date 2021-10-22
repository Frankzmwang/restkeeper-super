package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.basic.BasicPojo;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.SmsChannel;
import com.itheima.restkeeper.mapper.SmsChannelMapper;
import com.itheima.restkeeper.req.SmsChannelVo;
import com.itheima.restkeeper.service.ISmsChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public Page<SmsChannel> findSmsChannelVoPage(SmsChannelVo smsChannelVo, int pageNum, int pageSize) {
        Page<SmsChannel> page = new Page<>(pageNum,pageSize);
        QueryWrapper<SmsChannel> queryWrapper = new QueryWrapper<>();

        if (!EmptyUtil.isNullOrEmpty(smsChannelVo.getChannelLabel())) {
            queryWrapper.lambda().eq(SmsChannel::getChannelLabel,smsChannelVo.getChannelLabel());
        }
        if (!EmptyUtil.isNullOrEmpty(smsChannelVo.getChannelName())) {
            queryWrapper.lambda().likeRight(SmsChannel::getChannelName,smsChannelVo.getChannelName());
        }
        if (!EmptyUtil.isNullOrEmpty(smsChannelVo.getEnableFlag())) {
            queryWrapper.lambda().eq(SmsChannel::getEnableFlag,smsChannelVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByAsc(SmsChannel::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    public SmsChannel createSmsChannel(SmsChannelVo smsChannelVo) {
        SmsChannel smsChannel = BeanConv.toBean(smsChannelVo, SmsChannel.class);
        boolean flag = save(smsChannel);
        if (flag){
            return smsChannel;
        }
        return null;
    }

    @Override
    public Boolean updateSmsChannel(SmsChannelVo smsChannelVo) {
        SmsChannel smsChannel = BeanConv.toBean(smsChannelVo, SmsChannel.class);
        return updateById(smsChannel);
    }

    @Override
    public Boolean deleteSmsChannel(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        List<Long> idsLong = new ArrayList<>();
        ids.forEach(n->{
            idsLong.add(Long.valueOf(n));
        });
        return removeByIds(idsLong);
    }

    @Override
    public List<SmsChannel> findSmsChannelVoList() {
        QueryWrapper<SmsChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BasicPojo::getEnableFlag, SuperConstant.YES);
        return list(queryWrapper);
    }

}
