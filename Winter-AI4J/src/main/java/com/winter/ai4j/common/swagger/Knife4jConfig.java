package com.winter.ai4j.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(getParameters())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.winter.ai4j"))
                .paths(PathSelectors.any())
                .build();
    }

    /*
    * API信息
    * */
    private static final String API_TILE="Fox.AI";

    /*
    * API信息
    * */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_TILE)
                .description("Winter-AI4J接口文档")
                .version("1.0")
                .build();
    }

    /*
    * 请求头参数
    * */
    private List<Parameter> getParameters() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(); // 定义全局header参数
        parameterBuilder.name("Authorization") // header中的token参数
                .description("Access Token") // 描述
                .modelRef(new ModelRef("string")) // 数据类型
                .parameterType("header") // 参数类型
                .required(false) // 是否必须
                .build(); // 参数构建

        return Collections.singletonList(parameterBuilder.build());
    }
}

