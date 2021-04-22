package com.imooc.coupon.dao;

import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>优惠券DAO接口定义</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 15:44
 **/
public interface CouponDao extends JpaRepository<Coupon, Integer> {
    /**
     * 根据userId + 状态查找优惠券记录
     * where user_id = ? and status = ?
     *
     * @param userId 用户id
     * @param status 优惠券状态
     * @return 用户优惠券列表
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
