package com.imooc.coupon.feign;

import com.imooc.coupon.feign.hystrix.TemplateClientHystrix;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板微服务 Feign 接口定义</h1>
 * <p>
 * fallback:指定熔断降级实现类
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/29 17:00
 **/
@FeignClient(value = "eureka-client-coupon-template",
        fallback = TemplateClientHystrix.class)
public interface TemplateClient {
    /**
     * 查找所有可用的优惠券模板
     *
     * @return 可用的优惠券模板
     */
    @GetMapping(value = "/coupon-template/template/sdk/all")
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取模板ids 到CouponTemplateSDK的映射
     *
     * @return 获取优惠券模板id ->优惠券模板信息映射
     */
    @GetMapping(value = "/coupon-template/template/sdk/infos")
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam Collection<Integer> ids);
}
