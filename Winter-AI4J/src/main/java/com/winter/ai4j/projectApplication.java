package com.winter.ai4j;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "com.winter.ai4j.*.mapper")
@EnableAsync
@ServletComponentScan
@EnableScheduling
public class projectApplication {

    public static void main(String[] args) {
        SpringApplication.run(projectApplication.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        // 单体项目前端路由配置，当访问的路径不存在时，跳转到index.html
        // 如果不配置，前端除了根路径，其他路径都会报404。配置后，才能访问到其他路径。
        return factory -> factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
    }

}
