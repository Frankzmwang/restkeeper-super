package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.CategoryFace;
import com.itheima.restkeeper.pojo.Category;
import com.itheima.restkeeper.req.CategoryVo;
import com.itheima.restkeeper.service.ICategoryService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName CategoryFaceImpl.java
 * @Description 菜品分类dubbo服务接口实现
 */
@Slf4j
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
        methods ={
                @Method(name = "findCategoryVoPage",retries = 2),
                @Method(name = "createCategory",retries = 0),
                @Method(name = "updateCategory",retries = 0),
                @Method(name = "deleteCategory",retries = 0)
        })
public class CategoryFaceImpl implements CategoryFace {
    
    @Autowired
    ICategoryService categoryService;


    @Override
    public Page<CategoryVo> findCategoryVoPage(CategoryVo categoryVo, int pageNum, int pageSize) {
        Page<Category> page = categoryService.findCategoryVoPage(categoryVo, pageNum, pageSize);
        Page<CategoryVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //结果集转换
        List<Category> categoryList = page.getRecords();
        List<CategoryVo> categoryVoList = BeanConv.toBeanList(categoryList,CategoryVo.class);
        pageVo.setRecords(categoryVoList);
        return pageVo;
    }

    @Override
    public CategoryVo createCategory(CategoryVo categoryVo) {
        return BeanConv.toBean( categoryService.createCategory(categoryVo), CategoryVo.class);
    }

    @Override
    public Boolean updateCategory(CategoryVo categoryVo) {
        return categoryService.updateCategory(categoryVo);
    }

    @Override
    public Boolean deleteCategory(String[] checkedIds) {
        return categoryService.deleteCategory(checkedIds);
    }

    @Override
    public CategoryVo findCategoryByCategoryId(Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (!EmptyUtil.isNullOrEmpty(category)){
            return BeanConv.toBean(category,CategoryVo.class);
        }
        return null;
    }

    @Override
    public List<CategoryVo> findCategoryVoList() {
        return BeanConv.toBeanList(categoryService.findCategoryVoList(),CategoryVo.class);
    }
}
