package com.chao.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandle implements MetaObjectHandler
{
    /**
     * 插入操作，自动填充
     * @param metaObject 设置方法
     */
    @Override
    public void insertFill(MetaObject metaObject)
    {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentID());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }

    /**
     * 更新操作，自动填充
     * @param metaObject 设置方法
     */
    @Override
    public void updateFill(MetaObject metaObject)
    {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentID());
    }
}
