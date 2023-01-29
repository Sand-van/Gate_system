package com.chao.dto;

import com.chao.entity.AdminAuthority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("管理员权限DTO类")
public class AdminAuthorityDto extends AdminAuthority
{
    @ApiModelProperty("管理员姓名")
    private String adminName;

    @ApiModelProperty("设备名")
    private String deviceName;

    public String getAdminName()
    {
        return adminName;
    }

    public void setAdminName(String adminName)
    {
        this.adminName = adminName;
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
