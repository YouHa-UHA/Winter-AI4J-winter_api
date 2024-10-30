package com.winter.ai4j.aiChat.model.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: ChatHisPO
 * <blockquote><pre>
 * Description: [聊天历史-redis]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/27 15:09
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JSONType(orders = {"role", "type", "quote", "content", "content_type", "extra_info", "if_like", "chat_his_id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatHisPO {

    @JSONField(name = "role")
    String role;

    @JSONField(name = "type")
    String type;

    @JSONField(name = "quote")
    String quote;

    @JSONField(name = "content")
    String content;

    @JSONField(name = "content_type")
    String contentType;

    @JSONField(name = "extra_info")
    String extraInfo;

    @JSONField(name = "if_like")
    String ifLike;

    @JSONField(name = "chat_his_id")
    String chatHisId;

}
