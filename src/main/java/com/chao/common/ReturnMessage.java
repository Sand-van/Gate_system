package com.chao.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回对象
 * @param <T>
 */
@Data
public class ReturnMessage<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    public static <T> ReturnMessage<T> success(T object) {
        ReturnMessage<T> r = new ReturnMessage<T>();
        r.data = object;
        r.code = 1;
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
