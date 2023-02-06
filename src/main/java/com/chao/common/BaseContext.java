package com.chao.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户ID
 */
public class BaseContext
{
    private static ThreadLocal<UserSimpleInfo> threadLocal = new ThreadLocal<>();

    public static void setCurrentUserInfo(Long userID, Integer userType)
    {
        threadLocal.set(new UserSimpleInfo(userID, userType));
    }

    public static UserSimpleInfo getCurrentUserInfo()
    {
        return threadLocal.get();
    }
}
