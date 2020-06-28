package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：获取优惠券请求对象定义
 *
 * @Author wzy
 * @Date 2020/6/28 22:21
 * @Version V1.0
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
