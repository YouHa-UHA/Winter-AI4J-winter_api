package com.winter.ai4j.aiChat.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.ai4j.aiChat.mapper.ApiKeyMapper;
import com.winter.ai4j.aiChat.model.coze.CozeCreateRes;
import com.winter.ai4j.aiChat.model.coze.CozeQueReq;
import com.winter.ai4j.aiChat.model.coze.CozeQueRes;
import com.winter.ai4j.aiChat.model.coze.CozeRes;
import com.winter.ai4j.aiChat.model.dto.*;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.aiChat.model.vo.ChatVO;
import com.winter.ai4j.aiChat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * ClassName: ChatByLlamaServiceImpl
 * <blockquote><pre>
 * Description: [基本会话的Coze实现]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/8/13 下午3:27
 * @version 1.0.0
 * @since 1.0.0
 */
@Primary
@Slf4j
@Service
public class ChatByCozeServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyPO> implements ChatService {

    @Autowired
    private RedissonClient redissonClient;

    // 存储appKey
    private Map<String, ApiKeyPO> apiKeys;

    // 启动执行注解
    @PostConstruct
    public void init() {
        apiKeys = new HashMap<>();
        // 查询所有的appKey
        LambdaQueryWrapper<ApiKeyPO> apiKeyPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apiKeyPOLambdaQueryWrapper.eq(ApiKeyPO::getModelIndex, "coze");
        this.list(apiKeyPOLambdaQueryWrapper).forEach(apiKeyPO -> apiKeys.put(apiKeyPO.getAppIndex(), apiKeyPO));
        log.info("初始化 ===> Coze模型信息载入...");
        //增加判空，避免接口错误
        if (apiKeys.isEmpty()) {
            log.error("初始化失败 ===> 无信息读取");
        }
    }


    /*
     * 创建会话
     * */
    @Override
    public String createChat() {
        // TODO 后边增加一个全局异常的链接类,直接捕获全局返回模型丢失
        //增加判空，避免接口错误
        if (apiKeys.get("ai_coze_create") == null) {
            log.info("无法找到Coze对应的API Key");
            return "无法找到Coze对应的API Key";
        }
        ApiKeyPO cozeCreat = apiKeys.get("ai_coze_create");
        OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = RequestBody.create("",
                MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.coze.cn/v1/conversation/create")
                .post(requestBody)
                .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeCreat.getModelKey())
                .build();
        try (Response execute = HTTP_CLIENT.newCall(request).execute()) {
            if (execute.isSuccessful()) {
                String responseString = null;
                if (execute.body() != null) {
                    responseString = execute.body().string();
                }
                // TypeReference是fastjson提供的一个类，用于实现泛型的反序列化。
                CozeRes<CozeCreateRes> cozeRes = JSON.parseObject
                        (responseString, new TypeReference<CozeRes<CozeCreateRes>>() {});
                log.info("创建会话成功:{}", cozeRes);
                // Optional.ofNullable(T t) 方法的作用是判断t是否为null，
                // 中间任何一步为空都会返回一个空的Optional对象，不会抛出空指针异常。
                return Optional.ofNullable(cozeRes)
                        .map(CozeRes::getData)
                        .map(CozeCreateRes::getId)
                        .orElse(null);
            }
        } catch (IOException e) {
            log.error("创建会话失败", e);
            return null;
        }
        return null;
    }


    /*
     * 进行会话
     * */
    @Override
    public String question(SseEmitter emitter, QuestionDTO question) {
        // 获取对应的Agent对象
        ApiKeyPO apiKeyPO = apiKeys.get(question.getAppIndex());
        // 创建OkHttpClient对象
        OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();
        // 构建复合请求体
        CozeQueReq.CozeQueReqMessage cozeQueReqMessage = CozeQueReq.CozeQueReqMessage.builder().role("user")
                .type("question").content(question.getQuestion()).content_type("text").build();
        List<CozeQueReq.CozeQueReqMessage> additionalMessages = new ArrayList<>();
        additionalMessages.add(cozeQueReqMessage);
        CozeQueReq cozeQueReq = CozeQueReq.builder().bot_id(apiKeyPO.getAgentId()).user_id("123456789")
                .stream(true).auto_save_history(true).additional_messages(additionalMessages).build();
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(cozeQueReq), MediaType.parse("application/json"));

        // 构建请求地域性
        Request request = new Request.Builder().url(apiKeyPO.getUrl() + "?conversation_id=" + question.getChatId())
                .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyPO.getModelKey())
                .post(requestBody).build();

        // 进行连接与消息的接收，传入请求与监听器
        RealEventSource realEventSource = new RealEventSource(request, new CozeEventSourceListener(question, emitter));
        // 连接
        realEventSource.connect(HTTP_CLIENT);
        return null;
    }


    /*
    * 监听器
    * */
    public class CozeEventSourceListener extends EventSourceListener {

        private final QuestionDTO question;
        private SseEmitter emitter;

        public CozeEventSourceListener(QuestionDTO question, SseEmitter emitter) {
            this.question = question;
            this.emitter = emitter;
        }
        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        }
        @Override
        public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
            // 判断消息类型(这是coze的约定)
            if ("error".equals(type)) {
                onFailure(eventSource, new RuntimeException("error"), null);
                return;
            }
            if ("done".equals(type)) {
                return;
            }
            // 接收消息转换
            CozeQueRes cozeResponseWrapper = JSON.parseObject(data, CozeQueRes.class);
            String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
            String chatId = question.getChatId();
            if ("conversation.message.delta".equals(type)) {
                String answerContent = cozeResponseWrapper.getContent();
                ChatVO chatVO = ChatVO.builder().isFinish(false).userId(null).answer(answerContent).chatId(chatId).date(formatted).build();
                String sendData = JSON.toJSONString(chatVO);
                sendEventDataToUser(emitter, eventSource, sendData);
            }

        }

        @Override
        public void onClosed(EventSource eventSource) {
            eventSource.cancel();
        }

        @Override
        public void onFailure(EventSource eventSource, Throwable t, Response response) {

        }
    }

    /*
    * 发送消息
    * */
    public void sendEventDataToUser(SseEmitter emitter, EventSource eventSource, String message) {
        try {
            // 发送消息
            emitter.send(SseEmitter.event().data(message));
        } catch (IOException e) {
            eventSource.cancel();
        }
    }

}
