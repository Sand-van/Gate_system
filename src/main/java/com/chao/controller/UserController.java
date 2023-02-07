package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.*;
import com.chao.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户操作相关接口")
@CrossOrigin
public class UserController
{
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


    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAccount", value = "用户账户", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true)
    })
    public ReturnMessage<User> login(Long userAccount, String password)
    {
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, userAccount);
        User queryedUser = userService.getOne(queryWrapper);

        if (queryedUser == null)
            return ReturnMessage.commonError("用户或密码错误");

        if (!queryedUser.getPassword().equals(password))
            return ReturnMessage.commonError("用户或密码错误");

        BaseContext.setCurrentUserInfo(queryedUser.getId(), queryedUser.getType());

        return ReturnMessage.successWithToken(queryedUser);
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取当前登录用户信息")
    @ApiImplicitParam(name = "token", value = "token")
    public ReturnMessage<User> info(String token)
    {
        User user = userService.getById(BaseContext.getCurrentUserInfo().getUserID());
        if (user != null)
            return ReturnMessage.success(user);

        return ReturnMessage.commonError("没有该用户");
    }

    /**
     * 添加用户方法
     *
     * @param userToAdd 要添加的用户信息
     * @return 通用返回信息
     */
    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @ApiImplicitParam(name = "userToAdd", value = "要添加的用户信息", required = true)
    public ReturnMessage<String> addUser(@RequestBody User userToAdd)
    {
        Integer loginUserType = BaseContext.getCurrentUserInfo().getUserType();
        userToAdd.setPassword(DigestUtils.md5DigestAsHex(userToAdd.getPassword().getBytes()));
        userToAdd.setId(null);

        //判断用户是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, userToAdd.getAccount());
        User queryedUser = userService.getOne(queryWrapper);
        if (queryedUser != null)
            return ReturnMessage.commonError("用户已存在");

        //普通用户没有权限添加用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        //管理员只能添加普通用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_ADMIN))
        {
            if (Objects.equals(userToAdd.getType(), CommonEnum.USER_TYPE_USER))
            {
                userService.save(userToAdd);
                return ReturnMessage.success("新增成功");
            } else
                return ReturnMessage.commonError("没有权限");
        }
        //超级管理员可以添加所有类型的用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.save(userToAdd);
            return ReturnMessage.success("新增成功");
        }
        return ReturnMessage.forbiddenError("非法用户");
    }


    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public ReturnMessage<String> logout()
    {
        //todo:考虑更改成功码，或根本不需要该功能
        return ReturnMessage.success("退出成功");
    }

    @GetMapping("/page")
    @ApiOperation("请求用户分页信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "要显示第几页", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "pageSize", value = "一页显示几条信息", dataTypeClass = int.class, required = true),
            @ApiImplicitParam(name = "queryName", value = "要搜索的人名", dataTypeClass = String.class),
            @ApiImplicitParam(name = "queryNumber", value = "要搜索的工号、学号", dataTypeClass = String.class)
    })
    public ReturnMessage<Page<User>> page(int page, int pageSize, String queryName, String queryNumber)
    {
        Page<User> userPageInfo = new Page<>(page, pageSize);
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //普通用户没有权限查看用户分页
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryName), User::getName, queryName)
                .like(StringUtils.isNotEmpty(queryNumber), User::getAccount, queryNumber)
                .orderByDesc(User::getUpdateUser);

        userService.page(userPageInfo, queryWrapper);

        return ReturnMessage.success(userPageInfo);
    }

    @PutMapping("/update")
    @ApiOperation("修改用户")
    @ApiImplicitParam(name = "userToUpdate", value = "要修改的用户信息", required = true)
    public ReturnMessage<String> updateUser(HttpServletRequest request, @RequestBody User userToUpdate)
    {
        log.info(User.class.toString());
        User nowLoginUser = userService.getById((Long) request.getSession().getAttribute("id"));

        //自己可以修改自己
        if (Objects.equals(userToUpdate.getId(), nowLoginUser.getId()))
        {
            userService.updateById(userToUpdate);
            return ReturnMessage.success("信息修改成功");
        }

        //超级管理员的权限
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.updateById(userToUpdate);
            return ReturnMessage.success("信息修改成功");
        }

        return ReturnMessage.forbiddenError("非法用户");
    }


    @DeleteMapping("/deleteByIdList")
    @ApiOperation("通过列表删除用户")
    @ApiImplicitParam(name = "userIdListToDelete", value = "要删除的用户的id列表", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUserByList(@RequestBody List<Long> userIdListToDelete)
    {
        int successNumber = 0, failNumber = 0;

        for (Long item : userIdListToDelete)
        {
            if (deleteUser(item).getCode() == 200)
                successNumber += 1;
            else
                failNumber += 1;
        }
        return ReturnMessage.success(String.format("成功删除的个数：%d，删除失败的个数：%d", successNumber, failNumber));
    }


    @DeleteMapping("/delete")
    @ApiOperation("删除单个用户")
    @ApiImplicitParam(name = "userIdToDelete", value = "要删除的用户的id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUser(Long userIdToDelete)
    {
//        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //超级管理员的权限
        if (Objects.equals(BaseContext.getCurrentUserInfo().getUserType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.removeById(userIdToDelete);

            LambdaQueryWrapper<PermissionRecords> PRQueryWrapper = new LambdaQueryWrapper<>();
            PRQueryWrapper.eq(PermissionRecords::getUserId, userIdToDelete);
            permissionRecordsService.remove(PRQueryWrapper);

            LambdaQueryWrapper<UserApply> userApplyQueryWrapper = new LambdaQueryWrapper<>();
            userApplyQueryWrapper.eq(UserApply::getUserId, userIdToDelete);
            userApplyService.remove(userApplyQueryWrapper);

            LambdaQueryWrapper<UserPermit> userPermitQueryWrapper = new LambdaQueryWrapper<>();
            userPermitQueryWrapper.eq(UserPermit::getUserId, userIdToDelete);
            userPermitService.remove(userPermitQueryWrapper);

            LambdaQueryWrapper<AdminAuthority> adminAuthorityQueryWrapper = new LambdaQueryWrapper<>();
            adminAuthorityQueryWrapper.eq(AdminAuthority::getUserId, userIdToDelete);
            adminAuthorityService.remove(adminAuthorityQueryWrapper);

            return ReturnMessage.success("用户删除成功");
        }

        return ReturnMessage.forbiddenError("非法用户");
    }

    @GetMapping("/getById")
    @ApiOperation("根据用户ID查询信息")
    @ApiImplicitParam(name = "id", value = "要查询的用户的id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<User> getUserById(Long id)
    {
        User user = userService.getById(id);
        if (user != null)
            return ReturnMessage.success(user);

        return ReturnMessage.commonError("没有该用户");
    }
}
