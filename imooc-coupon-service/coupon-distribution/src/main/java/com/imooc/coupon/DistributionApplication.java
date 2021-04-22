package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * <h1>分发微服务的启动入口</h1>
 * <p>
 * *@EnableEurekaClient eureka 客户端
 * *@EnableFeignClients feign调用
 * *@EnableCircuitBreaker 熔断降级
 * *@EnableJpaAuditing jap审计
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 13:17
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableJpaAuditing
public class DistributionApplication {

    /**
     * <h2>通过RestTemplate可以访问其他接口</h2>
     * <p>
     * *@LoadBalanced 实现对多实例负载均衡
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(DistributionApplication.class, args);
    }
}
