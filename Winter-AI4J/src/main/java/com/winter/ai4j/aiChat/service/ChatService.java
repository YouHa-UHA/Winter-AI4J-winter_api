package com.winter.ai4j.aiChat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ClassName: ChatService
 * <blockquote><pre>
 * Description: [基本会话的接口，分别指向不同的模型实现]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/8/13 下午3:26
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ChatService extends IService<ApiKeyPO> {

    /*
    * 创建会话
    * */
    String createChat();

    /*
    * 进行会话
    * */
    String proceedChat(SseEmitter emitter);

}
