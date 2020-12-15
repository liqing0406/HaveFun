package com.hebtu.havefun.entity.exception;

/**
 * @author PengHuAnZhi
 * @createTime 2020/12/15 10:01
 * @projectName HaveFun
 * @className BaseErrorInfoInterface.java
 * @description TODO
 */
public interface BaseErrorInfoInterface {
    /**
     * 错误码
     */
    String getResultCode();

    /**
     * 错误描述
     */
    String getResultMsg();
}