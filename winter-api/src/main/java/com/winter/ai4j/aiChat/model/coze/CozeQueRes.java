package com.winter.ai4j.aiChat.model.coze;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: CozeQueRes
 * <blockquote><pre>
 * Description: [Coze请求提问回答体]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/6/27 上午9:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CozeQueRes {

    String id;

    @JSONField(name = "conversation_id")
    String conversationId;

    @JSONField(name = "bot_id")
    String botId;

    String role;

    String type;

    String content;

    @JSONField(name = "content_type")
    String contentType;

    @JSONField(name = "chat_id")
    String chatId;

}
