package com.winter.ai4j.aiChat.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.ai4j.aiChat.mapper.ApiKeyMapper;
import com.winter.ai4j.aiChat.model.dto.CozeCreateResDTO;
import com.winter.ai4j.aiChat.model.dto.CozeResDTO;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.aiChat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
                CozeResDTO<CozeCreateResDTO> cozeResDTO = JSON.parseObject
                        (responseString, new TypeReference<CozeResDTO<CozeCreateResDTO>>() {});
                log.info("创建会话成功:{}", cozeResDTO);
                // Optional.ofNullable(T t) 方法的作用是判断t是否为null，
                // 中间任何一步为空都会返回一个空的Optional对象，不会抛出空指针异常。
                return Optional.ofNullable(cozeResDTO)
                        .map(CozeResDTO::getData)
                        .map(CozeCreateResDTO::getId)
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
    public String proceedChat(SseEmitter emitter) {
        return null;
    }
}
