package com.itheima.restkeeper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.restkeeper.mapper.AffixMapper;
import com.itheima.restkeeper.pojo.Affix;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.UploadMultipartFile;
import com.itheima.restkeeper.service.FileStorageService;
import com.itheima.restkeeper.service.IAffixService;
import com.itheima.restkeeper.utils.BeanConv;
import com.itheima.restkeeper.utils.EmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：附件 服务实现类
 */
@Service
public class AffixServiceImpl extends ServiceImpl<AffixMapper, Affix> implements IAffixService {

    @Autowired
    IdentifierGenerator identifierGenerator;

    @Autowired
    FileStorageService fileStorageService;

    @Value("${spring.cloud.alicloud.oss.bucket-name}")
    String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    String endpoint;

    @Override
    public Affix upLoad(UploadMultipartFile multipartFile,
                          AffixVo affixVo) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String filename = identifierGenerator.nextId(affixVo)+"-"+originalFilename;
        String pathUrl = fileStorageService.store(affixVo.getBusinessType(), filename,
                new ByteArrayInputStream(multipartFile.getFileByte()));
        Affix affix = Affix.builder()
                .businessType(affixVo.getBusinessType().replace("/", ""))
                .fileName(filename)
                .suffix(originalFilename.substring(originalFilename.lastIndexOf(".")))
                .pathUrl(pathUrl)
                .build();
        save(affix);
        affix.setPathUrl("https://"+bucketName+"."+endpoint+"/"+pathUrl);
        return affix;
    }

    @Override
    public Affix bindBusinessId(AffixVo affixVo) {
        Affix affix = BeanConv.toBean(affixVo, Affix.class);
        boolean flag = updateById(affix);
        if (flag){
            return affix;
        }
        return null;
    }

    @Override
    public List<Affix> findAffixVoByBusinessId(Long businessId) {
        QueryWrapper<Affix> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Affix::getBusinessId,businessId);
        List<Affix> list = list(queryWrapper);
        if (!EmptyUtil.isNullOrEmpty(list)){
            list.forEach(n->{
                n.setPathUrl("https://"+bucketName+"."+endpoint+"/"+n.getPathUrl());
            });
        }
        return list;
    }

    @Override
    public Boolean deleteAffixVoByBusinessId(Long businessId) {
        QueryWrapper<Affix> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Affix::getBusinessId,businessId);
        Affix affix = this.getOne(queryWrapper);
        //删除OSS中的图片
        if (!EmptyUtil.isNullOrEmpty(affix)){
            String pathUrl = affix.getPathUrl();
            List<String> list = new ArrayList<>();
            list.add(pathUrl);
            fileStorageService.deleteBatch(list);
        }
        //删除数据库
        return this.remove(queryWrapper);
    }

    @Override
    public Page<Affix> findAffixVoPage(AffixVo affixVo, int pageNum , int pageSize) {
        Page<Affix> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Affix> queryWrapper = new QueryWrapper<>();

        if (!EmptyUtil.isNullOrEmpty(affixVo.getBusinessType())) {
            queryWrapper.lambda().eq(Affix::getBusinessType,affixVo.getBusinessType());
        }
        if (!EmptyUtil.isNullOrEmpty(affixVo.getPathUrl())) {
            queryWrapper.lambda().likeRight(Affix::getPathUrl,affixVo.getPathUrl());
        }
        if (!EmptyUtil.isNullOrEmpty(affixVo.getEnableFlag())) {
            queryWrapper.lambda().likeRight(Affix::getEnableFlag,affixVo.getEnableFlag());
        }
        queryWrapper.lambda().orderByDesc(Affix::getCreatedTime);
        page(page, queryWrapper);
        List<Affix> affixs = page.getRecords();
        if (!EmptyUtil.isNullOrEmpty(affixs)){
            affixs.forEach(n->{
                String path = n.getPathUrl();
                n.setPathUrl("https://"+bucketName+"."+endpoint+"/"+path);
            });
        }
        page.setRecords(affixs);
        return page;
    }

    @Override
    public Boolean deleteAffix(String[] checkedIds) {
        for (String checkedId : checkedIds) {
            Affix affix = getById(checkedIds);
            //删除OSS中的图片
            if (!EmptyUtil.isNullOrEmpty(affix)){
                String pathUrl = affix.getPathUrl();
                List<String> list = new ArrayList<>();
                list.add(pathUrl);
                fileStorageService.deleteBatch(list);
            }
        }
        //删除数据库
        return this.removeByIds(Arrays.asList(checkedIds));
    }
}
