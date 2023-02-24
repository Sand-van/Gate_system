package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.dto.UserPermitDto;
import com.chao.entity.UserPermit;
import com.chao.service.AdminAuthorityService;
import com.chao.service.DeviceService;
import com.chao.service.UserPermitService;
import com.chao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user/permit")
@Api(tags = "用户通行权限操作相关接口")
public class UserPermitController
{
    @Autowired
    private UserService userService;

    @Autowired
    private UserPermitService userPermitService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AdminAuthorityService adminAuthorityService;

    /**
     * 判断当前用户是否有权限操作当前申请信息
     *
     * @param userPermit 当前的申请信息
     * @return boolean, 是否有权限
     */
    private Boolean isLoginUserHasAuthority(UserPermit userPermit)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return false;
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_ADMIN))
        {
            //管理员没有该设备的权限
            return adminAuthorityService.getDeviceIdByAdminId(BaseContext.getCurrentUserInfo().getUserID()).contains(userPermit.getDeviceId());
        }
        return true;
    }

    @PostMapping("/add")
    @ApiOperation("添加用户通行权限信息")
    @ApiImplicitParam(name = "userPermitToAdd", value = "要添加的用户通行权限信息", required = true)
    public ReturnMessage<String> addUserPermit(@RequestBody UserPermit userPermitToAdd)
    {
        if (isLoginUserHasAuthority(userPermitToAdd))
        {
            userPermitToAdd.setId(null);
            userPermitService.save(userPermitToAdd);
            return ReturnMessage.success("添加成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    @PostMapping("/addByList")
    @ApiOperation("通过列表添加用户通行权限信息")
    @ApiImplicitParam(name = "userPermitListToAdd", value = "要添加的用户通行权限信息的列表", required = true)
    public ReturnMessage<String> addUserPermitByList(@RequestBody List<UserPermit> userPermitListToAdd)
    {
        int successNumber = 0, failNumber = 0;
        for (UserPermit userPermit : userPermitListToAdd)
        {
            if (addUserPermit(userPermit).getCode() == 200)
                successNumber +=1;
            else
                failNumber +=1;
        }
        return ReturnMessage.success(String.format("成功添加的个数：%d，添加失败的个数：%d", successNumber, failNumber));
    }

    //todo:增加修改操作

    @DeleteMapping("/deleteById")
    @ApiOperation("通过id来删除用户通行权限信息")
    @ApiImplicitParam(name = "userPermitId", value = "要删除的用户通行权限信息id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUserPermitById(Long userPermitId)
    {
        UserPermit userPermit = userPermitService.getById(userPermitId);
        if (isLoginUserHasAuthority(userPermit))
        {
            userPermitService.removeById(userPermitId);
            return ReturnMessage.success("删除成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    @DeleteMapping("/deleteByIdList")
    @ApiOperation("通过id列表来删除用户通行权限信息")
    @ApiImplicitParam(name = "userPermitIdList", value = "要删除的用户通行权限信息id列表", dataTypeClass = List.class, required = true)
    public ReturnMessage<String> deleteUserPermitByIdList(@RequestBody List<Long> userPermitIdList)
    {
        int successNumber = 0, failNumber = 0;
        for (Long userPermitId : userPermitIdList)
        {
            if (deleteUserPermitById(userPermitId).getCode() == 200)
                successNumber += 1;
            else
                failNumber += 1;
        }
        return ReturnMessage.success(String.format("成功删除的个数：%d，删除失败的个数：%d", successNumber, failNumber));
    }

    @GetMapping("/page")
    @ApiOperation("请求用户通行权限信息分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryUserId", value = "要请求的用户id（精准匹配）", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "queryDeviceId", value = "要请求的设备id（精准匹配）", dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的人名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryAccount", value = "要搜索的学号", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryDevice", value = "要搜索的设备名", dataTypeClass = String.class)
    })
    public ReturnMessage<Page<UserPermitDto>> page(int page, int pageSize, Long queryUserId, Long queryDeviceId, String queryName, String queryAccount, String queryDevice)
    {
        Page<UserPermit> userPageInfo = new Page<>(page, pageSize);

        Page<UserPermitDto> userPermitDtoPageInfo = new Page<>();

        //普通用户没有权限查看用户分页
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<UserPermit> queryWrapper = new LambdaQueryWrapper<>();

        //管理员只能查看自己权限内的信息
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_ADMIN))
        {
            List<Long> deviceIdByAdminId = adminAuthorityService.getDeviceIdByAdminId(BaseContext.getCurrentUserInfo().getUserID());
            if (deviceIdByAdminId.size() == 0)
                return ReturnMessage.success(userPermitDtoPageInfo);
            queryWrapper.in(UserPermit::getDeviceId, deviceIdByAdminId);
        }

        queryWrapper.eq(queryUserId != null, UserPermit::getUserId, queryUserId);
        queryWrapper.eq(queryDeviceId != null, UserPermit::getDeviceId, queryDeviceId);

        if (StringUtils.isNotEmpty(queryName) || StringUtils.isNotEmpty(queryAccount))
        {
            List<Long> userIds = userService.getIdByLikeNameAndAccount(queryName, queryAccount);
            if (userIds.size() == 0)    //防止出现 select * from xxx where(user_id in [null])错误
                return ReturnMessage.success(userPermitDtoPageInfo);
            queryWrapper.in(UserPermit::getUserId, userIds);
        }

        if (StringUtils.isNotEmpty(queryDevice))
        {
            List<Long> deviceIds = deviceService.getIdByLikeName(queryDevice);
            if (deviceIds.size() == 0)  //防止出现 in []错误
                return ReturnMessage.success(userPermitDtoPageInfo);
            queryWrapper.in(UserPermit::getDeviceId, deviceIds);
        }

        queryWrapper.orderByDesc(UserPermit::getUserId);
        userPermitService.page(userPageInfo, queryWrapper);
        BeanUtils.copyProperties(userPageInfo, userPermitDtoPageInfo, "records");
        //流处理，将UserPermit转化为UserPermitDto
        List<UserPermit> records = userPageInfo.getRecords();
        List<UserPermitDto> dtoRecords = records.stream().map((item) ->
        {
            UserPermitDto userPermitDto = new UserPermitDto();
            BeanUtils.copyProperties(item, userPermitDto);

            Long id = item.getUserId();
            userPermitDto.setUserName(userService.getById(id).getName());

            id = item.getDeviceId();
            userPermitDto.setDeviceName(deviceService.getById(id).getName());

            return userPermitDto;
        }).collect(Collectors.toList());

        userPermitDtoPageInfo.setRecords(dtoRecords);
        return ReturnMessage.success(userPermitDtoPageInfo);
    }
}
