package com.chao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "通行记录", description = "通行记录实体类")
@TableName("permission_records")
@Data
public class PermissionRecords implements Serializable
{
    /**
     * 主键
     */
    @ApiModelProperty(name = "主键")
    @TableId
    private Long id;
    /**
     * 用户id
     */
    @ApiModelProperty(name = "用户id")
    private Long userId;
    /**
     * 设备id
     */
    @ApiModelProperty(name = "设备id")
    private Long deviceId;
    /**
     * 通行时间
     */
    @ApiModelProperty(name = "通行时间")
    @TableField(fill = FieldFill.INSERT)
    private Date permissionTime;

}