package com.chao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("用户通行权限实体类")
@TableName("user_permit")
@Data
public class UserPermit implements Serializable
{
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long id;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;
    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private Long deviceId;
    /**
     * 起始时间
     */
    @ApiModelProperty("起始时间")
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
    /**
     * 创建人id
     */
    @ApiModelProperty(notes = "创建人id", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(notes = "创建时间", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新人id
     */
    @ApiModelProperty(notes = "更新人id", hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    /**
     * 更新时间
     */
    @ApiModelProperty(notes = "更新时间", hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}