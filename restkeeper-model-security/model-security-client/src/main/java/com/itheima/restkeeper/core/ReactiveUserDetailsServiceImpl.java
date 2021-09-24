package com.itheima.restkeeper.core;

import com.itheima.restkeeper.UserAdapterFace;
import com.itheima.restkeeper.basic.UserAuth;
import com.itheima.restkeeper.req.UserVo;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * @ClassName ReactiveUserDetailsServiceImpl.java
 * @Description 支持flum的身份类实现ReactiveUserDetailsService接口
 */
@Component("reactiveUserDetailsService")
@Slf4j
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService{

    //调用RPC原创服务
    @DubboReference(version = "${dubbo.application.version}", check = false)
    UserAdapterFace userAdapterFace;

    //验证身份
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        String[] username_enterpriseId = username.split(":");
        if (username_enterpriseId.length!=2){
            log.warn("用户:{}登录信息不完整",username);
            return Mono.empty();
        }
        //查询用户
        UserVo userVo = userAdapterFace.findUserByUsernameAndEnterpriseId(
                username_enterpriseId[0],
                Long.valueOf(username_enterpriseId[1]));
        if (EmptyUtil.isNullOrEmpty(userVo)){
            log.warn("用户:{}不存在",username);
            return Mono.empty();
        }
        UserAuth userAuth = new UserAuth(
                userVo.getUsername(),
                userVo.getPassword(),
                new HashSet<>(),
                userVo.getId(),
                userVo.getShardingId(),
                userVo.getEnterpriseId(),
                userVo.getStoreId(),
                userVo.getJwtToken(),
                userVo.getRealName(),
                userVo.getSex(),
                userVo.getMobil(),
                userVo.getEmail(),
                userVo.getDiscountLimit(),
                userVo.getReduceLimit(),
                userVo.getDuties(),
                userVo.getCreatedTime(),
                userVo.getUpdatedTime()
        );

        return Mono.just(userAuth);
    }



}
