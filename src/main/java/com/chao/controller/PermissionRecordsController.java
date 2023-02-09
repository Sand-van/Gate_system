package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.Formatter;
import com.chao.common.ReturnMessage;
import com.chao.dto.PermissionRecordsDto;
import com.chao.entity.PermissionRecords;
import com.chao.service.AdminAuthorityService;
import com.chao.service.DeviceService;
import com.chao.service.PermissionRecordsService;
import com.chao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/permissionRecords")
@Api(tags = "通信信息管理相关接口")
public class PermissionRecordsController
{
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionRecordsService permissionRecordsService;

    @Autowired
    private AdminAuthorityService adminAuthorityService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 添加通行记录
     * @param recordsToAdd 要添加的通行记录
     */
    public void addPermissionRecords(PermissionRecords recordsToAdd)
    {
        recordsToAdd.setId(null);
        permissionRecordsService.save(recordsToAdd);
    }

    @GetMapping("/page")
    @ApiOperation("通行信息查看")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的人名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryAccount", value = "要搜索的学号", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryDevice", value = "要搜索的设备名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "beginTime", value = "要搜索的起始时间", dataTypeClass = String.class),
            @ApiImplicitParam(name = "endTime", value = "要搜索的结束时间", dataTypeClass = String.class)
    })
    public ReturnMessage<Page<PermissionRecordsDto>> page(int page, int pageSize, String queryName, String queryAccount, String queryDevice, String beginTime, String endTime)
    {
        Page<PermissionRecords> recordsPageInfo = new Page<>(page, pageSize);
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        Page<PermissionRecordsDto> recordsDtoPageInfo = new Page<>();

        //普通用户没有权限查看用户分页
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        //管理员只能查看自己权限内的信息
        LambdaQueryWrapper<PermissionRecords> queryWrapper = new LambdaQueryWrapper<>();

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_ADMIN))
        {
            List<Long> deviceIdByAdminId = adminAuthorityService.getDeviceIdByAdminId(BaseContext.getCurrentUserInfo().getUserID());
            if (deviceIdByAdminId.size() == 0)
                return ReturnMessage.success(recordsDtoPageInfo);
            queryWrapper.in(PermissionRecords::getDeviceId, deviceIdByAdminId);
        }

        if (StringUtils.isNotEmpty(queryName) || StringUtils.isNotEmpty(queryAccount))
        {
            List<Long> userIds = userService.getIdByLikeNameAndAccount(queryName, queryAccount);
            if (userIds.size() == 0)    //防止出现 select * from xxx where(user_id in [null])错误
                return ReturnMessage.success(recordsDtoPageInfo);
            queryWrapper.in(PermissionRecords::getUserId, userIds);
        }

        if (StringUtils.isNotEmpty(queryDevice))
        {
            List<Long> deviceIds = deviceService.getIdByLikeName(queryDevice);
            if (deviceIds.size() == 0)  //防止出现 in []错误
                return ReturnMessage.success(recordsDtoPageInfo);
            queryWrapper.in(PermissionRecords::getDeviceId, deviceIds);
        }

        LocalDateTime beginDateTime = Formatter.stringToLocalDataTime(beginTime);
        LocalDateTime endDateTime = Formatter.stringToLocalDataTime(endTime);

        queryWrapper.gt(beginDateTime != null, PermissionRecords::getPermissionTime, beginDateTime);
        queryWrapper.lt(endDateTime != null, PermissionRecords::getPermissionTime, endDateTime);

        queryWrapper.orderByDesc(PermissionRecords::getPermissionTime);
        permissionRecordsService.page(recordsPageInfo, queryWrapper);
        BeanUtils.copyProperties(recordsPageInfo,recordsDtoPageInfo,"records");

        //流处理
        List<PermissionRecords> records = recordsPageInfo.getRecords();
        List<PermissionRecordsDto> dtoRecords = records.stream().map((item) ->
        {
            PermissionRecordsDto permissionRecordsDto = new PermissionRecordsDto();
            BeanUtils.copyProperties(item, permissionRecordsDto);

            Long id = item.getUserId();
            permissionRecordsDto.setUserName(userService.getById(id).getName());

            id = item.getDeviceId();
            permissionRecordsDto.setDeviceName(deviceService.getById(id).getName());

            return permissionRecordsDto;
        }).collect(Collectors.toList());

        recordsDtoPageInfo.setRecords(dtoRecords);
        return ReturnMessage.success(recordsDtoPageInfo);
    }
}
