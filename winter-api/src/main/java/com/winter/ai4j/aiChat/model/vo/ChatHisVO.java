
package com.winter.ai4j.aiChat.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: ChatHisPO
 * <blockquote><pre>
 * Description: [聊天历史]
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
public class ChatHisVO {

    @ApiModelProperty(value = "角色")
    @JSONField(name = "role")
    String role;

    @ApiModelProperty(value = "类型")
    @JSONField(name = "type")
    String type;

    @ApiModelProperty(value = "引用")
    @JSONField(name = "quote")
    String quote;

    @ApiModelProperty(value = "内容")
    @JSONField(name = "content")
    String content;

    @ApiModelProperty(value = "内容类型")
    @JSONField(name = "content_type")
    String contentType;

    @ApiModelProperty(value = "额外信息")
    @JSONField(name = "extra_info")
    String extraInfo;

    @ApiModelProperty(value = "是否喜欢")
    @JSONField(name = "if_like")
    String ifLike;

    @ApiModelProperty(value = "聊天历史ID")
    @JSONField(name = "chat_his_id")
    String chatHisId;

}
