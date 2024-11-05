package com.winter.ai4j.common.constant;

import lombok.Getter;

/**
 * ClassName: RedisCodeEnum
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/11/5 17:31
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public enum RedisCodeEnum implements CodeEnum{

    REDIS(101,"chat");

    private final Integer code;
    private final String message;

    private RedisCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
