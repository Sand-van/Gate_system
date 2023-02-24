package com.chao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.common.CommonEnum;
import com.chao.entity.*;
import com.chao.mapper.DeviceMapper;
import com.chao.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService
{
    @Autowired
    UserService userService;

    @Autowired
    AdminAuthorityService adminAuthorityService;

    @Autowired
    UserPermitService userPermitService;

    @Autowired
    PermissionRecordsService permissionRecordsService;

    @Autowired
    UserApplyService userApplyService;

    @Override
    public List<Long> getIdByLikeName(String name)
    {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Device::getName, name);
        return this.list(queryWrapper).stream().map(Device::getId).collect(Collectors.toList());
    }

    @Override
    public boolean judgeUserAndDevice(Long userId, Long deviceId)
    {
        User user = userService.getById(userId);

        //超级管理员无需判断
        if (Objects.equals(user.getType(), CommonEnum.USER_TYPE_SUPER_ADMIN))
            return true;

        List<Long> permitDeviceIdList = userPermitService.getPermitDeviceByUserID(userId);

        if (Objects.equals(user.getType(), CommonEnum.USER_TYPE_ADMIN))
        {
            List<Long> adminAuthorityDeviceIdList = adminAuthorityService.getDeviceIdByAdminId(userId);
            //取交集
            permitDeviceIdList.removeAll(adminAuthorityDeviceIdList);
            permitDeviceIdList.addAll(adminAuthorityDeviceIdList);
        }
        return permitDeviceIdList.contains(deviceId);
    }

    @Override
    public int getDeviceDataCount(Long deviceId)
    {
        int count = 0;

        LambdaQueryWrapper<PermissionRecords> PRQueryWrapper = new LambdaQueryWrapper<>();
        PRQueryWrapper.eq(PermissionRecords::getDeviceId, deviceId);
        count += permissionRecordsService.count(PRQueryWrapper);

        LambdaQueryWrapper<UserApply> userApplyQueryWrapper = new LambdaQueryWrapper<>();
        userApplyQueryWrapper.eq(UserApply::getDeviceId, deviceId);
        count += userApplyService.count(userApplyQueryWrapper);

        LambdaQueryWrapper<UserPermit> userPermitQueryWrapper = new LambdaQueryWrapper<>();
        userPermitQueryWrapper.eq(UserPermit::getDeviceId, deviceId);
        count += userPermitService.count(userPermitQueryWrapper);

        LambdaQueryWrapper<AdminAuthority> adminAuthorityQueryWrapper = new LambdaQueryWrapper<>();
        adminAuthorityQueryWrapper.eq(AdminAuthority::getDeviceId, deviceId);
        count += adminAuthorityService.count(adminAuthorityQueryWrapper);

        return count;
    }

}
