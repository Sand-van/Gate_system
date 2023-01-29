package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.Device;
import com.chao.entity.User;
import com.chao.service.DeviceService;
import com.chao.service.UserPermitService;
import com.chao.service.UserService;
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

    @PostMapping("/add")
    @ApiOperation("添加设备")
    @ApiImplicitParam(name = "deviceToAdd", value = "要添加的设备信息", required = true)
    public ReturnMessage<String> addDevice(@RequestBody Device deviceToAdd)
    {
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            deviceToAdd.setId(null);
            deviceService.save(deviceToAdd);

            return ReturnMessage.success("添加成功");
        }
        return ReturnMessage.forbiddenError("没有权限");
    }

    @PostMapping("/update")
    @ApiOperation("修改设备信息")
    @ApiImplicitParam(name = "deviceToUpdate", value = "要更新的设备信息", required = true)
    public ReturnMessage<String> updateDevice(@RequestBody Device deviceToUpdate)
    {
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            deviceService.updateById(deviceToUpdate);
            return ReturnMessage.success("修改成功");
        }
        return ReturnMessage.forbiddenError("没有权限");
    }

    @GetMapping("/getById")
    @ApiOperation("根据设备ID获取设备信息")
    @ApiImplicitParam(name = "deviceId", value = "要获取的设备Id", required = true)
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
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //普通用户没有权限查看分页
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryName), Device::getName, queryName)
                .orderByDesc(Device::getName);

        deviceService.page(devicePageInfo, queryWrapper);

        return ReturnMessage.success(devicePageInfo);
    }
}
