package com.imooc.coupon.controller;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 描述：Ribbon 应用 controller
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/30 15:21
 **/
@Slf4j
@RestController
public class RibbonController {
    /**
     * rest 客户端
     */
    private final RestTemplate restTemplate;

    @Autowired
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 通过Ribbon组件调用模板微服务
     * coupon-distribution/info
     */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo() {
        // 自动解析应用名称
        String infoUrl = "http://eureka-client-coupon-template/coupon-template/info";
        // 反序列化为对象，因为返回的为CommonResponse
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /**
     * 模板微服务的元信息,定义vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TemplateInfo {

        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }


}
