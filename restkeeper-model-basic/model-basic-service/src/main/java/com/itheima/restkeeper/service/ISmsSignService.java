package com.itheima.restkeeper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.pojo.SmsSign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.restkeeper.req.SmsSignVo;

/**
 * @Description： 签名服务类
 */
public interface ISmsSignService extends IService<SmsSign> {

    /**
     * @Description 签名列表
     * @param smsSignVo 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<SmsSignVo>
     */
    Page<SmsSign> findSmsSignVoPage(SmsSignVo smsSignVo, int pageNum, int pageSize);
}
