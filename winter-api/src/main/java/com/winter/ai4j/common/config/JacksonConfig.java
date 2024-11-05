// package com.winter.ai4j.common.util;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// /**
//  * ClassName: JacksonConfig
//  * <blockquote><pre>
//  * Description: [Jackson配置，设定全局按照下划线命名取代驼峰命名]
//  * </pre></blockquote>
//  *
//  * @author WYH4J
//  * Date: 2024/9/20 17:40
//  * @version 1.0.0
//  * @since 1.0.0
//  */
// @Configuration
// public class JacksonConfig {
//
//     @Bean
//     public ObjectMapper objectMapper() {
//         ObjectMapper objectMapper = new ObjectMapper();
//         objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//         return objectMapper;
//     }
//
// }