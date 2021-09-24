package com.itheima.restkeeper.face;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.BrandFace;
import com.itheima.restkeeper.pojo.Brand;
import com.itheima.restkeeper.req.BrandVo;
import com.itheima.restkeeper.service.IBrandService;
import com.itheima.restkeeper.utils.BeanConv;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName BrandFaceImpl.java
 * @Description 品牌dubbo接口定义实现
 */
@DubboService(version = "${dubbo.application.version}",timeout = 5000,
    methods ={
        @Method(name = "findBrandVoPage",retries = 2),
        @Method(name = "createBrand",retries = 0),
        @Method(name = "updateBrand",retries = 0),
        @Method(name = "deleteBrand",retries = 0)
    })
public class BrandFaceImpl implements BrandFace {
    
    @Autowired
    IBrandService brandService;

    @Override
    public Page<BrandVo> findBrandVoPage(BrandVo brandVo, int pageNum, int pageSize) {
        //查询Page<Brand>图片分页
        Page<Brand> page = brandService.findBrandVoPage(brandVo, pageNum, pageSize);
        //转化Page<Brand>为Page<BrandVo>
        Page<BrandVo> pageVo = new Page<>();
        BeanConv.toBean(page,pageVo);
        //转换List<Brand>为 List<BrandVo>
        List<Brand> brandList = page.getRecords();
        List<BrandVo> brandVoList = BeanConv.toBeanList(brandList,BrandVo.class);
        pageVo.setRecords(brandVoList);
        //返回结果
        return pageVo;
    }

    @Override
    public BrandVo createBrand(BrandVo brandVo) {
        return BeanConv.toBean( brandService.createBrand(brandVo), BrandVo.class);
    }

    @Override
    public Boolean updateBrand(BrandVo brandVo) {
        return brandService.updateBrand(brandVo);
    }

    @Override
    public Boolean deleteBrand(String[] checkedIds) {
        return brandService.deleteBrand(checkedIds);
    }

    @Override
    public BrandVo findBrandByBrandId(Long brandId) {
        Brand brand = brandService.getById(brandId);
        return BeanConv.toBean(brand,BrandVo.class);
    }

    @Override
    public List<BrandVo> findBrandVoList() {
        return BeanConv.toBeanList(brandService.findBrandVoList(),BrandVo.class);
    }
}
