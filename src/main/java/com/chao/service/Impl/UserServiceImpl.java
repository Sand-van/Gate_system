package com.chao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.User;
import com.chao.mapper.UserMapper;
import com.chao.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Override
    public User getByNumber(String number)
    {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, number);
        return this.getOne(queryWrapper);
    }

    @Override
    public String getNameById(Long id)
    {
        User user = this.getById(id);
        return user.getName();
    }

    @Override
    public List<Long> getIdByLikeNameAndAccount(String name, String account)
    {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), User::getName, name)
                .like(StringUtils.isNotEmpty(account), User::getAccount, account);
        return this.list(queryWrapper).stream().map(User::getId).collect(Collectors.toList());
    }
}
