package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.AdminAuthority;
import com.chao.mapper.AdminAuthorityMapper;
import com.chao.service.AdminAuthorityService;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorityServiceImpl extends ServiceImpl<AdminAuthorityMapper, AdminAuthority> implements AdminAuthorityService
{
}
