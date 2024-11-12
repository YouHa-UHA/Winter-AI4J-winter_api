package com.winter.ai4j.common.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.winter.ai4j.common.constant.ResultCodeEnum;
import com.winter.ai4j.common.execption.BusinessException;
import com.winter.ai4j.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * ClassName: GlobalExceptionHandler
 * <p>
 *
 * </p >
 *
 * @author wyh
 * Date:
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result handlerException(NotLoginException e, HttpServletResponse response) {
        return Result.custom(null,ResultCodeEnum.BAD_UNAUTHORIZED.getCode(),ResultCodeEnum.BAD_UNAUTHORIZED.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result error(HttpMessageNotReadableException e){
        e.printStackTrace();
        return Result.fail("参数错误");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result error(BusinessException e,HttpServletResponse response){
        // response.setStatus(208);
        return Result.fail(e.getMessage());
    }


}
