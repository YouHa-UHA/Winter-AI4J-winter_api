package com.winter.ai4j.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
@Data
@Slf4j
@Validated
@ConfigurationProperties(prefix = "winter")
public class ExecutorConfig {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private int keepAliveSeconds;

    private static String namePrefix = "winter-";

    @Bean(name = "ServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setThreadNamePrefix(namePrefix);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor exe) -> {
            throw new RuntimeException("服务器繁忙，请稍后重试！");
        });
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }


}
