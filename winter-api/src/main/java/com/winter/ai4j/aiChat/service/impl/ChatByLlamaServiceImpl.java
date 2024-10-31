package com.winter.ai4j.aiChat.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.winter.ai4j.aiChat.mapper.ApiKeyMapper;
import com.winter.ai4j.aiChat.model.dto.QuestionDTO;
import com.winter.ai4j.aiChat.model.entity.ApiKeyPO;
import com.winter.ai4j.aiChat.model.entity.ChatListPO;
import com.winter.ai4j.aiChat.model.vo.ChatHisVO;
import com.winter.ai4j.aiChat.model.vo.FollowVO;
import com.winter.ai4j.aiChat.service.ChatService;
import com.winter.ai4j.common.model.BaseDTO;
import com.winter.ai4j.common.model.BaseVO;
import com.winter.ai4j.user.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

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
public class ChatByLlamaServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyPO> implements ChatService {

    // // 存储appKey
    // private Map<String, ApiKeyPO> apiKeys;

    // // 启动执行注解
    // @PostConstruct
    // public void init() {
    //     apiKeys = new HashMap<>();
    //     // 查询所有的appKey
    //     LambdaQueryWrapper<ApiKeyPO> apiKeyPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //     apiKeyPOLambdaQueryWrapper.eq(ApiKeyPO::getCompany, "ollama");
    //     this.list(apiKeyPOLambdaQueryWrapper).forEach(apiKeyPO -> apiKeys.put(apiKeyPO.getAppIndex(), apiKeyPO));
    //     log.info("初始化 ===> Ollama模型信息载入...");
    //     //增加判空，避免接口错误
    //     if (apiKeys.isEmpty()) {
    //         log.error("初始化失败 ===> 无信息读取");
    //     }
    // }


    /*
     * 创建会话
     * */
    @Override
    public String createChat(String userId) {
        // // TODO 后边增加一个全局异常的链接类,直接捕获全局返回模型丢失
        // //增加判空，避免接口错误
        // if (apiKeys.get("ai_coze_create") == null) {
        //     log.info("无法找到Coze对应的API Key");
        //     return "无法找到Coze对应的API Key";
        // }
        // ApiKeyPO cozeCreat = apiKeys.get("ai_coze_create");
        // OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        //         .readTimeout(60, TimeUnit.SECONDS)
        //         .writeTimeout(60, TimeUnit.SECONDS)
        //         .connectTimeout(60, TimeUnit.SECONDS)
        //         .build();
        // RequestBody requestBody = RequestBody.create("",
        //         MediaType.parse("application/json"));
        // Request request = new Request.Builder()
        //         .url("https://api.coze.cn/v1/conversation/create")
        //         .post(requestBody)
        //         .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        //         .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeCreat.getModelKey())
        //         .build();
        // try (Response execute = HTTP_CLIENT.newCall(request).execute()) {
        //     if (execute.isSuccessful()) {
        //         String responseString = null;
        //         if (execute.body() != null) {
        //             responseString = execute.body().string();
        //         }
        //         // TypeReference是fastjson提供的一个类，用于实现泛型的反序列化。
        //         CozeRes<CozeCreateRes> cozeResDTO = JSON.parseObject
        //                 (responseString, new TypeReference<CozeRes<CozeCreateRes>>() {});
        //         log.info("创建会话成功:{}", cozeResDTO);
        //         // Optional.ofNullable(T t) 方法的作用是判断t是否为null，
        //         // 中间任何一步为空都会返回一个空的Optional对象，不会抛出空指针异常。
        //         return Optional.ofNullable(cozeResDTO)
        //                 .map(CozeRes::getData)
        //                 .map(CozeCreateRes::getId)
        //                 .orElse(null);
        //     }
        // } catch (IOException e) {
        //     log.error("创建会话失败", e);
        //     return null;
        // }
        return null;
    }

    /*
     * 进行会话
     * */
    @Override
    public String question(SseEmitter emitter, QuestionDTO question, UserDTO user) {
        // 创建OkHttpClient对象用于发送请求
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                // .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .retryOnConnectionFailure(true).build();
        // 创建请求体
        Gson gson = new Gson();
        JsonObject body = new JsonObject();
        body.addProperty("model", "llama3.1");
        body.addProperty("prompt", "你好");
        RequestBody requestBody = RequestBody.create(gson.toJson(body),
                MediaType.parse(APPLICATION_NDJSON_VALUE));
        // request请求对象，包含请求的URL、请求方法、请求头等信息
        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                // .addHeader(HttpHeaders.AUTHORIZATION, "Bearer ")
                .build();
        // TODO 回调有问题
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 处理请求失败的情况
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 处理响应
                if (response.isSuccessful()) {
                    // 处理服务器发送的事件数据
                    String eventData = response.body().string();
                    emitter.send(SseEmitter.event().data(eventData));
                    System.out.println("Received event data: " + eventData);
                } else {
                    // 处理响应失败的情况
                    System.out.println("Response failed: " + response.code());
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 在这里处理接收到的 SSE 事件数据
                        System.out.println(response);
                        emitter.send(SseEmitter.event().data(line));
                        // System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        // 创建sseListener对象，用于处理接收到的事件数据
        SSEListener sseListener = new SSEListener();
        // 创建realEventSource对象，用于接收服务器发送的事件数据
        RealEventSource realEventSource = new RealEventSource(request, sseListener);
        // 连接服务器
        realEventSource.connect(httpClient);
        return null;
    }

    @Override
    public FollowVO getFollow(QuestionDTO questionDTO) {
        return null;
    }

    @Override
    public List<ChatHisVO> queryHistory(String chatId, String userId) {
        return null;
    }

    @Override
    public BaseVO<ChatListPO> listHistory(BaseDTO baseDTO, String userId) {

        return null;
    }


    // SSEListener内部类，用于处理接收到的事件数据
    private static class SSEListener extends EventSourceListener {

        public SSEListener() {
            // 内部类传入参数
        }

        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            log.info("开始连接");
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
            log.info("接收事件");
        }

        @Override
        public void onClosed(EventSource eventSource) {
            log.info("关闭连接");

        }

        @Override
        public void onFailure(EventSource eventSource, Throwable t, Response response) {
            log.info("失败情况");
        }
    }


}
