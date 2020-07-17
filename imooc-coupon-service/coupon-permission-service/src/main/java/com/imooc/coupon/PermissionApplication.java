package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 描述：权限微服务启动程序
 *
 * @Author wzy
 * @Date 2020/7/17 13:46
 * @Version V1.0
 **/
@EnableEurekaClient
@SpringBootApplication
public class PermissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class, args);
    }
}
