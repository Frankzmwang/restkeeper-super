package com.itheima.restkeeper.core;

import com.itheima.restkeeper.constant.SecurityCacheConstant;
import com.itheima.restkeeper.req.EnterpriseVo;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @ClassName JwtUserDetailsRepositoryReactiveAuthenticationManager.java
 * @Description 认证管理器
 */
@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    //密码编辑者
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories
            .createDelegatingPasswordEncoder();

    //调度程序
    private Scheduler scheduler = Schedulers.parallel();

    //用户明细信息服务
    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    //redisson客户端
    @Autowired
    RedissonClient redissonClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String principal = authentication.getName();
        String[] principalArray = principal.split(":");
        //域名校验
        RBucket<EnterpriseVo> bucket = redissonClient
                .getBucket(SecurityCacheConstant.INIT_EWEBSITE+principalArray[1]);
        EnterpriseVo enterpriseVo = bucket.get();
        if (EmptyUtil.isNullOrEmpty(enterpriseVo)){
            return  Mono.error(new BadCredentialsException("Invalid hostName"));
        }
        String username_enterpriseId = principalArray[0]+":"+enterpriseVo.getEnterpriseId();
        //校验密码
        final String presentedPassword = (String) authentication.getCredentials();
        return this.userDetailsService.findByUsername(username_enterpriseId)
            .publishOn(this.scheduler)
            //密码比较
            .filter(u -> this.passwordEncoder.matches(presentedPassword, u.getPassword()))
            //失败处理
            .switchIfEmpty(Mono.defer(() ->
                    Mono.error(new BadCredentialsException("Invalid Credentials"))))
            //成功处理
            .map(u ->
                    new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()) );
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    public void setScheduler(Scheduler scheduler) {
        Assert.notNull(scheduler, "scheduler cannot be null");
        this.scheduler = scheduler;
    }

}
