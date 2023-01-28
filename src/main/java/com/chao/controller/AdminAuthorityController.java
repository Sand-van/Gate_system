package com.chao.controller;

import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.AdminAuthority;
import com.chao.entity.User;
import com.chao.service.AdminAuthorityService;
import com.chao.service.DeviceService;
import com.chao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员权限管理相关接口")
public class AdminAuthorityController
{
    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AdminAuthorityService adminAuthorityService;

    @PostMapping("/add")
    @ApiOperation("添加管理员权限")
    @ApiImplicitParam(name = "adminAuthorityToAdd", value = "要添加的管理员权限信息", required = true)
    public ReturnMessage<String> addAdminAuthority(@RequestBody AdminAuthority adminAuthorityToAdd)
    {
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());
        User adminToAdd = userService.getById(adminAuthorityToAdd.getUserId());

        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN) && Objects.equals(adminToAdd.getType(), CommonEnum.USER_TYPE_ADMIN))
        {
            //查找是否有重复信息
            if (adminAuthorityService.getAuthorityIdByAdminIdAndDeviceId(adminAuthorityToAdd.getUserId(), adminAuthorityToAdd.getDeviceId()) != null)
                return ReturnMessage.commonError("重复的权限信息");

            adminAuthorityToAdd.setId(null);

            adminAuthorityService.save(adminAuthorityToAdd);
            return ReturnMessage.success("添加成功");
        }

        return ReturnMessage.commonError("没有权限");
    }

    @DeleteMapping("/deleteById")
    @ApiOperation("通过id来删除管理员权限")
    @ApiImplicitParam(name = "adminAuthorityId", value = "要删除的管理员权限id", required = true)
    public ReturnMessage<String> deleteAdminAuthority(Long adminAuthorityId)
    {
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            adminAuthorityService.removeById(adminAuthorityId);
            return ReturnMessage.success("删除成功");
        }
        return ReturnMessage.commonError("没有权限");
    }
}
