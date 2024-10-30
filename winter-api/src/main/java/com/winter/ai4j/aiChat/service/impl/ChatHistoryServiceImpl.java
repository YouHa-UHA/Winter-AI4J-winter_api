package com.winter.ai4j.aiChat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.ai4j.aiChat.mapper.ChatHistoryMapper;
import com.winter.ai4j.aiChat.model.entity.ChatHistoryPO;
import com.winter.ai4j.aiChat.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * ClassName: ChatHistoryServiceImpl
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/10/23 11:10
 * @version 1.0.0
 * @since 1.0.0
 */
@Primary
@Slf4j
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistoryPO> implements ChatHistoryService {


}
