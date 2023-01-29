package com.chao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.UserPermit;
import com.chao.mapper.UserPermitMapper;
import com.chao.service.UserPermitService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPermitServiceImpl extends ServiceImpl<UserPermitMapper, UserPermit> implements UserPermitService
{
    @Override
    public List<Long> getPermitDeviceByUserID(Long userId)
    {
        LambdaQueryWrapper<UserPermit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPermit::getUserId, userId);
        return this.list(queryWrapper).stream().map(UserPermit::getDeviceId).collect(Collectors.toList());
    }
}
