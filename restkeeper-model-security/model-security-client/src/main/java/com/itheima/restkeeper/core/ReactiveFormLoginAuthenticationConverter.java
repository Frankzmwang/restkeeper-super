package com.itheima.restkeeper.core;

import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName ReactiveFormLoginAuthenticationConverter.java
 * @Description 自定义表单转换
 */
@Component
public class ReactiveFormLoginAuthenticationConverter implements ServerAuthenticationConverter {

    private String usernameParameter = "username";

    private String passwordParameter = "password";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData().map( data -> {
            String username = data.getFirst(this.usernameParameter);
            String password = data.getFirst(this.passwordParameter);
            String hostName = exchange.getRequest().getURI().getHost();
            if (EmptyUtil.isNullOrEmpty(username)||
                EmptyUtil.isNullOrEmpty(password)||
                EmptyUtil.isNullOrEmpty(hostName)){
                throw  new RuntimeException("用户登陆异常");
            }
            String principal = username+":"+hostName;
            return new UsernamePasswordAuthenticationToken(principal, password);
        });
    }

}
