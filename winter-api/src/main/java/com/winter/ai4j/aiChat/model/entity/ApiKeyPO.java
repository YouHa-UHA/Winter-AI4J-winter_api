package com.winter.ai4j.aiChat.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winter.ai4j.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: ApiKeyPO
 * <blockquote><pre>
 * Description: [ApiKeyPO-mysql]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/6/27 上午9:11
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("api_key")
public class ApiKeyPO extends BaseEntity {

    /*
     * @ApiModelProperty是 Swagger 框架提供的一个注解,用于描述 API 模型属性的元信息。
     * 其中 value 和 required 属性的作用如下:
     * value 属性:
     * 该属性用于指定属性的描述信息。
     * 在 Swagger 生成的 API 文档中,这个属性的值将会显示在对应属性的描述中。
     * 例如 @ApiModelProperty(value = "app索引标识")，表示该属性的描述是"app索引标识"。
     * required 属性:
     * 该属性用于指定该属性是否为必填项。
     * 如果设置为 true，则在 Swagger 生成的 API 文档中,该属性将被标记为必填。
     * 例如 @ApiModelProperty(value = "app索引标识", required = true)，表示"app索引标识"属性是必填的。
     * */
    @ApiModelProperty(value = "模型索引", required = true)
    @TableField("model_index")
    private String modelIndex;

    @ApiModelProperty(value = "模型公司", required = false)
    @TableField("company")
    private String company;

    @ApiModelProperty(value = "模型密钥", required = true)
    @TableField("model_key")
    private String modelKey;

    @ApiModelProperty(value = "功能名称", required = false)
    @TableField("app_name")
    private String appName;

    // 使用应用名+索引标识这组url和appKey是哪个模型用来创建、对话的
    @ApiModelProperty(value = "模型功能索引", required = true)
    @TableField("app_index")
    private String appIndex;

    @ApiModelProperty(value = "请求地址", required = true)
    @TableField("url")
    private String url;

    // 仅用于需要设定agent的场景
    @ApiModelProperty(value = "智能体ID", required = false)
    @TableField("agent_id")
    private String agentId;

}
