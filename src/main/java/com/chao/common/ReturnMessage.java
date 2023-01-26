package com.chao.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回对象
 * @param <T>
 */
@Data
@ApiModel(value = "通用返回类", description = "通用返回信息实体类")
public class ReturnMessage<T> {

    @ApiModelProperty(value = "状态码")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("错误信息")
    private String msg; //错误信息

    @ApiModelProperty("数据")
    private T data; //数据

    @ApiModelProperty("动态数据")
    private Map map = new HashMap(); //动态数据

    public static <T> ReturnMessage<T> success(T object) {
        ReturnMessage<T> r = new ReturnMessage<T>();
        r.data = object;
        r.code = 100;
        return r;
    }

    public static <T> ReturnMessage<T> error(String msg) {
        ReturnMessage<T> r = new ReturnMessage<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public ReturnMessage<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
