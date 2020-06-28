package com.imooc.coupon.dao;

import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 描述：优惠券DAO接口定义
 *
 * @Author wzy
 * @Date 2020/6/28 15:44
 * @Version V1.0
 **/
public interface CouponDao extends JpaRepository<Coupon, Integer> {
    /**
     * 根据userId + 状态查找优惠券记录
     * where user_id = ? and status = ?
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
