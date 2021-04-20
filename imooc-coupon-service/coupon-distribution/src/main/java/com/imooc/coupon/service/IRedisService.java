package com.imooc.coupon.service;

import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * 描述：Redis相关的操作服务接口定义
 * 1、用户三个状态优惠Cache相关操作
 * 2、优惠券模板生成的优惠券码的Cache操作
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 15:51
 **/
public interface IRedisService {

    /**
     * 根据userId和状态找到缓存的优惠券列表数据
     *
     * @param userId 用户id
     * @param status 优惠券状态 {@link CouponStatus}
     * @return 优惠券列表数据 {@link Coupon}s, 注意这个接口可能返回null，代表从没有过记录
     */
    List<Coupon> getCacheCoupons(long userId, Integer status);

    /**
     * 保存空的优惠券列表到缓存中（避免缓存穿透）
     *
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 从缓存中获取优惠券码，可能也会为空，因为可能优惠券已经分发完了
     *
     * @param templateId 优惠券模板主键
     * @return 优惠券码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);


    /**
     * 将用户的优惠券信息缓存到redis中
     *
     * @param userId  用户id
     * @param coupons {@link Coupon}s 优惠券列表
     * @param status  状态
     * @return 保存成功的个数
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons,
                             Integer status) throws CouponException;
}
