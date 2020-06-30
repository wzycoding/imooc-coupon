package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 描述：用户服务相关的接口定义
 * 1、用户三类状态优惠券信息展示服务
 * 2、查看用户当前可以领取的优惠券模板（是否过期、从来没有领过，可以领多张等情况）
 *    配合coupon-template 微服务
 * 3、用户领取优惠券服务
 * 4、用户消费优惠券服务 - coupon-settlement（计算结果） 微服务配合实现
 *
 * @Author wzy
 * @Date 2020/6/28 17:48
 * @Version V1.0
 **/
public interface IUserService {

    /**
     * 根据用户id和状态查询优惠券记录
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    List<Coupon> findCouponByStatus(Long userId, Integer status)
            throws CouponException;

    /**
     * 根据用户id查找当前用户可以领取的优惠券模板
     * @param userId 用户id
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId);


    /**
     * 用户领取优惠券
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    Coupon AcquireTemplate(AcquireTemplateRequest request)
            throws CouponException;

    /**
     * 结算（核销）优惠券, 计算最终的金额cost，填充进去返回给分发微服务
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}
