package com.chao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.AdminAuthority;
import com.chao.mapper.AdminAuthorityMapper;
import com.chao.service.AdminAuthorityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAuthorityServiceImpl extends ServiceImpl<AdminAuthorityMapper, AdminAuthority> implements AdminAuthorityService
{
    @Override
    public List<Long> getDeviceIdByAdminId(Long adminId)
    {
        LambdaQueryWrapper<AdminAuthority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(adminId != null, AdminAuthority::getUserId, adminId);
        List<AdminAuthority> adminAuthorityList = this.list(queryWrapper);

        //设备id表
        return adminAuthorityList.stream().map(AdminAuthority::getDeviceId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAdminIdByDeviceId(Long deviceId)
    {
        LambdaQueryWrapper<AdminAuthority> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(deviceId != null, AdminAuthority::getDeviceId, deviceId);
        List<AdminAuthority> adminAuthorityList = this.list(queryWrapper);

        //设备id表
        return adminAuthorityList.stream().map(AdminAuthority::getUserId).collect(Collectors.toList());
    }

    @Override
    public Long getAuthorityIdByAdminIdAndDeviceId(Long adminId, Long deviceId)
    {
        if ((adminId != null)&&(deviceId != null))
        {
            LambdaQueryWrapper<AdminAuthority> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdminAuthority::getDeviceId, deviceId)
                    .eq(AdminAuthority::getUserId, adminId);
            AdminAuthority adminAuthority = this.getOne(queryWrapper);
            if (adminAuthority == null)
                return null;
            else
                return adminAuthority.getId();
        }
        return null;
    }
}
