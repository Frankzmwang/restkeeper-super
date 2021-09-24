package com.itheima.restkeeper.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Description：
 */
@Slf4j
@Component
public class RegisterBeanHandler {

    ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    public RegisterBeanHandler(ConfigurableApplicationContext configurableApplicationContext) {
        this.configurableApplicationContext = configurableApplicationContext;
    }

    public <T> boolean registerBean(String beanName, T bean) {
        // 将bean对象注册到bean工厂
        configurableApplicationContext.getBeanFactory().registerSingleton(beanName, bean);
        return true;
    }

    public <T> T getBean(String beanName, Class<T> t) {
        return configurableApplicationContext.getBean(beanName,t);
    }

    public boolean containsBean(String beanName) {
        return configurableApplicationContext.containsBean(beanName);
    }
}
