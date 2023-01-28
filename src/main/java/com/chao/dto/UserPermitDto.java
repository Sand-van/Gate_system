package com.chao.dto;

import com.chao.entity.UserPermit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用户通行权限DTO类")
public class UserPermitDto extends UserPermit
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
