package com.itheima.restkeeper.service.impl;

import com.itheima.restkeeper.pojo.SmsBlacklist;
import com.itheima.restkeeper.mapper.SmsBlacklistMapper;
import com.itheima.restkeeper.service.ISmsBlacklistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description：黑名单表 服务实现类
 */
@Service
public class SmsBlacklistServiceImpl extends ServiceImpl<SmsBlacklistMapper, SmsBlacklist> implements ISmsBlacklistService {

}
