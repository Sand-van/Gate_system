package com.chao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.entity.PermissionRecords;
import com.chao.mapper.PermissionRecordsMapper;
import com.chao.service.PermissionRecordsService;
import org.springframework.stereotype.Service;

@Service
public class PermissionRecordsServiceImpl extends ServiceImpl<PermissionRecordsMapper, PermissionRecords> implements PermissionRecordsService
{
}
