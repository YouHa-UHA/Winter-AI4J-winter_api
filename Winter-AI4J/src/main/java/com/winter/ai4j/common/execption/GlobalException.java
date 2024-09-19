package com.winter.ai4j.common.execption;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: GlobalException
 * <p>
 *
 * </p >
 *
 * @author wyh
 * Date:
 */
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotLoginException.class)
    public SaResult handlerException(NotLoginException e, HttpServletResponse response) {
        response.setStatus(208);
        return SaResult.error("未登录");
    }

}
