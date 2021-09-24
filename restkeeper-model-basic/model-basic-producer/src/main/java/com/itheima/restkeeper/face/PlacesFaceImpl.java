package com.itheima.restkeeper.face;

import com.itheima.restkeeper.PlacesFace;
import com.itheima.restkeeper.req.PlacesVo;
import com.itheima.restkeeper.service.IPlacesService;
import com.itheima.restkeeper.utils.BeanConv;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName PlacesFaceImpl.java
 * @Description 地方表 服务类
 */
@DubboService(version = "${dubbo.application.version}",retries = 0,timeout = 5000)
public class PlacesFaceImpl implements PlacesFace {

    @Autowired
    IPlacesService placesService;

    @Override
    public List<PlacesVo> findPlacesVoListByParentId(Long parentId) {
        return BeanConv.toBeanList(placesService.
                findPlacesVoListByParentId(parentId),PlacesVo.class);
    }
}
