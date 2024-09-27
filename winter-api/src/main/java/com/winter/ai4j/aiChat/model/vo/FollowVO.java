package com.winter.ai4j.aiChat.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: FollowVO
 * <blockquote><pre>
 * Description: [提问后的联想回答]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/27 10:47
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowVO {

    @ApiModelProperty(value = "回答")
    @JSONField(name = "answer")
    private String answer;

    @ApiModelProperty(value = "跟随")
    @JSONField(name = "follow")
    private List<String> follow;

}
