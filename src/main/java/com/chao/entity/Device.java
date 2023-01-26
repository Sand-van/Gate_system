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

@ApiModel(value = "设备", description = "设备实体类")
@TableName("device")
@Data
public class Device implements Serializable
{
    /**
     * 主键
     */
    @ApiModelProperty(name = "主键")
    @TableId
    private Long id;
    /**
     * 设备名称
     */
    @ApiModelProperty(name = "设备名称")
    private String name;
    /**
     * 设备ip
     */
    @ApiModelProperty(name = "设备ip")
    private String ip;
    /**
     * 设备状态
     */
    @ApiModelProperty(name = "设备状态")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}