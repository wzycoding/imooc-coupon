package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 描述：结算微服务调用的熔断降级实现
 *
 * @Author wzy
 * @Date 2020/6/29 21:32
 * @Version V1.0
 **/
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    /**
     * 优惠券规则计算
     * @param settlementInfo 入参
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo)
            throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule" +
                "request error");
        // 不能去做核销
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);

        return new CommonResponse<>(-1,
                "[eureka-client-coupon-settlement] computeRule request error",
                settlementInfo);
    }
}
