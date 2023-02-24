package com.chao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("通行记录实体类")
@TableName("permission_records")
@Data
public class PermissionRecords implements Serializable
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

    @ApiModelProperty("校园卡id")
    private Long cardId;
    /**
     * 通行时间
     */
    @ApiModelProperty("通行时间")
    private LocalDateTime permissionTime;

    @ApiModelProperty("是否通行成功")
    private Integer isSuccess;
}