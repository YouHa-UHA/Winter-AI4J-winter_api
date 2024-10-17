package com.winter.ai4j.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.ai4j.aiChat.mapper.ApiKeyMapper;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.user.mapper.UserMapper;
import com.winter.ai4j.user.model.dto.UserDTO;
import com.winter.ai4j.user.model.entity.UserPO;
import com.winter.ai4j.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: UserServiceImpl
 * <blockquote><pre>
 * Description: [用户信息实现类]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/23 14:31
 * @version 1.0.0
 * @since 1.0.0
 */
@Primary
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO>  implements UserService {


    @Override
    public String login(UserDTO req) {

        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPO::getPhone, req.getPhone());
        UserPO userPo = this.getOne(queryWrapper);

        if (userPo == null
                || userPo.getPhone().isEmpty()
                || userPo.getPassword().isEmpty()) {
            // TODO 之后改造直接抛出异常，然后在直接返回错误
            return null;
        }

        return userPo.getPhone();


    }
}
