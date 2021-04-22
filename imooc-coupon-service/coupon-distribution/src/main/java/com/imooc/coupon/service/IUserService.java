package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * <h1>用户服务相关的接口定义</h1>
 * <p>
 * 1、用户三类状态优惠券信息展示服务
 * 2、查看用户当前可以领取的优惠券模板（是否过期、从来没有领过，可以领多张等情况）
 * 配合coupon-template 微服务
 * 3、用户领取优惠券服务
 * 4、用户消费优惠券服务 - coupon-settlement（计算结果） 微服务配合实现
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 17:48
 **/
public interface IUserService {

    /**
     * <h2>根据用户id和状态查询优惠券记录</h2>
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return {@link Coupon} 用户优惠券列表
     * @throws CouponException 优惠券异常
     */
    List<Coupon> findCouponByStatus(Long userId, Integer status)
            throws CouponException;

    /**
     * <h2>根据用户id查找当前用户可以领取的优惠券模板</h2>
     *
     * @param userId 用户id
     * @return {@link CouponTemplateSDK}s
     * @throws CouponException 优惠券异常
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId)
            throws CouponException;


    /**
     * 用户领取优惠券
     *
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException 优惠券异常
     */
    Coupon acquireTemplate(AcquireTemplateRequest request)
            throws CouponException;

    /**
     * 结算（核销）优惠券, 计算最终的金额cost，填充进去返回给分发微服务
     *
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException 优惠券异常
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}
