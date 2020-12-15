package com.hebtu.havefun.entity.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/15 9:59
 * @projectName HaveFun
 * @className MyExceptionHandler.java
 * @description TODO
 */
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        System.out.println("未知异常！原因是:" + e);
        return e.getMessage();
    }
}
