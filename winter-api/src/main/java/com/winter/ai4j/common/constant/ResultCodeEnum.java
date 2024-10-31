package com.winter.ai4j.common.constant;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum implements CodeEnum {

    SUCCESS(200,"请求成功"),
    FAIL(201, "失败"),
    ILLEGAL_REQUEST( 204, "非法请求"),
    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SERVICE_ERROR(500, "服务异常");

    private final Integer code;
    private final String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
