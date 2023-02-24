package com.chao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.entity.Device;

import java.util.List;

public interface DeviceService extends IService<Device>
{
    /**
     * 通过设备名获取设备id列表
     * @param name 设备名
     * @return 设备id列表
     */
    List<Long> getIdByLikeName(String name);


    /**
     * 判断用户是否可以启动设备
     * @param userId 用户id
     * @param deviceId 设备id
     * @return 是否能启动
     */
    boolean judgeUserAndDevice(Long userId, Long deviceId);


    /**
     * 根据设备名称来获取该设备在其他表中的数据总数
     * @param deviceId 设备id
     * @return 数据数
     */
    int getDeviceDataCount(Long deviceId);

}
