package com.winter.ai4j.aiChat.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: QuestionDTO
 * <blockquote><pre>
 * Description: [QuestionDTO]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/1 上午11:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDTO implements Serializable {

    @ApiModelProperty(value = "会话ID", required = true)
    @TableField("chat_id")
    private String chatId;

    @ApiModelProperty(value = "模型功能索引", required = true)
    @TableField("app_index")
    private String appIndex;

    @ApiModelProperty(value = "问题", required = true)
    @TableField("question")
    private String question;


}
