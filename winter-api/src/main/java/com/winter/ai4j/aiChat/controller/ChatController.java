package com.winter.ai4j.aiChat.controller;

import com.winter.ai4j.aiChat.model.dto.Question;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ClassName: ChatLlamaController
 * <blockquote><pre>
 * Description: [基本会话的控制层，由业务层指向不同的模型实现]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/8/12 下午5:10
 * @version 1.0.0
 * @since 1.0.0
 */
@Api(tags = "Coze模块")
@Slf4j
@RestController
@RequestMapping(value = "/system/coze")
public class ChatController {

    @Autowired
    @Qualifier("chatByCozeServiceImpl")
    private ChatService chatByCoseService;

    @Autowired
    @Qualifier("chatByLlamaServiceImpl")
    private ChatService chatByLlamaService;


    /**
     * Coze-创建会话
     * @param userID 用户ID
     * @return 创建chat 结果
     */
    @ApiOperation(value = "Coze-创建会话", notes = "创建对话")
    @PostMapping(value = "/create")
    public Result<String> createCoze(@RequestBody String userID) {
        String chat = chatByCoseService.createChat();
        return Result.ok(chat);
    }


    /**
     * Coze-进行对话
     * @return 进行对话结果
     */
    @ApiOperation(value = "Coze-进行对话", notes = "进行对话")
    @PostMapping(value = "/proceed")
    public SseEmitter proceedConversationCoze(@RequestBody Question question) {
        // llama 进行对话
        SseEmitter emitter = new SseEmitter(1800000L);
        emitter.onCompletion(() -> {
        });
        emitter.onTimeout(() -> {
        });
        // chatByLlamaService.proceedChat(emitter); // ollama 存在问题，先不要用
        return emitter;
    }




}
