package com.itheima.restkeeper.service.impl;

import com.itheima.restkeeper.pojo.SmsSendRecord;
import com.itheima.restkeeper.mapper.SmsSendRecordMapper;
import com.itheima.restkeeper.service.ISmsSendRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description：发送记录表 服务实现类
 */
@Service
public class SmsSendRecordServiceImpl extends ServiceImpl<SmsSendRecordMapper, SmsSendRecord> implements ISmsSendRecordService {

}
