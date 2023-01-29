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
     * 启动设备
     * @param deviceId 要启动的设备id
     */
    void openDevice(Long deviceId);

    /**
     * 判断用户是否可以启动设备
     * @param userId 用户id
     * @param deviceId 设备id
     * @return 是否能启动
     */
    boolean judgeUserAndDevice(Long userId, Long deviceId);

    /**
     * 更新设备信息
     * @param device 设备信息
     * @return 是否更新成功
     */
    boolean updateDeviceInfo(Device device);

    /**
     * 具体设备连接后，进行的数据库操作方法
     * @param linkDevice 连接的设备信息
     */
    void deviceLink(Device linkDevice);

    /**
     * 根据设备名称来获取该设备在其他表中的数据总数
     * @param deviceId 设备id
     * @return 数据数
     */
    int getDeviceDataCount(Long deviceId);

    /**
     * 重置该设备，恢复初始状态
     * @param deviceId 要重置的设备id
     */
    void resetDevice(Long deviceId);
}
