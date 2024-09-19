package com.winter.ai4j.common.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取登录用户信息类
 */
public class AuthContext {

    /**
     * 获取当前登录用户id
     * @param request
     * @return
     */
    public static String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return StringUtils.isEmpty(userId) ? "" : userId;
    }

    /**
     * 获取当前未登录临时用户id
     * @param request
     * @return
     */
    public static String getUserTempId(HttpServletRequest request) {
        String userTempId = request.getHeader("userTempId");
        return StringUtils.isEmpty(userTempId) ? "" : userTempId;
    }
}
