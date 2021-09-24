package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.TableFace;
import com.itheima.restkeeper.pojo.Table;
import com.itheima.restkeeper.req.TableVo;
import com.itheima.restkeeper.service.ITableService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description 桌台dubbo接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findTableVoPage",retries = 2),
                @Method(name = "createTable",retries = 0),
                @Method(name = "updateTable",retries = 0),
                @Method(name = "deleteTable",retries = 0)
        })
public class TableFaceImpl implements TableFace {

    @Autowired
    ITableService tableService;
    
    @Override
    public Page<TableVo> findTableVoPage(TableVo tableVo, int pageNum, int pageSize) {
        Page<Table> page = tableService.findTableVoPage(tableVo, pageNum, pageSize);
        Page<TableVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Table> tableList = page.getRecords();
        List<TableVo> tableVoList = BeanConv.toBeanList(tableList,TableVo.class);
        pageVo.setRecords(tableVoList);
        return pageVo;
    }

    @Override
    public TableVo createTable(TableVo tableVo) {
        return BeanConv.toBean( tableService.createTable(tableVo), TableVo.class);
    }

    @Override
    @GlobalTransactional
    public Boolean updateTable(TableVo tableVo) {
        return tableService.updateTable(tableVo);
    }

    @Override
    public Boolean deleteTable(String[] checkedIds) {
        return tableService.deleteTable(checkedIds);
    }

    @Override
    public TableVo findTableByTableId(Long tableId) {
        Table table = tableService.getById(tableId);
        if (!EmptyUtil.isNullOrEmpty(table)){
            return BeanConv.toBean(table,TableVo.class);
        }
        return null;
    }

    @Override
    public List<TableVo> findTableVoList() {
        return BeanConv.toBeanList(tableService.findTableVoList(),TableVo.class);
    }
}
