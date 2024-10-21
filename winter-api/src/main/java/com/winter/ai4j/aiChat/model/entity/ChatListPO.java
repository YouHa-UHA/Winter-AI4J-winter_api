package com.winter.ai4j.aiChat.model.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: ChatListVO
 * <blockquote><pre>
 * Description: [会话历史列表]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/10/21 09:28
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatListPO {

    @ApiModelProperty(value = "会话ID")
    @JSONField(name = "chat_id")
    private String chatId;

    @ApiModelProperty(value = "会话名称")
    @JSONField(name = "chat_name")
    private String chatName;

    @ApiModelProperty(value = "对话时间")
    @JSONField(name = "time")
    private String time;

}
