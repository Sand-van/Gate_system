package com.chao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("设备实体类")
@TableName("device")
@Data
public class Device implements Serializable
{
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long id;
    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    private String name;
    /**
     * 设备ip
     */
    @ApiModelProperty("设备ip")
    private String ip;
    /**
     * 设备状态
     */
    @ApiModelProperty("设备状态")
    private Integer status;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}