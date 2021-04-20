package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 描述：优惠券结算微服务启动入口
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/1 1:00
 **/
@EnableEurekaClient
@SpringBootApplication
public class SettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SettlementApplication.class, args);
    }
}
