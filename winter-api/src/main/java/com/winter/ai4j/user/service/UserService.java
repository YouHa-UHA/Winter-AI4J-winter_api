package com.winter.ai4j.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winter.ai4j.user.model.entity.UserPO;

/**
 * ClassName: UserService
 * <blockquote><pre>
 * Description: [用户信息接口]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/23 14:31
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserService extends IService<UserPO> {

    /*
    * 用户登录
    * */
    String login(UserPO req);

}
