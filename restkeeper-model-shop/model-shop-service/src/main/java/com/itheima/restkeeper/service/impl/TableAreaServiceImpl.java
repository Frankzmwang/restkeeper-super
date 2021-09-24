package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.basic.BasicPojo;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.mapper.TableAreaMapper;
import com.itheima.restkeeper.pojo.TableArea;
import com.itheima.restkeeper.req.TableAreaVo;
import com.itheima.restkeeper.service.ITableAreaService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：桌台区域 服务实现类
 */
@Service
public class TableAreaServiceImpl extends ServiceImpl<TableAreaMapper, TableArea> implements ITableAreaService {

    @Override
    public Page<TableArea> findTableAreaVoPage(TableAreaVo tableAreaVo, int pageNum, int pageSize) {
        Page<TableArea> page = new Page<>(pageNum,pageSize);
        QueryWrapper<TableArea> queryWrapper = new QueryWrapper<>();

        if (!EmptyUtil.isNullOrEmpty(tableAreaVo.getAreaName())) {
            queryWrapper.lambda().likeRight(TableArea::getAreaName,tableAreaVo.getAreaName());
        }
        if (!EmptyUtil.isNullOrEmpty(tableAreaVo.getEnableFlag())) {
            queryWrapper.lambda().eq(TableArea::getEnableFlag,tableAreaVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByAsc(TableArea::getSortNo);
        return page(page, queryWrapper);
    }

    @Override
    public TableArea createTableArea(TableAreaVo tableAreaVo) {
        TableArea tableArea = BeanConv.toBean(tableAreaVo, TableArea.class);
        boolean flag = save(tableArea);
        if (flag){
            return tableArea;
        }
        return null;
    }

    @Override
    public Boolean updateTableArea(TableAreaVo tableAreaVo) {
        TableArea tableArea = BeanConv.toBean(tableAreaVo, TableArea.class);
        return updateById(tableArea);
    }

    @Override
    public Boolean deleteTableArea(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        List<Long> idsLong = new ArrayList<>();
        ids.forEach(n->{
            idsLong.add(Long.valueOf(n));
        });
        return removeByIds(idsLong);
    }

    @Override
    public List<TableArea> findTableAreaVoList() {
        QueryWrapper<TableArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BasicPojo::getEnableFlag, SuperConstant.YES);
        return list(queryWrapper);
    }
}
