package com.winter.ai4j.aiChat.model.coze;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: CozeQueReq
 * <blockquote><pre>
 * Description: [Coze请求提问请求体]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/27 上午9:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CozeQueReq {


    String bot_id;

    String user_id;

    List<CozeQueReqMessage> additional_messages;

    boolean stream;

    boolean auto_save_history;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CozeQueReqMessage {

        String role;

        String type;

        String content;

        String content_type;

    }


}
