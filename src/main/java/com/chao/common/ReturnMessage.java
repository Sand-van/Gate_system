package com.chao.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回对象
 *
 * @param <T>
 */
@Data
@ApiModel(value = "通用返回类", description = "通用返回信息实体类")
public class ReturnMessage<T>
{

    @ApiModelProperty(value = "状态码")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("错误信息")
    private String msg; //错误信息

    @ApiModelProperty("数据")
    private T data; //数据

    @ApiModelProperty("动态数据")
    private Map<String, Object> map = new HashMap<>(); //动态数据

    public static <T> ReturnMessage<T> success(T object)
    {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.data = object;
        r.code = 200;
        return r;
    }

    /**
     * 自定义错误
     * @param code 错误码
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> error(int code, String msg)
    {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.msg = msg;
        r.code = code;
        return r;
    }

    /**
     * 普通错误,服务端显示对应的信息
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> commonError(String msg)
    {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.msg = msg;
        r.code = 430;
        return r;
    }

    /**
     * 未授权错误,请求要求用户的身份认证
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> unauthorizedError(String msg)
    {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.msg = msg;
        r.code = 401;
        return r;
    }

    /**
     * 禁止错误,服务器理解请求客户端的请求，但是拒绝执行此请求
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> forbiddenError(String msg)
    {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.msg = msg;
        r.code = 403;
        return r;
    }

    public ReturnMessage<T> add(String key, Object value)
    {
        this.map.put(key, value);
        return this;
    }
}
