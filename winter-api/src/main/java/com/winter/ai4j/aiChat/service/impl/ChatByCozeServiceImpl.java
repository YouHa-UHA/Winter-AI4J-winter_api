package com.winter.ai4j.aiChat.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.winter.ai4j.aiChat.mapper.ApiKeyMapper;
import com.winter.ai4j.aiChat.model.coze.CozeCreateRes;
import com.winter.ai4j.aiChat.model.coze.CozeQueReq;
import com.winter.ai4j.aiChat.model.coze.CozeQueRes;
import com.winter.ai4j.aiChat.model.coze.CozeRes;
import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.aiChat.model.entity.ChatHisPO;
import com.winter.ai4j.aiChat.model.entity.ChatHistoryPO;
import com.winter.ai4j.aiChat.model.entity.ChatListPO;
import com.winter.ai4j.aiChat.model.vo.ChatHisVO;
import com.winter.ai4j.aiChat.model.vo.ChatVO;
import com.winter.ai4j.aiChat.model.vo.FollowVO;
import com.winter.ai4j.aiChat.service.ChatHistoryService;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.constant.ResultCodeEnum;
import com.winter.ai4j.common.execption.BusinessException;
import com.winter.ai4j.common.model.BaseDTO;
import com.winter.ai4j.common.model.BaseVO;
import com.winter.ai4j.common.util.GZipUtil;
import com.winter.ai4j.user.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
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

    @Lazy
    @Autowired
    private ChatHistoryService chatHistoryService;

    @Lazy
    @Autowired
    private ChatListServiceImpl ChatListService;

    @Autowired
    private RedissonClient redissonClient;

    // 存储appKey
    private Map<String, ApiKeyPO> apiKeys;

    String API_KEY;

    // 启动执行注解
    @PostConstruct
    public void init() {
        apiKeys = new HashMap<>();
        // 查询所有的appKey
        LambdaQueryWrapper<ApiKeyPO> apiKeyPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apiKeyPOLambdaQueryWrapper.eq(ApiKeyPO::getModelIndex, "coze");
        this.list(apiKeyPOLambdaQueryWrapper).forEach(apiKeyPO -> apiKeys.put(apiKeyPO.getAppIndex(), apiKeyPO));
        log.info("初始化 ===> Coze模型信息载入...");
        // 增加判空，避免接口错误
        if (apiKeys.isEmpty()) {
            log.error("初始化失败 ===> 无信息读取");
        }
        API_KEY = apiKeys.get("ai_coze").getModelKey();
    }


    /*
     * 创建会话
     * */
    @Override
    public String createChat(String userId) {
        if (apiKeys.get("ai_coze") == null) {
            throw new BusinessException("无法找到对应的API Key", ResultCodeEnum.FAIL.getCode());
        }

        RList<String> chatListClient = redissonClient.getList("chat:" + userId + ":list");
        // 检查是不是已经有创建的新会话还没有用过的新会话
        if (!chatListClient.isEmpty()) {
            ChatListPO chatListPO = JSON.parseObject(chatListClient.get(0), ChatListPO.class);
            return chatListPO.getChatId();
        }

        ApiKeyPO cozeCreat = apiKeys.get("ai_coze");
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
                String responseString = execute.body() != null ? execute.body().string() : null;
                // TypeReference是fastjson提供的一个类，用于实现泛型的反序列化。
                CozeRes<CozeCreateRes> cozeRes = JSON.parseObject
                        (responseString, new TypeReference<CozeRes<CozeCreateRes>>() {
                        });
                log.info("创建会话成功:{}", cozeRes);
                // Optional.ofNullable(T t) 方法的作用是判断t是否为null，
                // 中间任何一步为空都会返回一个空的Optional对象，不会抛出空指针异常
                String result = Optional.ofNullable(cozeRes)
                        .map(CozeRes::getData)
                        .map(CozeCreateRes::getId)
                        .orElse(null);

                String oldChartId = (String) redissonClient.getBucket("current:" + userId).get();
                // 检查是否发生了对话切换
                if (!StringUtils.isEmpty(oldChartId)) {
                    // 发生了对话切换，需要重新载入历史记录，先覆盖当前的
                    RList<String> rChatHistoryString = redissonClient.getList("chat_his:" + oldChartId);
                    // 更新历史记录
                    LambdaQueryWrapper<ChatHistoryPO> oldChatHistoryWrapper = new LambdaQueryWrapper<>();
                    oldChatHistoryWrapper.eq(ChatHistoryPO::getPhone, userId)
                            .eq(ChatHistoryPO::getChatId, oldChartId);
                    ChatHistoryPO oldChatHistoryPO = chatHistoryService.getOne(oldChatHistoryWrapper);
                    oldChatHistoryPO.setCompressedData(GZipUtil.compressString(JSON.toJSONString(rChatHistoryString)));
                    chatHistoryService.saveOrUpdate(oldChatHistoryPO);
                    // 清空当前历史
                    rChatHistoryString.clear();
                }

                ChatListPO chatListPO = ChatListPO.builder().phone(userId).chatId(result)
                        .chatName("新会话").time(LocalDateTime.now()).build();
                // 存入redis作为新会话
                String chatListStr = JSON.toJSONString(chatListPO);
                chatListClient.add(chatListStr);
                redissonClient.getBucket("current:" + userId).set(result);

                // 会话历史先载入mysql
                ChatHistoryPO chatHistoryPO = ChatHistoryPO.builder()
                        .chatName("新会话").phone(userId).chatId(result)
                        .compressedData(null)
                        .build();
                chatHistoryService.save(chatHistoryPO);

                return result;
            } else {
                throw new BusinessException("创建会话失败", ResultCodeEnum.FAIL.getCode());
            }
        } catch (IOException e) {
            log.error("创建会话失败", e);
            throw new BusinessException("创建会话失败", ResultCodeEnum.FAIL.getCode());
        }
    }


    /*
     * 进行会话
     * */
    @Override
    public String question(SseEmitter emitter, QuestionDTO question, UserDTO user) {

        // 检查是不是有新会话没有被同步到mysql
        RList<String> chatListClient = redissonClient.getList("chat:" + user.getPhone() + ":list");
        if (!chatListClient.isEmpty()) {
            for (String chatList : chatListClient) {
                ChatListPO chatListPO = JSON.parseObject(chatList, ChatListPO.class);
                chatListPO.setChatName(question.getQuestion().length() <= 10
                        ? question.getQuestion()
                        : question.getQuestion().substring(10));
                ChatListService.save(chatListPO);
            }
        }
        chatListClient.clear();

        ApiKeyPO apiKeyPO = apiKeys.get(question.getAppIndex());
        // 记录问题历史内容
        String chatId = question.getChatId();
        RList<String> chatHistoryString = redissonClient.getList("chat_his:" + chatId);
        ChatHisPO chatHisPO = ChatHisPO.builder()
                .role("user")
                .type("question")
                .content(question.getQuestion())
                .contentType("text")
                .ifLike("0")
                .chatHisId(String.valueOf(System.currentTimeMillis())).build();
        String questionJson = JSON.toJSONString(chatHisPO);
        chatHistoryString.add(questionJson);

        // 创建一个OkHttpClient对象
        OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();

        // 构造请求体
        CozeQueReq.CozeQueReqMessage cozeQueReqMessage = CozeQueReq.CozeQueReqMessage.builder()
                .role("user").type("question").content(question.getQuestion())
                .content_type("text").build();
        List<CozeQueReq.CozeQueReqMessage> additionalMessages = new ArrayList<>();
        additionalMessages.add(cozeQueReqMessage);
        CozeQueReq cozeQueReq = CozeQueReq.builder()
                .bot_id(apiKeyPO.getAgentId()).user_id(user.getPhone())
                .stream(true).auto_save_history(true)
                .additional_messages(additionalMessages)
                .build();

        RequestBody requestBody = RequestBody.create(JSON.toJSONString(cozeQueReq)
                , MediaType.parse("application/json"));

        String urlWithParams = UriComponentsBuilder.fromHttpUrl(apiKeyPO.getUrl())
                .queryParam("conversation_id", question.getChatId())
                .build().encode().toUri().toString();

        Request request = new Request.Builder().url(urlWithParams)
                .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyPO.getModelKey())
                .post(requestBody).build();

        // 进行连接与消息的接收，传入请求与监听器
        RealEventSource realEventSource = new RealEventSource
                (request, new CozeEventSourceListener(user, question, emitter));

        realEventSource.connect(HTTP_CLIENT);
        return null;
    }


    /*
     * 获取联想回答
     * */
    @Override
    public FollowVO getFollow(QuestionDTO question) {
        String chartId = question.getChatId();
        RList<String> list = redissonClient.getList("chat_follow:" + chartId);
        int size = list.size();
        List<String> safeSubList = new ArrayList<>(list.subList(Math.max(size - 3, 0), size));
        list.clear();
        return FollowVO.builder().answer(chartId).follow(safeSubList).build();
    }


    /*
     * 查询历史记录
     * */
    @Override
    public List<ChatHisVO> queryHistory(String chatId, String userId) {
        // 最终返回
        List<ChatHisVO> chartHistories = new ArrayList<>();
        List<String> chatHistoryString = new ArrayList<>();
        // 当前历史
        String oldChartId = (String) redissonClient.getBucket("current:" + userId).get();
        // 检查是否发生了对话切换
        if (!StringUtils.equals(chatId, oldChartId)) {
            // 发生了对话切换，但要先确定旧的对话是否有历史记录
            if(!StringUtils.isEmpty(oldChartId)){
                RList<String> rChatHistoryString = redissonClient.getList("chat_his:" + oldChartId);
                // 更新历史记录
                LambdaQueryWrapper<ChatHistoryPO> oldChatHistoryWrapper = new LambdaQueryWrapper<>();
                oldChatHistoryWrapper.eq(ChatHistoryPO::getPhone, userId)
                        .eq(ChatHistoryPO::getChatId, oldChartId);
                ChatHistoryPO oldChatHistoryPO = chatHistoryService.getOne(oldChatHistoryWrapper);
                oldChatHistoryPO.setCompressedData(GZipUtil.compressString(JSON.toJSONString(rChatHistoryString)));
                chatHistoryService.saveOrUpdate(oldChatHistoryPO);
                // 清空当前历史
                rChatHistoryString.clear();
            }

            // 读取新的历史记录
            LambdaQueryWrapper<ChatHistoryPO> ChatHistoryWrapper = new LambdaQueryWrapper<>();
            ChatHistoryWrapper.eq(ChatHistoryPO::getPhone, userId)
                    .eq(ChatHistoryPO::getChatId, chatId);
            ChatHistoryPO chatHistoryPO = chatHistoryService.getOne(ChatHistoryWrapper);
            if (chatHistoryPO == null) {
                chatHistoryPO = ChatHistoryPO.builder()
                        .phone(userId).chatId(chatId)
                        .compressedData(null)
                        .build();
                chatHistoryService.save(chatHistoryPO);
            }
            String jsonString = GZipUtil.decompressString(chatHistoryPO.getCompressedData());
            List<String> chatHisVOS = JSON.parseArray(jsonString, String.class);
            if(!CollectionUtils.isEmpty(chatHisVOS)){
                for (String message : chatHisVOS) {
                    ChatHisVO chatHisVO = JSON.parseObject(message, ChatHisVO.class);
                    chartHistories.add(chatHisVO);
                }
            }
            // 更新当前对话
            redissonClient.getBucket("current:" + userId).set(chatId);
            RList<String> list = redissonClient.getList("chat_his:" + chatId);
            list.addAll(chatHisVOS);
        } else {
            // 没有发生对话切换，直接读取redis中的历史记录
            RList<String> list = redissonClient.getList("chat_his:" + chatId);
            for (String s : list) {
                ChatHisVO chatHisVO = JSON.parseObject(s, ChatHisVO.class);
                chartHistories.add(chatHisVO);
            }
        }

        return chartHistories;
    }


    /*
     * 查询历史列表
     * */
    @Override
    public BaseVO<ChatListPO> listHistory(BaseDTO baseDTO, String userId) {
        // 分页查询
        Page<ChatListPO> page = new Page<>(baseDTO.getPageNum(), baseDTO.getPageSize());
        LambdaQueryWrapper<ChatListPO> chatListPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatListPOLambdaQueryWrapper.eq(ChatListPO::getPhone, userId);
        Page<ChatListPO> chatListPOS = ChatListService.page(page, chatListPOLambdaQueryWrapper);
        BaseVO<ChatListPO> listBaseVO = new BaseVO(chatListPOS);
        return listBaseVO;
    }


    /*
     * 监听器
     * */
    public class CozeEventSourceListener extends EventSourceListener {

        private final UserDTO user;
        private final QuestionDTO question;
        private final SseEmitter emitter;

        public CozeEventSourceListener(UserDTO user, QuestionDTO question, SseEmitter emitter) {
            this.user = user;
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

            CozeQueRes cozeQueRes = JSON.parseObject(data, CozeQueRes.class);
            String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
            String chatId = question.getChatId();

            if ("conversation.message.delta".equals(type)) {
                ChatVO chatVO = ChatVO.builder()
                        .chatId(chatId).isFinish(false).userId(null)
                        .answer(cozeQueRes.getContent()).date(formatted)
                        .build();
                String sendData = JSON.toJSONString(chatVO);

                // 中间过程载入redis
                RList<String> chatHistorySemi = redissonClient.getList("chat_intermediate:" + chatId);
                chatHistorySemi.add(cozeQueRes.getContent());

                sendEventDataToUser(emitter, eventSource, sendData, chatId, cozeQueRes);
            }

            if ("conversation.message.completed".equals(type) && "answer".equals(cozeQueRes.getType())) {
                // 最后一次是完整会话，所以可以不用中间过程，直接清空
                RList<String> chatHistorySemi = redissonClient.getList("chat_intermediate:" + chatId);
                chatHistorySemi.clear();
                String result = cozeQueRes.getContent();
                ChatHisPO chatHisPO = ChatHisPO.builder()
                        .role("assistant").type("answer").content(result)
                        .contentType("text").ifLike("0")
                        .chatHisId(String.valueOf(System.currentTimeMillis())).build();
                String answer = JSON.toJSONString(chatHisPO);
                redissonClient.getList("user_his:" + chatId).add(answer);
            }

            if ("conversation.message.completed".equals(type) && "follow_up".equals(cozeQueRes.getType())) {
                // 联想问题载入redis
                RList<String> list = redissonClient.getList("chat_follow:" + chatId);
                list.add(cozeQueRes.getContent());
            }

        }

        @Override
        public void onClosed(EventSource eventSource) {
            // 关闭消息
            closeEventToUser(emitter, question);
            eventSource.cancel();
        }

        @Override
        public void onFailure(EventSource eventSource, Throwable t, Response response) {
            RList<String> chatHistorySemi = redissonClient.getList("chat_his:" + question.getChatId());
            String result = String.join("", chatHistorySemi);
            chatHistorySemi.clear();
            eventSource.cancel();
            if (!isSseEmitterComplete(emitter)) {
                ChatHisPO chatHisPO = ChatHisPO.builder()
                        .role("assistant")
                        .type("answer")
                        .content(result + " \n系统繁忙，请您稍后重试，感谢您的耐心等待")
                        .contentType("text")
                        .ifLike("0")
                        .chatHisId(String.valueOf(System.currentTimeMillis())).build();
                String answerJson = JSON.toJSONString(chatHisPO);
                redissonClient.getList("user_his:" + question.getChatId()).add(answerJson);
                closeEventByModel(emitter, question, user);
            }
        }

    }


    /*
     * 发送消息
     * */
    public void sendEventDataToUser(SseEmitter emitter, EventSource eventSource, String message, String chatId, CozeQueRes cozeQueRes) {
        try {
            // 发送消息
            emitter.send(SseEmitter.event().data(message));
        } catch (IOException e) {
            log.error("主机中止连接情况[用户中止了链接，导致发送失败]");
            // 清空中间历史
            RList<String> chatHistorySemi = redissonClient.getList("chat_intermediate:" + chatId);
            String result = String.join("", chatHistorySemi);
            chatHistorySemi.clear();
            ChatHisPO chatHisPO = ChatHisPO.builder()
                    .role("assistant")
                    .type("answer")
                    .content(result)
                    .contentType("text")
                    .ifLike("0")
                    .chatHisId(String.valueOf(System.currentTimeMillis())).build();
            String answerJson = JSON.toJSONString(chatHisPO);
            // 维护历史记录
            redissonClient.getList("user_his:" + chatId).add(answerJson);
            closeEventByUser(chatId, cozeQueRes);
            eventSource.cancel();
        }
    }


    /*
     * 模型端意外关闭
     * */
    public void closeEventByModel(SseEmitter emitter, QuestionDTO question, UserDTO userDTO) {
        String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
        String chatId = question.getChatId();
        try {
            // 构造并反馈提示
            ChatVO chatVO = ChatVO.builder()
                    .isFinish(false)
                    .userId(null)
                    .answer(" \n系统繁忙，请您稍后重试，感谢您的耐心等待")
                    .chatId(chatId)
                    .date(formatted).build();
            String sendData = JSON.toJSONString(chatVO);
            emitter.send(SseEmitter.event().data(sendData));
            // 结束
            chatVO.setIsFinish(true);
            sendData = JSON.toJSONString(chatVO);
            emitter.send(SseEmitter.event().data(sendData));
            log.error("主机中止连接情况[模型服务关闭了链接] ===> {}", userDTO.getPhone());
        } catch (IOException e) {
            log.error("主机中止连接情况[模型服务关闭连接后，后台还未处理，用户也恰好关闭了链接] ===> {}", chatId);
        } finally {
            emitter.complete();
            // String lockKey = "BlockingsseEmitterLockKey_" + chatId;
            // RLock lock = redissonClient.getLock(lockKey);
            // lock.forceUnlock();
        }
    }


    /*
     * 用户关闭
     * */
    public void closeEventByUser(String chatId, CozeQueRes cozeQueRes) {
        // 主动请求关闭链接
        boolean b = closeCoze(cozeQueRes);
        // String lockKey = "BlockingsseEmitterLockKey_" + chatId;
        // RLock lock = redissonClient.getLock(lockKey);
        // lock.forceUnlock();
    }


    /*
     * 关闭对话请求（主动请求coze）
     * */
    public boolean closeCoze(CozeQueRes cozeQueRes) {
        OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Gson gson = new Gson();
        JsonObject body = new JsonObject();
        body.addProperty("chat_id", cozeQueRes.getChatId());
        body.addProperty("conversation_id", cozeQueRes.getConversationId());
        RequestBody requestBody = RequestBody.create(gson.toJson(body),
                MediaType.parse("application/json"));
        Request request = new Request.Builder().url("https://api.coze.cn/v3/chat/cancel")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY).build();
        // TODO 这个API_KEY要优化，改成直接从这里获取，以后要作为传入值放进来
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                return true;
            }
        } catch (IOException e) {
            log.error("取消对话失败:{}", e.getMessage());
            return false;
        }
        return true;
    }


    /*
     * 正常关闭消息
     * */
    public void closeEventToUser(SseEmitter emitter, QuestionDTO question) {
        String formatted = DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now());
        String chatId = question.getChatId();
        try {
            ChatVO chatVO = ChatVO.builder()
                    .isFinish(true)
                    .userId(null)
                    .answer("")
                    .chatId(chatId)
                    .date(formatted)
                    .build();
            String sendData = JSON.toJSONString(chatVO);
            // 判断emitter对象是否还在链接
            if (!isSseEmitterComplete(emitter)) {
                emitter.send(SseEmitter.event().data(sendData));
            }
        } catch (IOException e) {
            log.error("主机中止连接情况[模型端结束服务，响应结束时，用户也结束了服务] ===> {}", chatId);
        } finally {
            if (!isSseEmitterComplete(emitter)) {
                emitter.complete();
            }
            // String lockKey = "BlockingsseEmitterLockKey_" + chartId;
            // RLock lock = redissonClient.getLock(lockKey);
            // lock.forceUnlock();
        }

    }


    /*
     * SseEmitter是否完成
     * */
    public boolean isSseEmitterComplete(SseEmitter sseEmitter) {
        try {
            Field completeField = sseEmitter.getClass().getSuperclass().getDeclaredField("complete");
            completeField.setAccessible(true);
            return (boolean) completeField.get(sseEmitter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }


}
