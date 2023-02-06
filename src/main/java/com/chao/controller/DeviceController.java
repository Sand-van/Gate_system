package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.*;
import com.chao.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/device")
@Api(tags = "设备管理相关接口")
public class DeviceController
{
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPermitService userPermitService;

    @Autowired
    private AdminAuthorityService adminAuthorityService;

    @Autowired
    private PermissionRecordsService permissionRecordsService;

    @Autowired
    private UserApplyService userApplyService;

    @PostMapping("/add")
    @ApiOperation("手动添加设备")
    @ApiImplicitParam(name = "deviceToAdd", value = "要添加的设备信息", required = true)
    public ReturnMessage<String> addDevice(@RequestBody Device deviceToAdd)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            deviceService.save(deviceToAdd);
            return ReturnMessage.success("添加成功");
        }
        return ReturnMessage.forbiddenError("没有权限");
    }


    @DeleteMapping("/delete")
    @ApiOperation("删除单个设备")
    @ApiImplicitParam(name = "deviceIdToDelete", value = "要删除的用户的id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUser(Long deviceIdToDelete)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //超级管理员的权限
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            if (DeviceWebSocket.getDeviceWebSocketByDeviceID(deviceIdToDelete) != null)
                return ReturnMessage.commonError("设备离线");

            LambdaQueryWrapper<PermissionRecords> PRQueryWrapper = new LambdaQueryWrapper<>();
            PRQueryWrapper.eq(PermissionRecords::getDeviceId, deviceIdToDelete);
            permissionRecordsService.remove(PRQueryWrapper);

            LambdaQueryWrapper<UserApply> userApplyQueryWrapper = new LambdaQueryWrapper<>();
            userApplyQueryWrapper.eq(UserApply::getDeviceId, deviceIdToDelete);
            userApplyService.remove(userApplyQueryWrapper);

            LambdaQueryWrapper<UserPermit> userPermitQueryWrapper = new LambdaQueryWrapper<>();
            userPermitQueryWrapper.eq(UserPermit::getDeviceId, deviceIdToDelete);
            userPermitService.remove(userPermitQueryWrapper);

            LambdaQueryWrapper<AdminAuthority> adminAuthorityQueryWrapper = new LambdaQueryWrapper<>();
            adminAuthorityQueryWrapper.eq(AdminAuthority::getDeviceId, deviceIdToDelete);
            adminAuthorityService.remove(adminAuthorityQueryWrapper);

            deviceService.resetDevice(deviceIdToDelete);

            deviceService.removeById(deviceIdToDelete);

            return ReturnMessage.success("设备删除成功");
        }
        return ReturnMessage.forbiddenError("非法用户");
    }

    //todo:未测试过！！
    //todo:未完成！！
    @PostMapping("/replace")
    @ApiOperation("替换设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIdToBeReplace", value = "被替换的设备id", required = true),
            @ApiImplicitParam(name = "deviceToReplace", value = "替换的设备信息", required = true)
    })
    public ReturnMessage<String> replaceDevice(Long deviceIdToBeReplace, @RequestBody Device deviceToReplace)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            if (deviceService.getDeviceDataCount(deviceToReplace.getId()) != 0)
                return ReturnMessage.commonError("要替换的设备存在数据信息，请尝试删除该设备来重置此设备");

            //
            DeviceWebSocket deviceToReplaceSocket = DeviceWebSocket.getDeviceWebSocketByDeviceID(deviceToReplace.getId());

            deviceToReplace.setId(deviceIdToBeReplace);
//            deviceService.updateDeviceInfo(deviceToReplace);
            return ReturnMessage.success("替换成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    @PostMapping("/update")
    @ApiOperation("更新设备信息")
    @ApiImplicitParam(name = "deviceToUpdate", value = "要更新的设备信息", required = true)
    public ReturnMessage<String> updateDevice(@RequestBody Device deviceToUpdate)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            //更新数据库
            deviceService.updateById(deviceToUpdate);
            //更新具体设备
            DeviceWebSocket deviceToUpdateSocket = DeviceWebSocket.getDeviceWebSocketByDeviceID(deviceToUpdate.getId());
            if (deviceToUpdateSocket == null)
                return ReturnMessage.commonError("设备不在线");
            deviceToUpdateSocket.sendDeviceData(deviceToUpdate.getId(), deviceToUpdate.getName());
            return ReturnMessage.success("更新成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    @GetMapping("/getById")
    @ApiOperation("根据设备ID获取设备信息")
    @ApiImplicitParam(name = "deviceId", value = "要获取的设备Id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<Device> getDeviceById(Long deviceId)
    {
        return ReturnMessage.success(deviceService.getById(deviceId));
    }

    @GetMapping("/page")
    @ApiOperation("请求设备信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的设备名", dataTypeClass = String.class),
    })
    public ReturnMessage<Page<Device>> page(int page, int pageSize, String queryName)
    {
        Page<Device> devicePageInfo = new Page<>(page, pageSize);
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //普通用户没有权限查看分页
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryName), Device::getName, queryName)
                .orderByDesc(Device::getName);

        deviceService.page(devicePageInfo, queryWrapper);

        return ReturnMessage.success(devicePageInfo);
    }

    @PostMapping("/openDevice")
    @ApiOperation("启动设备")
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> openDevice(Long deviceId)
    {
        if (deviceService.judgeUserAndDevice(BaseContext.getCurrentUserInfo().getUserID(), deviceId))
        {
            DeviceWebSocket deviceSocket = DeviceWebSocket.getDeviceWebSocketByDeviceID(deviceId);
            if (deviceSocket == null)
                return ReturnMessage.commonError("设备未上线");
            deviceSocket.sendOpenRequest();
            return ReturnMessage.success("启动成功");
        }
        return ReturnMessage.commonError("没有权限");
    }
}
