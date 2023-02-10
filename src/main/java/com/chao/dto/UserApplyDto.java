package com.chao.dto;

import com.chao.entity.UserApply;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用户申请DTO类")
public class UserApplyDto extends UserApply
{
    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("用户账号")
    private String userAccount;

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

    public String getUserAccount()
    {
        return userAccount;
    }

    public void setUserAccount(String userAccount)
    {
        this.userAccount = userAccount;
    }
}
