package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

/**
 * 描述：优惠券模板异步服务接口定义
 *
 * @Author wzy
 * @Date 2020/6/26 12:04
 * @Version V1.0
 **/
public interface IAsyncService {
    /**
     * <h2>根据模板异步创建优惠券码</h2>
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
