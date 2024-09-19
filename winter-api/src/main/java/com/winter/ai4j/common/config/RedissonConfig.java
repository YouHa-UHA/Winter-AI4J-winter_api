package com.winter.ai4j.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * redisson配置信息
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private String host;

    private String addresses;

    private String password;

    private String port;

    private int timeout = 20000;
    private int connectionPoolSize = 64;
    private int connectionMinimumIdleSize=10;
    private int pingConnectionInterval = 60000;
    private static String PROTOCAL = "redis://";

    /*
    * redissonSingle
    * */
    @Bean
    RedissonClient redissonSingle() {
        Config config = new Config();
        config.setNettyThreads(1000);
        if(StringUtils.isEmpty(host)){
            throw new RuntimeException("host is empty");
        }
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(PROTOCAL + this.host + ":" + port)
                .setTimeout(this.timeout)
                .setPingConnectionInterval(pingConnectionInterval)
                .setConnectionPoolSize(this.connectionPoolSize)
                .setConnectionMinimumIdleSize(this.connectionMinimumIdleSize);
        if(!StringUtils.isEmpty(this.password)) {
            serverConfig.setPassword(this.password);
        }
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);

    }
}
