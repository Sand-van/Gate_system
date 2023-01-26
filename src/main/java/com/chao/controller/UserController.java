package com.chao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chao.common.CommonEnum;
import com.chao.common.ReturnMessage;
import com.chao.entity.User;
import com.chao.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户操作相关接口")
public class UserController
{
    @Autowired
    private UserService userService;

    /**
     * 用户登录方法
     * @param request Http请求体
     * @param user 要登录的用户信息
     * @return 通用返回信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    @ApiImplicitParam(name = "user", value = "登录的用户信息", required = true)
    public ReturnMessage<User> login(HttpServletRequest request, @RequestBody User user)
    {
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNumber, user.getNumber());
        User queryedUser = userService.getOne(queryWrapper);

        if (queryedUser == null)
            return ReturnMessage.error("用户或密码错误");

        if (!queryedUser.getPassword().equals(password))
            return ReturnMessage.error("用户或密码错误");

        request.getSession().setAttribute("id", queryedUser.getId());
        request.getSession().setAttribute("type", queryedUser.getType());

        return ReturnMessage.success(queryedUser);
    }

    /**
     * 添加用户方法
     * @param request Http请求体
     * @param userToAdd 要添加的用户信息
     * @return 通用返回信息
     */
    @PostMapping("/addUser")
    @ApiOperation("添加用户接口")
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
            return ReturnMessage.error("用户已存在");

        //普通用户没有权限添加用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_USER))
            return ReturnMessage.error("没有权限");

        //管理员只能添加普通用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_ADMIN))
        {
            if (Objects.equals(userToAdd.getType(), CommonEnum.USER_TYPE_USER))
            {
                userService.save(userToAdd);
                return ReturnMessage.success("新增成功");
            }
            else
                return ReturnMessage.error("没有权限");
        }
        //超级管理员可以添加所有类型的用户
        if (Objects.equals(loginUserType, CommonEnum.USER_TYPE_SUPER_ADMIN))
        {
            userService.save(userToAdd);
            return ReturnMessage.success("新增成功");
        }
        return ReturnMessage.error("非法用户");
    }
}
