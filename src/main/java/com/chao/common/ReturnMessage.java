package com.chao.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
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
    private int code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("错误信息")
    private String msg; //信息

    @ApiModelProperty("token")
    private String token;

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
     * 返回一个带有token的成功信息
     * @param object 要传递的信息
     * @return 封装好的信息js
     */
    public static <T> ReturnMessage<T> successWithToken(T object)
    {
        ReturnMessage<T> r = success(object);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", BaseContext.getCurrentUserInfo().getUserID());
        map.put("type", BaseContext.getCurrentUserInfo().getUserType());

        r.token = Jwts.builder().setIssuedAt(new Date())
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))   //60分钟过期
                .signWith(SignatureAlgorithm.HS256, "tZe0M6")
                .compact();

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
        return error(430, msg);
    }

    /**
     * token过期错误
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> tokenOutDateError(String msg)
    {
        return error(431, msg);
    }

    /**
     * 禁止错误,服务器理解请求客户端的请求，但是拒绝执行此请求
     * @param msg 错误信息
     * @return 通用信息类
     */
    public static <T> ReturnMessage<T> forbiddenError(String msg)
    {
        return error(403, msg);
    }

    public ReturnMessage<T> add(String key, Object value)
    {
        this.map.put(key, value);
        return this;
    }
}
