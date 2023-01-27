package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.common.BaseContext;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.User;
import com.chao.service.UserService;
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
@Api(tags = "用户操作相关")
public class UserController
{
    @Autowired
    private UserService userService;

    /**
     * 用户登录方法
     *
     * @param request Http请求体
     * @param user    要登录的用户信息
     * @return 通用返回信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @ApiImplicitParam(name = "user", value = "登录的用户信息", required = true)
    public ReturnMessage<User> login(HttpServletRequest request, @RequestBody User user)
    {
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNumber, user.getNumber());
        User queryedUser = userService.getOne(queryWrapper);

        if (queryedUser == null)
            return ReturnMessage.commonError("用户或密码错误");

        if (!queryedUser.getPassword().equals(password))
            return ReturnMessage.commonError("用户或密码错误");

        request.getSession().setAttribute("id", queryedUser.getId());
        request.getSession().setAttribute("type", queryedUser.getType());

        return ReturnMessage.success(queryedUser);
    }

    /**
     * 添加用户方法
     *
     * @param request   Http请求体
     * @param userToAdd 要添加的用户信息
     * @return 通用返回信息
     */
    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @ApiImplicitParam(name = "userToAdd", value = "要添加的用户信息", required = true)
    public ReturnMessage<String> addUser(HttpServletRequest request, @RequestBody User userToAdd)
    {
        Integer loginUserType = (Integer) request.getSession().getAttribute("type");
        userToAdd.setPassword(DigestUtils.md5DigestAsHex(userToAdd.getPassword().getBytes()));
        userToAdd.setId(null);

        //判断用户是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNumber, userToAdd.getNumber());
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
    public ReturnMessage<String> logout(HttpServletRequest request)
    {
        request.getSession().removeAttribute("id");
        request.getSession().removeAttribute("type");
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
        User nowLoginUser = userService.getById(BaseContext.getCurrentID());

        //普通用户没有权限查看用户分页
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_USER))
            return ReturnMessage.forbiddenError("没有权限");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryName), User::getName, queryName)
                .like(StringUtils.isNotEmpty(queryNumber), User::getNumber, queryNumber)
                .orderByDesc(User::getUpdateUser);

        userService.page(userPageInfo, queryWrapper);

        return ReturnMessage.success(userPageInfo);
    }

    @PutMapping("/updateUser")
    @ApiOperation("修改用户")
    @ApiImplicitParam(name = "userToUpdate", value = "要修改的用户信息", required = true)
    public ReturnMessage<String> updateUser(HttpServletRequest request, @RequestBody User userToUpdate)
    {
        log.info(User.class.toString());
        User nowLoginUser = userService.getById((Long) request.getSession().getAttribute("id"));

        if (Objects.equals(userToUpdate.getId(), nowLoginUser.getId()))
        {
            userService.updateById(userToUpdate);
            return ReturnMessage.success("信息修改成功");
        }

        User targetUser = userService.getById(userToUpdate.getId());
        //管理员的权限
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_ADMIN))
        {
            if (Objects.equals(targetUser.getType(), CommonEnum.USER_TYPE_USER))
            {
                userService.updateById(userToUpdate);
                return ReturnMessage.success("信息修改成功");
            } else
                return ReturnMessage.commonError("没有权限");
        }
        //超级管理员的权限
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.updateById(userToUpdate);
            return ReturnMessage.success("信息修改成功");
        }

        return ReturnMessage.forbiddenError("非法用户");
    }


    @DeleteMapping("/deleteUseByList")
    @ApiOperation("通过列表删除用户")
    @ApiImplicitParam(name = "userIdListToDelete", value = "要删除的用户的id列表", required = true)
    public ReturnMessage<String> deleteUserByList(HttpServletRequest request, @RequestBody List<Long> userIdListToDelete)
    {
        int successNumber = 0, failNumber = 0;

        for (Long item : userIdListToDelete)
        {
            if (deleteUser(request, item).getCode() == 200)
                successNumber += 1;
            else
                failNumber += 1;
        }
        return ReturnMessage.success(String.format("成功删除的个数：%d，删除失败的个数：%d", successNumber, failNumber));
    }


    @DeleteMapping("/deleteUser")
    @ApiOperation("删除单个用户")
    @ApiImplicitParam(name = "userIdToDelete", value = "要删除的用户的id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<String> deleteUser(HttpServletRequest request, Long userIdToDelete)
    {
        User nowLoginUser = userService.getById((Long) request.getSession().getAttribute("id"));
        User targetUser = userService.getById(userIdToDelete);

        //管理员的权限
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_ADMIN))
        {
            if (Objects.equals(targetUser.getType(), CommonEnum.USER_TYPE_USER))
            {
                userService.removeById(userIdToDelete);
                return ReturnMessage.success("用户删除成功");
            } else
                return ReturnMessage.commonError("没有权限");
        }
        //超级管理员的权限
        if (Objects.equals(nowLoginUser.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.removeById(userIdToDelete);
            return ReturnMessage.success("用户删除成功");
        }

        return ReturnMessage.forbiddenError("非法用户");
    }

    @GetMapping("/getUser/{id}")
    @ApiOperation("根据用户ID查询信息")
    @ApiImplicitParam(name = "id", value = "要查询的用户的id", dataTypeClass = Long.class, required = true)
    public ReturnMessage<User> getUserById(@PathVariable Long id)
    {
        User user = userService.getById(id);
        if (user != null)
            return ReturnMessage.success(user);

        return ReturnMessage.commonError("没有该用户");
    }

}