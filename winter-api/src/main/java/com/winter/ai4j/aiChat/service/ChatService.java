package com.winter.ai4j.aiChat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.aiChat.model.entity.ChatListPO;
import com.winter.ai4j.aiChat.model.vo.ChatHisVO;
import com.winter.ai4j.aiChat.model.vo.FollowVO;
import com.winter.ai4j.user.model.dto.UserDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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

    /**
     * 创建会话
     * @param
     * @return
     */
    String createChat();

    /**
     * 进行会话
     * @param question
     * @return
     */
    String question(SseEmitter emitter, QuestionDTO question, UserDTO user);

    /**
     * 获取联想回复
     * @param
     * @return FollowVO
     */
    FollowVO getFollow(QuestionDTO question);

    /**
     * 查询历史记录
     * @param
     * @return List<ChatHisVO>
     */
    List<ChatHisVO> queryHistory(String chatId, String userId);

    /**
     * 查询历史记录
     * @param
     * @return List<ChatHisVO>
     */
    List<ChatListPO> listHistory(String userId);

}
