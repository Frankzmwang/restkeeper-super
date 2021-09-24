package com.itheima.restkeeper.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.restkeeper.AffixFace;
import com.itheima.restkeeper.UserAdapterFace;
import com.itheima.restkeeper.UserFace;
import com.itheima.restkeeper.basic.ResponseWrap;
import com.itheima.restkeeper.enums.UserEnum;
import com.itheima.restkeeper.exception.ProjectException;
import com.itheima.restkeeper.req.AffixVo;
import com.itheima.restkeeper.req.ResourceVo;
import com.itheima.restkeeper.req.RoleVo;
import com.itheima.restkeeper.req.UserVo;
import com.itheima.restkeeper.utils.EmptyUtil;
import com.itheima.restkeeper.utils.ExceptionsUtil;
import com.itheima.restkeeper.utils.ResponseWrapBuild;
import com.itheima.restkeeper.utils.UserVoContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName UserController.java
 * @Description 用户Controller
 */
@RestController
@RequestMapping("user")
@Slf4j
@Api(tags = "用户controller")
public class UserController {

    @DubboReference(version = "${dubbo.application.version}",check = false)
    UserFace userFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    UserAdapterFace userAdapterFace;

    @DubboReference(version = "${dubbo.application.version}",check = false)
    AffixFace affixFace;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping(value = "login")
    @ApiOperation(value = "用户登陆",notes = "用户登陆")
    void loginUser(String username,
                   String password,
                   String usertype) throws ProjectException {
        log.info("执行登录");
    }

    @PostMapping(value = "logout")
    @ApiOperation(value = "用户退出",notes = "用户退出")
    ResponseWrap<Boolean> logoutUser() throws ProjectException {
        return ResponseWrapBuild.build(UserEnum.LOGOUT_SUCCEED,true);
    }

