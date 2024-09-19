package com.winter.ai4j.aiChat.model.dto;

import com.alibaba.fastjson2.annotation.JSONType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: CozeCreateRes
 * <blockquote><pre>
 * Description: [CozeCreateRes]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/6/27 上午9:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JSONType(orders={"created_at", "id"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Coze消息创建返回", description = "Coze消息创建返回")
public class CozeCreateResDTO {

    @ApiModelProperty(value = "创建时间", required = true)
    String created_at;

    @ApiModelProperty(value = "消息id", required = true)
    String id;


}
