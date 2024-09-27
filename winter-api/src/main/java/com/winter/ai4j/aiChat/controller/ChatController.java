package com.winter.ai4j.aiChat.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.model.vo.ChatVO;
import com.winter.ai4j.aiChat.model.vo.FollowVO;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.result.Result;
import com.winter.ai4j.user.model.dto.UserDTO;
import com.winter.ai4j.user.model.entity.UserPO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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
@Api(tags = "Chat模块")
@Slf4j
@RestController
@RequestMapping(value = "/winter/chat")
public class ChatController {

    @Autowired
    @Qualifier("chatByCozeServiceImpl")
    private ChatService chatByCoseService;

    @Autowired
    @Qualifier("chatByLlamaServiceImpl")
    private ChatService chatByLlamaService;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * Chat-创建会话
     * @param userId 用户ID
     * @return 创建chat 结果
     */
    @ApiOperation(value = "chat-创建会话", notes = "创建对话")
    @PostMapping(value = "/create")
    public Result<String> createCoze(@RequestBody String userId) {
        String chat = chatByCoseService.createChat();
        return Result.ok(chat);
    }


    /**
     * Chat-进行对话
     * @param question 问题
     * @return 进行对话结果
     */
    @ApiOperation(value = "chat-进行对话", notes = "进行对话")
    @PostMapping(value = "/question")
    public SseEmitter chatByCoze(@RequestBody QuestionDTO question) {

        // TODO 后期载入分布式锁，防止用户发起多次提问
        // String lockKey = "ChatSSELock:" + question.getChatId();
        // RLock lock = redissonClient.getLock(lockKey);
        // lock.lock(100, TimeUnit.SECONDS);

        String userId = StpUtil.getLoginIdDefaultNull() != null ? StpUtil.getLoginIdAsString() : "error";
        UserDTO userDTO = new UserDTO();
        userDTO.setPhone(userId);

        // 处理未正常提供chatId的情况
        try {
            if (StringUtils.isEmpty(question.getChatId())) {
                SseEmitter emitter = new SseEmitter();
                String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
                ChatVO chatVO = ChatVO.builder().isFinish(true).userId(null)
                        .answer("请正确创建提问并刷新网页").chatId(null).date(formatted)
                        .build();
                String sendData = JSON.toJSONString(chatVO);
                emitter.send(SseEmitter.event().data(sendData));

                // chatVO.setIsFinish(true);
                // sendData = JSON.toJSONString(chatVO);
                // emitter.send(SseEmitter.event().data(sendData));

                emitter.complete();
                return emitter;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 创建SseEmitter对象，注意这里的timeout是发送时间，不是超时时间，网上的文档有问题
        SseEmitter emitter = new SseEmitter(1800000L);
        emitter.onCompletion(() -> {});
        emitter.onTimeout(() -> {});
        String question1 = chatByCoseService.question(emitter, question, userDTO);
        // chatByLlamaService.questionDTO(emitter); // ollama 存在问题，先不要用
        return emitter;
    }


    /**
     * Chat-联系问题
     * @param question 用户ID
     * @return 联系问题结果
     */
    @ApiOperation(value = "chat-联系问题", notes = "联系问题")
    @PostMapping(value = "/follow")
    public Result<FollowVO> follow(@RequestBody QuestionDTO question) {
        FollowVO follow = chatByCoseService.getFollow(question);
        return Result.ok(follow);
    }




}
