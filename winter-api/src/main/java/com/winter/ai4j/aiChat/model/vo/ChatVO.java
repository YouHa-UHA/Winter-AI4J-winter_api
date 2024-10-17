package com.winter.ai4j.aiChat.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: ChatVO
 * <blockquote><pre>
 * Description: [统一请求回复]
 * </pre></blockquote>
 *
 * @author
 * Date: 2024/9/19 上午9:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatVO implements Serializable {

    @ApiModelProperty(value = "会话ID")
    @JSONField(name = "chat_id")
    String chatId;

    @ApiModelProperty(value = "用户ID")
    @JSONField(name = "message_id")
    String userId;

    @ApiModelProperty(value = "回答")
    @JSONField(name = "answer")
    String answer;

    @ApiModelProperty(value = "时间")
    @JSONField(name = "date")
    String date;

    @ApiModelProperty(value = "唯一标识")
    @JSONField(name = "code")
    Integer code;

    @ApiModelProperty(value = "约定结束")
    @JSONField(name = "is_finish")
    Boolean isFinish;

}
