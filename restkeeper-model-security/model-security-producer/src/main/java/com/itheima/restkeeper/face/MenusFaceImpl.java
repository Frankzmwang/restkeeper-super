package com.itheima.restkeeper.face;

import com.itheima.restkeeper.MenusFace;
import com.itheima.restkeeper.req.MenuVo;
import com.itheima.restkeeper.service.IMenusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName MenusFaceImpl.java
 * @Description 菜单服务实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",retries = 2,timeout = 5000)
public class MenusFaceImpl implements MenusFace {

    @Autowired
    IMenusService menusService;
    @Override
    public List<MenuVo> findMenusBySystemCode(String systemCode) {
        return menusService.findMenusBySystemCode(systemCode);
    }
}
