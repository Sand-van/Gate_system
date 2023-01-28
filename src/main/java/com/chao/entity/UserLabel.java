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

@ApiModel("用户拥有的标签实体类")
@TableName("user_label")
@Data
public class UserLabel implements Serializable
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
     * 标签id
     */
    @ApiModelProperty("标签id")
    private Long labelId;
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