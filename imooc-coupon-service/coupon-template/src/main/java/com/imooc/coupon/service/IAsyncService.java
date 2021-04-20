package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

/**
 * 描述：优惠券模板异步服务接口定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 12:04
 **/
public interface IAsyncService {
    /**
     * <h2>根据模板异步创建优惠券码</h2>
     *
     * @param template {@link CouponTemplate} 优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
