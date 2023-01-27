package com.chao.dto;

import com.chao.entity.UserApply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "用户申请Dto", description = "用户申请DTO类")
public class UserApplyDto extends UserApply
{
    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("设备名")
    private String deviceName;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
}
