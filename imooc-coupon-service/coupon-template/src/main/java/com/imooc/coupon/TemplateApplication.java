package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h1>优惠券模板微服务启动入口</h1>
 * * @EnableJpaAuditing JPA审核注解
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/23 18:36
 **/
@EnableJpaAuditing
@EnableScheduling
@EnableEurekaClient
@SpringBootApplication
public class TemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }
}
