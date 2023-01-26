package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.UserApply;
import com.chao.mapper.UserApplyMapper;
import com.chao.service.UserApplyService;
import org.springframework.stereotype.Service;

@Service
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApply> implements UserApplyService
{
}
