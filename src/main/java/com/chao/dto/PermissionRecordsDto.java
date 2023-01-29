package com.chao.dto;

import com.chao.entity.PermissionRecords;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("通行记录DTO类")
public class PermissionRecordsDto extends PermissionRecords
{
    @ApiModelProperty("用户名")
    private String UserName;

    @ApiModelProperty("设备名")
    private String DeviceName;

    public String getUserName()
    {
        return UserName;
    }

    public void setUserName(String userName)
    {
        UserName = userName;
    }

    public String getDeviceName()
    {
        return DeviceName;
    }

    public void setDeviceName(String deviceName)
    {
        DeviceName = deviceName;
    }
}
