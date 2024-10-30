package com.winter.ai4j.aiChat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.ai4j.aiChat.mapper.ChatListMapper;
import com.winter.ai4j.aiChat.model.entity.ChatListPO;
import com.winter.ai4j.aiChat.service.ChatListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * ClassName: ChatListServiceImpl
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/10/23 15:53
 * @version 1.0.0
 * @since 1.0.0
 */
@Primary
@Slf4j
@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatListPO> implements ChatListService {
}
