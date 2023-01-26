package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.Label;
import com.chao.mapper.LabelMapper;
import com.chao.service.LabelService;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService
{
}
