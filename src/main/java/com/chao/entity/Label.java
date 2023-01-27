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

@ApiModel(value = "标签", description = "标签实体类")
@TableName("label")
@Data
public class Label implements Serializable
{
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId
    private Long id;
    /**
     * 标签名称
     */
    @ApiModelProperty("标签名称")
    private String labelName;
    /**
     * 标签颜色
     */
    @ApiModelProperty("标签颜色")
    private Integer labelColor;
    /**
     * 创建人id
     */
    @ApiModelProperty("用户类型")
    private Integer type;
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