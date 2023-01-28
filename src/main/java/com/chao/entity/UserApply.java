package com.chao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("用户申请实体类")
@TableName("user_apply")
@Data
public class UserApply implements Serializable
{

    @ApiModelProperty("主键")
    @TableId
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("设备id")
    private Long deviceId;

    @ApiModelProperty("开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

}