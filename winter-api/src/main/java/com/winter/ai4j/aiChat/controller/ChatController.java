package com.winter.ai4j.aiChat.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.model.vo.ChatHisVO;
import com.winter.ai4j.aiChat.model.vo.ChatVO;
import com.winter.ai4j.aiChat.model.vo.FollowVO;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.result.Result;
import com.winter.ai4j.user.model.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
     *
     * @return 创建chat 结果
     */
    @ApiOperation(value = "chat-创建会话", notes = "创建对话")
    @PostMapping(value = "/create")
    public Result<String> createCoze() {
        // TODO 未登录处理 优化成直接抛出异常
        String userId = StpUtil.getLoginIdDefaultNull() != null ? StpUtil.getLoginIdAsString() : "error";
        if("error".equals(userId)){
            throw new NotLoginException("未登录", NotLoginException.NOT_TOKEN , NotLoginException.NOT_TOKEN_MESSAGE);
        }
        String chat = chatByCoseService.createChat();
        return Result.ok(chat);
    }


    /**
     * Chat-进行对话
     *
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
        if("error".equals(userId)){
            throw new NotLoginException("未登录", NotLoginException.NOT_TOKEN , NotLoginException.NOT_TOKEN_MESSAGE);
        }

        // 创建SseEmitter对象，注意这里的timeout是发送时间，不是超时时间，网上的文档有问题
        SseEmitter emitter = new SseEmitter(1800000L);
        emitter.onCompletion(() -> {
        });
        emitter.onTimeout(() -> {
        });
        UserDTO userDTO = UserDTO.builder().phone(userId).build();
        // 处理未正常提供chatId的情况
        if (!StringUtils.hasText(question.getChatId())) {
            try {
                String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
                // 构造并反馈提示
                ChatVO chatVO = ChatVO.builder().isFinish(false).userId(null)
                        .answer("请正确创建提问并刷新网页").chatId(null).date(formatted)
                        .build();
                String sendData = JSON.toJSONString(chatVO);
                emitter.send(SseEmitter.event().data(sendData));
                // 结束
                chatVO.setIsFinish(true);
                sendData = JSON.toJSONString(chatVO);
                emitter.send(SseEmitter.event().data(sendData));
                emitter.complete();
                return emitter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 处理未正常提供问题的情况
        if (!StringUtils.hasText(question.getQuestion())) {
            question.setQuestion("你好，请介绍一下自己吧。并告诉我，你可以做什么？");
        }

        String answer = chatByCoseService.question(emitter, question, userDTO);
        // chatByLlamaService.questionDTO(emitter); // ollama 存在问题，先不要用
        return emitter;
    }


    /**
     * Chat-联系问题
     *
     * @param question 用户ID
     * @return 联系问题结果
     */
    @ApiOperation(value = "chat-联系问题", notes = "联系问题")
    @PostMapping(value = "/follow")
    public Result<FollowVO> follow(@RequestBody QuestionDTO question) {
        FollowVO follow = chatByCoseService.getFollow(question);
        return Result.ok(follow);
    }



    /**
     * Chat-查询历史列表
     *
     * @param question 用户ID
     * @return 查询对话历史结果
     */
    @ApiOperation(value = "FoxAI-查询历史列表", notes = "FoxAI-查询历史列表")
    @PostMapping(value = "/list")
    public Result<List<ChatHisVO>> list() {
        String userId = StpUtil.getLoginIdDefaultNull() != null ? StpUtil.getLoginIdAsString() : "error";
        if("error".equals(userId)){
            throw new NotLoginException("未登录", NotLoginException.NOT_TOKEN , NotLoginException.NOT_TOKEN_MESSAGE);
        }
        List<ChatHisVO> result = chatByCoseService.listHistory(userId);
        return Result.ok(result);
    }



    /**
     * Chat-查询对话历史
     *
     * @param question 用户ID
     * @return 查询对话历史结果
     */
    @ApiOperation(value = "FoxAI-查询对话历史", notes = "FoxAI-查询对话历史")
    @PostMapping(value = "/query")
    public Result<List<ChatHisVO>> query(@RequestBody QuestionDTO question) {
        String userId = StpUtil.getLoginIdDefaultNull() != null ? StpUtil.getLoginIdAsString() : "error";
        if("error".equals(userId)){
            throw new NotLoginException("未登录", NotLoginException.NOT_TOKEN , NotLoginException.NOT_TOKEN_MESSAGE);
        }
        String chatId = question.getChatId();
        List<ChatHisVO> result = chatByCoseService.queryHistory(chatId, userId);
        return Result.ok(result);
    }


}
