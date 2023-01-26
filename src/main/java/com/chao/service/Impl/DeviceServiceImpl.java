package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.Device;
import com.chao.mapper.DeviceMapper;
import com.chao.service.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService
{
}
