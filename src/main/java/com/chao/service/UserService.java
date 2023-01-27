package com.chao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.entity.User;

import java.util.List;

public interface UserService extends IService<User>
{
    User getByNumber(String number);

    String getNameById(Long id);

    List<Long> getIdByLikeNameAndNumber(String name, String number);
}
