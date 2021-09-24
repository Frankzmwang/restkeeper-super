package com.itheima.restkeeper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.restkeeper.pojo.Affix;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.UploadMultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description：附件 服务类
 */
public interface IAffixService extends IService<Affix> {

    /**
     * @Description 文件上传
     * @param multipartFile 上传对象
     * @param affixVo 附件对象
     * @return
     */
    Affix upLoad(UploadMultipartFile multipartFile,
                 AffixVo affixVo) throws IOException;

    /**
     * @Description 为上传绑定对应的业务Id
     * @param  affixVo 附件对象
     * @return
     */
    Affix bindBusinessId(AffixVo affixVo);

    /**
     * @Description 按业务ID查询附件
     * @param  businessId 附件对象业务Id
     * @return
     */
    List<Affix> findAffixVoByBusinessId(Long businessId);

    /**
     * @Description 删除业务相关附件
     * @param businessId 业务Id
     * @return
     */
    Boolean deleteAffixVoByBusinessId(Long businessId);

    /**
     * @Description 图片列表
     * @param affixVo 查询条件
     * @return
     */
    Page<Affix> findAffixVoPage(AffixVo affixVo, int pageNum, int pageSize);

    /**
     * @Description 删除图片
     * @param checkedIds 对象信息Id
     * @return
     */
    Boolean deleteAffix(String[] checkedIds);

}
