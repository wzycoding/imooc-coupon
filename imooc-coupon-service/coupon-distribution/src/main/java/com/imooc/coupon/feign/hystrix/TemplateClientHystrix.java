package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 描述：优惠券模板feign熔断降级策略
 *
 * @Author wzy
 * @Date 2020/6/29 18:03
 * @Version V1.0
 **/
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    /**
     * 查找所有可用的优惠券模板
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate " +
                "request error");
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] findAllUsableTemplate ",
                Collections.emptyList()
        );
    }

    /**
     * 获取模板ids 到 CouponTemplateSDK的映射
     * @param ids 优惠券模板id
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>>
    findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK " +
                "request error");
        return new CommonResponse<>(-1, "[eureka-client-coupon-template] findIds2TemplateSDK " +
                "request error", new HashMap<>());
    }
}
