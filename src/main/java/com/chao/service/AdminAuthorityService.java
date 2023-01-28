package com.chao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.entity.AdminAuthority;

import java.util.List;

public interface AdminAuthorityService extends IService<AdminAuthority>
{
    /**
     * 获取某个管理员所拥有的设备权限
     * @param adminId 管理员id
     * @return 设备id的List表
     */
    List<Long> getDeviceIdByAdminId(Long adminId);


    /**
     * 获取某个设备被什么管理员所管理
     * @param deviceId 管理员id
     * @return 管理员id的List表
     */
    List<Long> getAdminIdByDeviceId(Long deviceId);

    /**
     * 获取通过管理员id和设备id获取权限条目id
     * @param adminId 管理员id
     * @param deviceId 管理员id
     * @return 获取的权限条目id，没有则为null
     */
    Long getAuthorityIdByAdminIdAndDeviceId(Long adminId, Long deviceId);
}
