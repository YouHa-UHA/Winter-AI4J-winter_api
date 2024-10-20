package com.winter.ai4j.user.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.winter.ai4j.user.model.dto.UserDTO;
import com.winter.ai4j.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserController
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/23 14:19
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/winter/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
     * 用户登录
     * */
    @ApiOperation(value = "login", notes = "login")
    @PostMapping(value = "/login")
    public SaResult login(@RequestBody UserDTO req) {

        if (StringUtils.isEmpty(req.getPhone()) || StringUtils.isEmpty(req.getPassword())) {
            return SaResult.error("请输入账号密码");
        }

        String userMobile = userService.login(req);
        if (StringUtils.hasText(userMobile)) {
            StpUtil.login(userMobile);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.data(tokenInfo);
        }

        return SaResult.error("账号或密码错误");

    }

}
