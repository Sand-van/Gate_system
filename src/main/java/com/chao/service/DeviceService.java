package com.chao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.entity.Device;

import java.util.List;

public interface DeviceService extends IService<Device>
{
    List<Long> getIdByLikeName(String name);
}
