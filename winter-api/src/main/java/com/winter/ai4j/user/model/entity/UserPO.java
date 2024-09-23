package com.winter.ai4j.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winter.ai4j.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: UserPO
 * <blockquote><pre>
 * Description: [描述]
 * </pre></blockquote>
 *
 * @author WYH4J
 * Date: 2024/9/23 14:27
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class UserPO extends BaseEntity {

    @ApiModelProperty(value = "手机号", required = true)
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "邮箱", required = false)
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "密码", required = true)
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "昵称", required = false)
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "用户性别", required = false)
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "用户年龄", required = false)
    @TableField("age")
    private Integer age;

    @ApiModelProperty(value = "用户头像", required = false)
    @TableField("avatar_url")
    private String avatarUrl;

    @ApiModelProperty(value = "备注", required = false)
    @TableField("remarks")
    private String remarks;

}
