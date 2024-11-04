package com.winter.ai4j.aiChat.model.coze;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: CozeRes
 * <blockquote><pre>
 * Description: [Coze返回的泛型数据]
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
@ApiModel(value = "Coze返回的泛型数据", description = "Coze返回的泛型数据")
public class CozeRes<T> {


    @JSONField(name = "code")
    @ApiModelProperty(value = "状态码", required = true)
    String code;

    @JSONField(name = "data")
    @ApiModelProperty(value = "数据-泛型", required = true)
    T data;

}
