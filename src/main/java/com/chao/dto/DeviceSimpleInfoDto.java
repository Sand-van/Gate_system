package com.chao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("设备简单信息DTO类")
@Data
public class DeviceSimpleInfoDto
{
    @ApiModelProperty("设备ID")
    private Long id;

    @ApiModelProperty("设备名称")
    private String name;
}
