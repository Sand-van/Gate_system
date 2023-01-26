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

@ApiModel(value = "用户通行", description = "用户通行实体类")
@TableName("user_permit")
@Data
public class UserPermit implements Serializable
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
     * 起始时间
     */
    @ApiModelProperty(name = "起始时间")
    private Date beginTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(name = "结束时间")
    private Date endTime;
    /**
     * 创建人id
     */
    @ApiModelProperty(name = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新人id
     */
    @ApiModelProperty(name = "更新人id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}