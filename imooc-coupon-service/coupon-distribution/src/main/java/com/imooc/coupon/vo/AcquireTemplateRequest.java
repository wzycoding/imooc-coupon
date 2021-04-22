package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>获取优惠券请求对象定义<h1/>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 22:21
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优惠券模板信息
     */
    private CouponTemplateSDK templateSDK;
}
