package com.itheima.restkeeper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.enums.PayChannelEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.pojo.PayChannel;
import com.itheima.restkeeper.mapper.PayChannelMapper;
import com.itheima.restkeeper.req.PayChannelVo;
import com.itheima.restkeeper.req.PayChannelVo;
import com.itheima.restkeeper.service.IPayChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Description： 服务实现类
 */
@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, PayChannel> implements IPayChannelService {


    @Override
    public Page<PayChannel> findPayChannelVoPage(PayChannelVo payChannelVo, int pageNum, int pageSize) {
        Page<PayChannel> page = new Page<>(pageNum,pageSize);
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();

        if (!EmptyUtil.isNullOrEmpty(payChannelVo.getChannelLabel())) {
            queryWrapper.lambda().eq(PayChannel::getChannelLabel,payChannelVo.getChannelLabel());
        }
        if (!EmptyUtil.isNullOrEmpty(payChannelVo.getChannelName())) {
            queryWrapper.lambda().likeRight(PayChannel::getChannelName,payChannelVo.getChannelName());
        }
        if (!EmptyUtil.isNullOrEmpty(payChannelVo.getEnableFlag())) {
            queryWrapper.lambda().eq(PayChannel::getEnableFlag,payChannelVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByAsc(PayChannel::getCreatedTime);
        return page(page, queryWrapper);
    }

    @Override
    public PayChannel createPayChannel(PayChannelVo payChannelVo) {
        PayChannel payChannel = BeanConv.toBean(payChannelVo, PayChannel.class);
        payChannel.setOtherConfig(JSONObject.toJSONString(payChannelVo.getOtherConfigs()));
        boolean flag = save(payChannel);
        if (flag){
            return payChannel;
        }
        return null;
    }

    @Override
    public Boolean updatePayChannel(PayChannelVo payChannelVo) {
        PayChannel payChannel = BeanConv.toBean(payChannelVo, PayChannel.class);
        payChannel.setOtherConfig(JSONObject.toJSONString(payChannelVo.getOtherConfigs()));
        return updateById(payChannel);
    }

    @Override
    public Boolean deletePayChannel(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        return removeByIds(ids);
    }
}
