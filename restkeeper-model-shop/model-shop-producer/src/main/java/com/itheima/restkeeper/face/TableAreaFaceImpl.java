package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.TableAreaFace;
import com.itheima.restkeeper.pojo.TableArea;
import com.itheima.restkeeper.req.TableAreaVo;
import com.itheima.restkeeper.service.ITableAreaService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName TableAreaFaceImpl.java
 * @Description 桌台区域dubbo服务
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findTableAreaVoPage",retries = 2),
                @Method(name = "createTableArea",retries = 0),
                @Method(name = "updateTableArea",retries = 0),
                @Method(name = "deleteTableArea",retries = 0)
        })
public class TableAreaFaceImpl implements TableAreaFace {

    @Autowired
    ITableAreaService tableAreaService;

    @Override
    public Page<TableAreaVo> findTableAreaVoPage(TableAreaVo tableAreaVo, int pageNum, int pageSize) {
        Page<TableArea> page = tableAreaService.findTableAreaVoPage(tableAreaVo, pageNum, pageSize);
        Page<TableAreaVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<TableArea> tableAreaList = page.getRecords();
        List<TableAreaVo> tableAreaVoList = BeanConv.toBeanList(tableAreaList,TableAreaVo.class);
        pageVo.setRecords(tableAreaVoList);
        return pageVo;
    }

    @Override
    public TableAreaVo createTableArea(TableAreaVo tableAreaVo) {
        return BeanConv.toBean( tableAreaService.createTableArea(tableAreaVo), TableAreaVo.class);
    }

    @Override
    public Boolean updateTableArea(TableAreaVo tableAreaVo) {
        return tableAreaService.updateTableArea(tableAreaVo);
    }

    @Override
    public Boolean deleteTableArea(String[] checkedIds) {
        return tableAreaService.deleteTableArea(checkedIds);
    }

    @Override
    public TableAreaVo findTableAreaByTableAreaId(Long tableAreaId) {
        TableArea tableArea = tableAreaService.getById(tableAreaId);
        if (!EmptyUtil.isNullOrEmpty(tableArea)){
            return BeanConv.toBean(tableArea,TableAreaVo.class);
        }
        return null;
    }

    @Override
    public List<TableAreaVo> findTableAreaVoList() {
        return BeanConv.toBeanList(tableAreaService.findTableAreaVoList(),TableAreaVo.class);
    }
}
