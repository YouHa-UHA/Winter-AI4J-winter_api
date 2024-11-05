package com.winter.ai4j.common.constant;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum implements CodeEnum {

    // 2xx Success
    SUCCESS(200,"请求成功，返回所请求的资源"),
    SUCCESS_CREATED(201,"请求成功，新资源已经创建"),
    SUCCESS_ACCEPTED(202,"请求已经接受，等待处理"),
    SUCCESS_NO_CONTENT(204,"请求成功，但没有内容返回"),

    // 4xx Client Error
    BAD_REQUEST(400, "请求参数错误"),
    BAD_UNAUTHORIZED(401, "用户未授权，需要登录"),
    BAD_PAYMENT_REQUIRED(402, "需要付费"),
    BAD_FORBIDDEN(403, "用户被禁止访问"),
    BAD_NOT_FOUND(404, "请求的资源不存在");

    private final Integer code;
    private final String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
