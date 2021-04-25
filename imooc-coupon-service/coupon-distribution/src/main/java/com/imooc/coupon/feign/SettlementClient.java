package com.imooc.coupon.feign;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.hystrix.SettlementClientHystrix;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 描述：优惠券结算微服务 Feign接口定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/29 17:16
 **/
@FeignClient(value = "eureka-client-coupon-settlement",
        fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * 优惠券规则计算(计算出最终的价格)
     *
     * @param settlementInfo 入参
     * @return 结算信息
     * @throws CouponException 优惠券异常
     */
    @GetMapping(value = "/coupon-settlement/settlement/compute")
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlementInfo)
            throws CouponException;
}
