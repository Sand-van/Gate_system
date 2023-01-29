package com.chao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.entity.UserPermit;

import java.util.List;

public interface UserPermitService extends IService<UserPermit>
{
    /**
     * 通过用户id来获取其通行权限列表
     * @param userId 用户id
     * @return 能通行的设备id列表
     */
    List<Long> getPermitDeviceByUserID(Long userId);
}
