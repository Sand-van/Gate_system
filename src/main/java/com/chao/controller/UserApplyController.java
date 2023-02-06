package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.dto.UserApplyDto;
import com.chao.entity.UserApply;
import com.chao.entity.UserPermit;
import com.chao.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user/apply")
@Api(tags = "用户申请操作相关接口")
public class UserApplyController
{
    @Autowired
    private UserService userService;

    @Autowired
    private UserApplyService userApplyService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AdminAuthorityService adminAuthorityService;

    @Autowired
    private UserPermitService userPermitService;

    @PostMapping("/add")
    @ApiOperation("添加用户申请")
    @ApiImplicitParam(name = "userApplyToAdd", value = "要添加的用户申请", required = true)
    public ReturnMessage<String> addUserApply(@RequestBody UserApply userApplyToAdd)
    {
        userApplyToAdd.setId(null);
        userApplyToAdd.setApplyTime(LocalDateTime.now());

        userApplyService.save(userApplyToAdd);
        return ReturnMessage.success("保存成功");
    }

    @DeleteMapping("/deleteById")
    @ApiOperation("通过id来删除用户申请")
    @ApiImplicitParam(name = "userApplyId", value = "要删除的用户申请id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUserApplyById(Long userApplyId)
    {
        userApplyService.removeById(userApplyId);
        return ReturnMessage.success("删除成功");
    }

    @DeleteMapping("/deleteByIdList")
    @ApiOperation("通过id列表删除用户申请信息")
    @ApiImplicitParam(name = "userIdListToDelete", value = "要删除的用户申请信息的id列表", dataTypeClass = List.class, required = true)
    public ReturnMessage<String> deleteUserApplyByList(@RequestBody List<Long> userIdListToDelete)
    {
        int successNumber = 0, failNumber = 0;
        for (Long item : userIdListToDelete)
        {
            if (deleteUserApplyById(item).getCode() == 200)
                successNumber += 1;
            else
                failNumber += 1;
        }
        return ReturnMessage.success(String.format("成功删除的个数：%d，删除失败的个数：%d", successNumber, failNumber));
    }

    @GetMapping("/page")
    @ApiOperation("请求用户申请分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的人名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryNumber", value = "要搜索的学号", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryDevice", value = "要搜索的设备名", dataTypeClass = String.class)
    })
    public ReturnMessage<Page<UserApplyDto>> page(int page, int pageSize, String queryName, String queryNumber, String queryDevice)
    {
        Page<UserApply> userPageInfo = new Page<>(page, pageSize);
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        Page<UserApplyDto> userApplyDtoPageInfo = new Page<>();

        //普通用户没有权限查看用户分页
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<UserApply> queryWrapper = new LambdaQueryWrapper<>();

        //管理员只能查看自己权限内的申请
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_ADMIN))
        {
            List<Long> deviceId = adminAuthorityService.getDeviceIdByAdminId(BaseContext.getCurrentUserInfo().getUserID());
            if (deviceId.size() == 0)
                return ReturnMessage.success(userApplyDtoPageInfo);
            queryWrapper.in(UserApply::getDeviceId, deviceId);
        }

        if (StringUtils.isNotEmpty(queryName) || StringUtils.isNotEmpty(queryNumber))
        {
            List<Long> userIds = userService.getIdByLikeNameAndAccount(queryName, queryNumber);
            if (userIds.size() == 0)    //防止出现 select * from xxx where(user_id in [null])错误
                return ReturnMessage.success(userApplyDtoPageInfo);
            queryWrapper.in(UserApply::getUserId, userIds);
        }

        if (StringUtils.isNotEmpty(queryDevice))
        {
            List<Long> deviceIds = deviceService.getIdByLikeName(queryDevice);
            if (deviceIds.size() == 0)  //防止出现 in []错误
                return ReturnMessage.success(userApplyDtoPageInfo);
            queryWrapper.in(UserApply::getDeviceId, deviceIds);
        }

        queryWrapper.orderByDesc(UserApply::getApplyTime);
        userApplyService.page(userPageInfo, queryWrapper);
        BeanUtils.copyProperties(userPageInfo, userApplyDtoPageInfo, "records");
        //流处理，将UserApply转化为UserApplyDto
        List<UserApply> records = userPageInfo.getRecords();
        List<UserApplyDto> dtoRecords = records.stream().map((item) ->
        {
            UserApplyDto userApplyDto = new UserApplyDto();
            BeanUtils.copyProperties(item, userApplyDto);

            Long id = item.getUserId();
            userApplyDto.setUserName(userService.getById(id).getName());

            id = item.getDeviceId();
            userApplyDto.setDeviceName(deviceService.getById(id).getName());

            return userApplyDto;
        }).collect(Collectors.toList());

        userApplyDtoPageInfo.setRecords(dtoRecords);
        return ReturnMessage.success(userApplyDtoPageInfo);
    }

    @PostMapping("/accept")
    @ApiOperation("同意申请转正")
    @ApiImplicitParam(name = "applyId", value = "申请ID", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> acceptApply(Long applyId)
    {
        //普通用户没有权限
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());
        UserApply userApply = userApplyService.getById(applyId);

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_ADMIN))
        {
            if (!adminAuthorityService.getDeviceIdByAdminId(BaseContext.getCurrentUserInfo().getUserID()).contains(userApply.getDeviceId()))
            {
                //管理员没有该设备的权限
                return ReturnMessage.commonError("没有权限");
            }
        }

        UserPermit userPermit = new UserPermit();
        userPermit.setUserId(userApply.getUserId());
        userPermit.setDeviceId(userApply.getDeviceId());
        userPermit.setBeginTime(userApply.getBeginTime());
        userPermit.setEndTime(userApply.getEndTime());
        userPermitService.save(userPermit);
        userApplyService.removeById(applyId);

        return ReturnMessage.success("添加成功");
    }
}