    /**
     * @Description 用户列表
     * @param userVo 查询条件
     * @return
     */
    @PostMapping("page/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询用户分页",notes = "查询用户分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userVo",value = "用户查询对象",required = false,dataType = "UserVo"),
            @ApiImplicitParam(paramType = "path",name = "pageNum",value = "页码",example = "1",dataType = "Integer"),
            @ApiImplicitParam(paramType = "path",name = "pageSize",value = "每页条数",example = "10",dataType = "Integer")
    })
    public ResponseWrap<Page<UserVo>> findUserVoPage(
            @RequestBody UserVo userVo,
            @PathVariable("pageNum") int pageNum,
            @PathVariable("pageSize") int pageSize) throws ProjectException {
        try {
            Page<UserVo> userVoPage = userFace.findUserVoPage(userVo, pageNum, pageSize);
            //处理附件
            if (!EmptyUtil.isNullOrEmpty(userVoPage)){
                List<UserVo> userVoList = userVoPage.getRecords();
                userVoList.forEach(n->{
                    List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(n.getId());
                    if (!EmptyUtil.isNullOrEmpty(affixVoList)){
                        n.setAffixVo(affixVoList.get(0));
                    }
                });
                userVoPage.setRecords(userVoList);
            }
            return ResponseWrapBuild.build(UserEnum.SUCCEED,userVoPage);
        } catch (Exception e) {
            log.error("查询用户列表异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.PAGE_FAIL);
        }
    }

    /**
     * @Description 注册用户
     * @param userVo 对象信息
     * @return
     */
    @PostMapping
    @ApiOperation(value = "注册用户",notes = "注册用户")
    @ApiImplicitParam(name = "userVo",value = "用户对象",required = true,dataType = "UserVo")
    ResponseWrap<UserVo> registerUser(@RequestBody UserVo userVo) throws ProjectException {
        try {
            String plainPassword = userVo.getPassword();
            //必须要加{bcrypt}要不认证不通过
            String password = "{bcrypt}"+bCryptPasswordEncoder.encode(plainPassword);
            userVo.setPassword(password);
            UserVo userVoResult = userFace.createUser(userVo);
            //绑定附件
            if (!EmptyUtil.isNullOrEmpty(userVoResult)){
                affixFace.bindBusinessId(AffixVo.builder().businessId(userVoResult.getId()).id(userVo.getAffixVo().getId()).build());
            }
            userVoResult.setAffixVo(AffixVo.builder()
                    .pathUrl(userVo.getAffixVo().getPathUrl())
                    .businessId(userVoResult.getId())
                    .id(userVo.getAffixVo().getId()).build());
            //处理角色
            userVoResult.setHasRoleIds(userVo.getHasRoleIds());
            return ResponseWrapBuild.build(UserEnum.SUCCEED,userVoResult);
        } catch (Exception e) {
            log.error("保存用户异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.CREATE_FAIL);
        }
    }

    /**
     * @Description 修改用户
     * @param userVo 对象信息
     * @return
     */
    @PatchMapping
    @ApiOperation(value = "修改用户",notes = "修改用户")
    @ApiImplicitParam(name = "userVo",value = "用户对象",required = true,dataType = "UserVo")
    ResponseWrap<Boolean> updateUser(@RequestBody UserVo userVo) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(userVo.getId())){
            throw new ProjectException(UserEnum.UPDATE_FAIL);
        }
        try {
            Boolean flag = userFace.updateUser(userVo);
            if (flag){
                List<AffixVo> affixVoList = affixFace.findAffixVoByBusinessId(userVo.getId());
                List<Long> affixIds = affixVoList.stream().map(AffixVo::getId).collect(Collectors.toList());
                if (!affixIds.contains(userVo.getAffixVo().getId())){
                    //删除图片
                    flag = affixFace.deleteAffixVoByBusinessId(userVo.getId());
                    //绑定新图片
                    affixFace.bindBusinessId(AffixVo.builder().businessId(userVo.getId()).id(userVo.getAffixVo().getId()).build());
                }
            }
            return ResponseWrapBuild.build(UserEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("保存用户异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.UPDATE_FAIL);
        }
    }

    /**
     * @Description 删除用户
     * @param userVo 查询对象
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除用户",notes = "删除用户")
    @ApiImplicitParam(name = "userVo",value = "用户查询对象",required = true,dataType = "UserVo")
    ResponseWrap<Boolean> deleteUser(@RequestBody UserVo userVo )throws ProjectException {
        String[] checkedIds = userVo.getCheckedIds();
        if (EmptyUtil.isNullOrEmpty(checkedIds)){
            throw new ProjectException(UserEnum.DELETE_FAIL);
        }
        try {
            Boolean flag = userFace.deleteUser(checkedIds);
            //删除图片
            for (String checkedId : checkedIds) {
                affixFace.deleteAffixVoByBusinessId(Long.valueOf(checkedId));
            }
            return ResponseWrapBuild.build(UserEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("删除用户异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.DELETE_FAIL);
        }
    }

    /**
     * @Description 查找用户
     * @param userId 登录名
     * @return
     */
    @GetMapping("select-by-userId/{userId}")
    @ApiOperation(value = "查找用户",notes = "查找用户")
    @ApiImplicitParam(paramType = "path",name = "userId",value = "用户Id",example = "1",dataType = "Long")
    ResponseWrap<UserVo> findUserByUserId(@PathVariable("userId") Long userId) throws ProjectException {

        if (EmptyUtil.isNullOrEmpty(userId)){
            throw new ProjectException(UserEnum.SELECT_USER_FAIL);
        }
        try {
            UserVo userVo = userFace.findUserByUserId(userId);
            return ResponseWrapBuild.build(UserEnum.SUCCEED,userVo);
        } catch (Exception e) {
            log.error("查找用户所有角色异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.SELECT_USER_FAIL);
        }
    }

    /**
     * @Description 查找用户list
     * @return
     */
    @GetMapping("select-list")
    @ApiOperation(value = "查找用户list",notes = "查找用户list")
    ResponseWrap<List<UserVo>> findUserVoList() throws ProjectException {

        try {
            List<UserVo> list = userFace.findUserVoList();
            return ResponseWrapBuild.build(UserEnum.SUCCEED,list);
        } catch (Exception e) {
            log.error("查找用户list异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.SELECT_USER_LIST_FAIL);
        }
    }

    /**
     * @Description 查找用户有角色
     * @param userId 用户Id
     * @return
     */
    @GetMapping("role-by-userId/{userId}")
    @ApiOperation(value = "查找用户有角色",notes = "查找用户有角色")
    @ApiImplicitParam(paramType = "path",name = "userId",value = "用户ID",example = "1",dataType = "Long")
    ResponseWrap<List<RoleVo>> findRoleByUserId(@PathVariable("userId") Long userId) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(userId)){
            throw new ProjectException(UserEnum.SELECT_ROLE_FAIL);
        }
        try {
            List<RoleVo> roleVos = userAdapterFace.findRoleByUserId(userId);
            return ResponseWrapBuild.build(UserEnum.SUCCEED,roleVos);
        } catch (Exception e) {
            log.error("查找用户有角色异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.SELECT_ROLE_FAIL);
        }
    }

    /**
     * @Description 查询用户有资源
     * @param userId 用户Id
     * @return
     */
    @GetMapping("resource-by-userId/{userId}")
    @ApiOperation(value = "查询用户有资源",notes = "查询用户有资源")
    @ApiImplicitParam(paramType = "path",name = "userId",value = "用户ID",example = "1",dataType = "Long")
    ResponseWrap<List<ResourceVo>> findResourceByUserId(@PathVariable("userId") Long userId) throws ProjectException {
        if (EmptyUtil.isNullOrEmpty(userId)){
            throw new ProjectException(UserEnum.SELECT_RESOURCE_FAIL);
        }
        try {
            List<ResourceVo> resourceVos = userAdapterFace.findResourceByUserId(userId);
            return ResponseWrapBuild.build(UserEnum.SUCCEED,resourceVos);
        } catch (Exception e) {
            log.error("查询用户有资源异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.SELECT_RESOURCE_FAIL);
        }
    }

    @GetMapping("find-current-user")
    @ApiOperation(value = "查询当前用户",notes = "查询当前用户")
    ResponseWrap<UserVo> findCurrentUser() throws ProjectException {
        try {
            String currentUser = UserVoContext.getUserVoString();
            UserVo userVo = JSON.parseObject(currentUser, UserVo.class);
            return ResponseWrapBuild.build(UserEnum.SUCCEED,userVo);
        } catch (Exception e) {
            log.error("查询当前用户异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.SELECT_CURRENT_USER);
        }
    }

    @PostMapping("update-user-enableFlag")
    @ApiOperation(value = "修改用户状态",notes = "修改用户状态")
    @ApiImplicitParam(name = "userVo",value = "用户对象",required = true,dataType = "UserVo")
    ResponseWrap<Boolean> updateUserEnableFlag(@RequestBody UserVo userVo) throws ProjectException {
        try {
            Boolean flag = userFace.updateUserEnableFlag(userVo);
            return ResponseWrapBuild.build(UserEnum.SUCCEED,flag);
        } catch (Exception e) {
            log.error("查询当前用户异常：{}", ExceptionsUtil.getStackTraceAsString(e));
            throw new ProjectException(UserEnum.UPDATE_FAIL);
        }
    }

}
