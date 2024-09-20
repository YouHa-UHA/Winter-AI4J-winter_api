package com.winter.ai4j.common.execption;

import com.winter.ai4j.common.constant.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 */
@Data
public class BusinessException extends RuntimeException {

    private Integer code;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
