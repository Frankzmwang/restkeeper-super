package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.basic.BasicPojo;
import com.itheima.restkeeper.constant.SuperConstant;
import com.itheima.restkeeper.pojo.Category;
import com.itheima.restkeeper.mapper.CategoryMapper;
import com.itheima.restkeeper.req.CategoryVo;
import com.itheima.restkeeper.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：菜品分类业务服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public Page<Category> findCategoryVoPage(CategoryVo categoryVo, int pageNum, int pageSize) {
        Page<Category> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();

        if (!EmptyUtil.isNullOrEmpty(categoryVo.getCategoryType())) {
            queryWrapper.lambda().eq(Category::getCategoryType,categoryVo.getCategoryType());
        }
        if (!EmptyUtil.isNullOrEmpty(categoryVo.getCategoryName())) {
            queryWrapper.lambda().likeRight(Category::getCategoryName,categoryVo.getCategoryName());
        }
        if (!EmptyUtil.isNullOrEmpty(categoryVo.getEnableFlag())) {
            queryWrapper.lambda().eq(Category::getEnableFlag,categoryVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByAsc(Category::getSortNo);
        return page(page, queryWrapper);
    }

    @Override
    public Category createCategory(CategoryVo categoryVo) {
        Category category = BeanConv.toBean(categoryVo, Category.class);
        boolean flag = save(category);
        if (flag){
            return category;
        }
        return null;
    }

    @Override
    public Boolean updateCategory(CategoryVo categoryVo) {
        Category category = BeanConv.toBean(categoryVo, Category.class);
        return updateById(category);
    }

    @Override
    public Boolean deleteCategory(String[] checkedIds) {
        List<String> ids = Arrays.asList(checkedIds);
        List<Long> idsLong = new ArrayList<>();
        ids.forEach(n->{
            idsLong.add(Long.valueOf(n));
        });
        return removeByIds(idsLong);
    }

    @Override
    public List<Category> findCategoryVoList() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BasicPojo::getEnableFlag, SuperConstant.YES);
        return list(queryWrapper);
    }

    @Override
    public List<Category> findCategoryVoByStoreId(Long storeId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Category::getStoreId,storeId)
                .eq(BasicPojo::getEnableFlag, SuperConstant.YES);
        return list(queryWrapper);
    }

}
