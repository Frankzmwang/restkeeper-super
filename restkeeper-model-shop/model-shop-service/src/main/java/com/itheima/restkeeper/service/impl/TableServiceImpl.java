package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.basic.BasicPojo;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.Table;
import com.itheima.restkeeper.mapper.TableMapper;
import com.itheima.restkeeper.req.TableVo;
import com.itheima.restkeeper.service.ITableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：桌台 服务实现类
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {

    @Override
    public Page<Table> findTableVoPage(TableVo tableVo,int pageNum,int pageSize) {
        Page<Table> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Table> queryWrapper = new QueryWrapper<>();
        if (!EmptyUtil.isNullOrEmpty(tableVo.getTableName())) {
            queryWrapper.lambda().likeRight(Table::getTableName,tableVo.getTableName());
        }
        if (!EmptyUtil.isNullOrEmpty(tableVo.getTableSeatNumber())) {
            queryWrapper.lambda().eq(Table::getTableSeatNumber,tableVo.getTableSeatNumber());
        }
        if (!EmptyUtil.isNullOrEmpty(tableVo.getTableStatus())) {
            queryWrapper.lambda().eq(Table::getTableStatus,tableVo.getTableStatus());
        }
        if (!EmptyUtil.isNullOrEmpty(tableVo.getEnableFlag())) {
            queryWrapper.lambda().eq(Table::getEnableFlag,tableVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByAsc(Table::getSortNo);
        return page(page,queryWrapper);
    }

    @Override
    public Table createTable(TableVo tableVo) {
        Table table = BeanConv.toBean(tableVo, Table.class);
        boolean flag = save(table);
        if (flag){
            return table;
        }
        return null;
    }

    @Override
    public Boolean updateTable(TableVo tableVo) {
        Table table = BeanConv.toBean(tableVo, Table.class);
        return updateById(table);
    }

    @Override
    public Boolean deleteTable(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        List<Long> idsLong = new ArrayList<>();
        ids.forEach(n->{
            idsLong.add(Long.valueOf(n));
        });
        return removeByIds(idsLong);
    }

    @Override
    public List<Table> findTableVoList() {
        QueryWrapper<Table> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BasicPojo::getEnableFlag, SuperConstant.YES);
        return list(queryWrapper);
    }

    @Override
    public Boolean openTable(TableVo tableVo) {
        LambdaQueryWrapper<Table> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Table::getTableStatus,SuperConstant.FREE).eq(Table::getId,tableVo.getId());
        return update(BeanConv.toBean(tableVo,Table.class),lambdaQueryWrapper);
    }
}
