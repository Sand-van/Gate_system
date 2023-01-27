package com.chao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.Device;
import com.chao.mapper.DeviceMapper;
import com.chao.service.DeviceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService
{
    @Override
    public List<Long> getIdByLikeName(String name)
    {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Device::getName, name);
        return this.list(queryWrapper).stream().map(Device::getId).collect(Collectors.toList());
    }
}
