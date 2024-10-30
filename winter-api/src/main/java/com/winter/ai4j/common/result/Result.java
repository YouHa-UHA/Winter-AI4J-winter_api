package com.winter.ai4j.common.result;


import com.winter.ai4j.common.constant.CodeEnum;
import com.winter.ai4j.common.constant.ResultCodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 全局统一返回结果类
 *
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;

    private String message;

    private T data;

    public Result() {
    }

    private static <T> Result<T> build(T data, Integer code, String message) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    public static <T> Result<T> fail() {
        return fail(null);
    }

    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL.getCode(), ResultCodeEnum.FAIL.getMessage());
    }

    public static <T> Result<T> fail(T data, String message) {
        return build(data, ResultCodeEnum.FAIL.getCode(), message);
    }

    public static <T> Result<T> custom(T data, Integer code, String message) {
        return build(data, code, message);
    }

    /**
     * 设置返回消息
     * @param msg 消息内容
     * @return Result<T> 当前对象，用于链式调用
     */
    public Result<T> setMessage(String msg) {
        this.message = msg;
        return this;
    }

    /**
     * 设置返回码
     * @param code 返回码
     * @return Result<T> 当前对象，用于链式调用
     */
    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 判断操作是否成功
     * @return boolean 如果成功返回true，否则返回false
     */
    public boolean isOk() {
        return Objects.equals(this.getCode(), ResultCodeEnum.SUCCESS.getCode());
    }

}