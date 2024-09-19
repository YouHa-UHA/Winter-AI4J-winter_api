package com.winter.ai4j.aiChat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: Question
 * <blockquote><pre>
 * Description: [Question]
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
public class Question implements Serializable {

    private String question;


}
