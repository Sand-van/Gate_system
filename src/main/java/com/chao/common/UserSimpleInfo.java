package com.chao.common;

public class UserSimpleInfo
{
    private Long userID;
    private Integer userType;

    public UserSimpleInfo(Long userID, Integer userType)
    {
        this.userID = userID;
        this.userType = userType;
    }

    public Long getUserID()
    {
        return userID;
    }

    public void setUserID(Long userID)
    {
        this.userID = userID;
    }

    public Integer getUserType()
    {
        return userType;
    }

    public void setUserType(Integer userType)
    {
        this.userType = userType;
    }
}
