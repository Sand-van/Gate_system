package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.UserPermit;
import com.chao.mapper.UserPermitMapper;
import com.chao.service.UserPermitService;
import org.springframework.stereotype.Service;

@Service
public class UserPermitServiceImpl extends ServiceImpl<UserPermitMapper, UserPermit> implements UserPermitService
{
}
