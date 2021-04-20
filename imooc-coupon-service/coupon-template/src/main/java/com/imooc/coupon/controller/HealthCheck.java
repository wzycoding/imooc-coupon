package com.imooc.coupon.controller;

import com.imooc.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：健康检查接口
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/27 22:34
 **/
@Slf4j
@RestController
public class HealthCheck {

    /**
     * 服务发现客户端 :获取其他服务的元信息
     **/
    private final DiscoveryClient discoveryClient;

    /**
     * 服务注册接口，提供了获取服务id的方法：利用这个接口进行服务注册，
     * 通过id再到discoveryClient获取元信息
     */
    private final Registration registration;

    @Autowired
    public HealthCheck(DiscoveryClient discoveryClient, Registration registration) {
        this.discoveryClient = discoveryClient;
        this.registration = registration;
    }

    /**
     * 健康检查接口
     * http://localhost:7001/coupon-template/health
     * 网关：http://localhost:9000/imooc/coupon-template/health
     */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouponTemplate Is OK!";
    }

    /**
     * 异常测试接口
     * http://localhost:7001/coupon-template/exception
     */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * 获取EurekaServer微服务元信息
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        // 大约需要等待两分钟的时间才能获取到注册信息
        // registration.getServiceId()当前服务的id
        List<ServiceInstance> instances =
                discoveryClient.getInstances(registration.getServiceId());
        List<Map<String, Object>> result =
                new ArrayList<>(instances.size());
        instances.forEach(i -> {
            Map<String, Object> info = new HashMap<>();
            // 服务id
            info.put("serviceId", i.getServiceId());
            // 实例id，实例可以有多个，服务只能有一个
            info.put("instanceId", i.getInstanceId());
            info.put("port", i.getPort());

            result.add(info);
        });
        return result;
    }
}
