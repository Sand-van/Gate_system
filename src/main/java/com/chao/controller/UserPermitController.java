package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.dto.UserPermitDto;
import com.chao.entity.User;
import com.chao.entity.UserPermit;
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

import javax.servlet.http.HttpServletRequest;
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

    private Boolean isLoginUserHasAuthority(HttpServletRequest request)
    {
        return (request.getSession().getAttribute("type") == CommonEnum.USER_TYPE_ADMIN) || (request.getSession().getAttribute("type") == CommonEnum.USER_TYPE_SUPER_ADMIN);
    }

    @PostMapping("/add")
    @ApiOperation("添加用户通行权限信息")
    @ApiImplicitParam(name = "userPermitToAdd", value = "要添加的用户通行权限信息", required = true)
    public ReturnMessage<String> addUserPermit(HttpServletRequest request, @RequestBody UserPermit userPermitToAdd)
    {
        //TODO:判断是否重复添加
        if (isLoginUserHasAuthority(request))
        {
            userPermitToAdd.setId(null);
            userPermitService.save(userPermitToAdd);
            return ReturnMessage.success("添加成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    @DeleteMapping("/deleteById")
    @ApiOperation("通过id来删除用户通行权限信息")
    @ApiImplicitParam(name = "userPermitId", value = "要删除的用户通行权限信息id", required = true)
    public ReturnMessage<String> deleteUserPermitById(HttpServletRequest request, Long userPermitId)
    {
        //TODO:增加批量删除权限（需要获取管理员的操作权限List）
        if (isLoginUserHasAuthority(request))
        {
            userPermitService.removeById(userPermitId);
            return ReturnMessage.success("删除成功");
        }
        return ReturnMessage.commonError("没有权限");
    }

    //TODO:增加批量删除权限（需要获取管理员的操作权限List）

    @GetMapping("/page")
    @ApiOperation("请求用户通行权限信息分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的人名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryNumber", value = "要搜索的学号", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryDevice", value = "要搜索的设备名", dataTypeClass = String.class)
    })
    public ReturnMessage<Page<UserPermitDto>> page(int page, int pageSize, String queryName, String queryNumber, String queryDevice)
    {
        Page<UserPermit> userPageInfo = new Page<>(page, pageSize);
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        Page<UserPermitDto> userPermitDtoPageInfo = new Page<>();

        //普通用户没有权限查看用户分页
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<UserPermit> queryWrapper = new LambdaQueryWrapper<>();

//        TODO:增加管理员的相关权限判断(默认存在一个queryDevice，与查询出来的取交集)
        if (StringUtils.isNotEmpty(queryName) || StringUtils.isNotEmpty(queryNumber))
        {
            List<Long> userIds = userService.getIdByLikeNameAndNumber(queryName, queryNumber);
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
