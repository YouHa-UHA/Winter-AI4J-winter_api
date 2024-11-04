package com.winter.ai4j.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: IpLocation
 * <p>
 *
 * </p >
 *
 * @author wyh
 * Date: 2024/9/3 下午3:35
 */
@Data
public class IpLocation implements Serializable {

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("country")
    private String country;

    @ApiModelProperty("province")
    private String province;

    @ApiModelProperty("city")
    private String city;

    @ApiModelProperty("isp")
    private String isp;
}
