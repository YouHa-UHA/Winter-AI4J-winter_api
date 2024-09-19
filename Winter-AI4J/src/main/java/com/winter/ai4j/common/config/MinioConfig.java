package com.winter.ai4j.common.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Configuration
@Validated
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    @NotEmpty
    String url;

    @NotEmpty
    String accessKey;

    @NotEmpty
    String secretKey;

    @NotEmpty
    String bucketName;

    // @Bean
    // public MinioClient minioClient(){
    //
    //     //创建minio客户端
    //     MinioClient minioClient = MinioClient.builder()
    //             .endpoint(url)
    //             .credentials(accessKey,secretKey)
    //             .build();
    //
    //     return minioClient;
    //
    // }

}
