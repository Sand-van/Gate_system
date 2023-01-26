package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.UserLabel;
import com.chao.mapper.UserLabelMapper;
import com.chao.service.UserLabelService;
import org.springframework.stereotype.Service;

@Service
public class UserLabelServiceImpl extends ServiceImpl<UserLabelMapper, UserLabel> implements UserLabelService
{
}
