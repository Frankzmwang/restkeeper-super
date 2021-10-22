package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.pojo.SmsSign;
import com.itheima.restkeeper.mapper.SmsSignMapper;
import com.itheima.restkeeper.req.SmsSignVo;
import com.itheima.restkeeper.service.ISmsSignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

/**
 * @Description： 签名服务实现类
 */
@Service
public class SmsSignServiceImpl extends ServiceImpl<SmsSignMapper, SmsSign> implements ISmsSignService {

    @Override
    public Page<SmsSign> findSmsSignVoPage(SmsSignVo smsSignVo, int pageNum, int pageSize) {
        //构建分页对象
        Page<SmsSign> page = new Page<>(pageNum,pageSize);
        //构建查询条件
        QueryWrapper<SmsSign> queryWrapper = new QueryWrapper<>();
        //按签名名称查询
        if (!EmptyUtil.isNullOrEmpty(smsSignVo.getSignName())) {
            queryWrapper.lambda().likeRight(SmsSign::getSignName,smsSignVo.getSignName());
        }
        //按签名发送状态查询
        if (!EmptyUtil.isNullOrEmpty(smsSignVo.getAcceptStatus())) {
            queryWrapper.lambda().likeRight(SmsSign::getAcceptStatus,smsSignVo.getAcceptStatus());
        }
        //按签名审核状态查询
        if (!EmptyUtil.isNullOrEmpty(smsSignVo.getAuditStatus())) {
            queryWrapper.lambda().eq(SmsSign::getAuditStatus,smsSignVo.getAuditStatus());
        }
        //按签名状态查询
        if (!EmptyUtil.isNullOrEmpty(smsSignVo.getEnableFlag())) {
            queryWrapper.lambda().eq(SmsSign::getEnableFlag,smsSignVo.getEnableFlag());
        }
        //按创建时间降序
        queryWrapper.lambda().orderByDesc(SmsSign::getCreatedTime);
        //执行分页查询
        return page(page, queryWrapper);
    }
}
