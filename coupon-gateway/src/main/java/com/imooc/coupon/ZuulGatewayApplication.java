package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 描述：网关应用启动入口
 * 1、@EnableZuulProxy 标识当前的应用是Zuul Server
 * 2、@SpringCloudApplication 这里要用这个注解，它是一个组合注解，内部包含了@SpringBootAppication
 *       组合了服务发现注解 @EnableDiscoveryClient
 *        组合了熔断注解 @EnableCircuitBreaker
 * @Author wzy
 * @Date 2020/6/22 16:08
 * @Version V1.0
 **/
@SpringCloudApplication
@EnableZuulProxy
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
