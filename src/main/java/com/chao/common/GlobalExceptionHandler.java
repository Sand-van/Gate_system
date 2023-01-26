package com.chao.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, ControllerAdvice.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler
{
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ReturnMessage<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        if (ex.getMessage().contains("Duplicate entry"))
        {
            String[] split = ex.getMessage().split(" ");
            String message = split[2];
            return ReturnMessage.error(message + "已存在");
        }

        return ReturnMessage.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public ReturnMessage<String> exceptionHandler(CustomException ex)
    {
        return ReturnMessage.error(ex.getMessage());
    }
}

























