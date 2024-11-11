package com.winter.ai4j.common.result;


import com.winter.ai4j.common.constant.ResultCodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 序列化版本号
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 构造函数
     */
    public Result() {
    }

    /**
     * 构建返回结果
     *
     * @param data    返回数据
     * @param code    返回码
     * @param message 返回消息
     * @param <T>     返回数据类型
     * @return Result<T> 返回结果
     */
    private static <T> Result<T> build(T data, Integer code, String message) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(code);
        result.setMsg(message);
        return result;
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> fail() {
        return fail(null);
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.BAD_REQUEST.getCode(), ResultCodeEnum.BAD_REQUEST.getMessage());
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> fail(T data, String message) {
        return build(data, ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 构建返回结果
     *
     * @param data     返回数据
     * @param codeEnum 返回码枚举
     * @param <T>      返回数据类型
     * @return Result<T> 返回结果
     */
    public static <T> Result<T> custom(T data, Integer code, String message) {
        return build(data, code, message);
    }

    /**
     * 设置返回消息
     *
     * @param msg 消息内容
     * @return Result<T> 当前对象，用于链式调用
     */
    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 设置返回码
     *
     * @param code 返回码
     * @return Result<T> 当前对象，用于链式调用
     */
    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 判断操作是否成功
     *
     * @return boolean 如果成功返回true，否则返回false
     */
    public boolean isOk() {
        return Objects.equals(this.getCode(), ResultCodeEnum.SUCCESS.getCode());
    }

}