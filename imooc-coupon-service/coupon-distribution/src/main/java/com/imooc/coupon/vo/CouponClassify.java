package com.imooc.coupon.vo;

import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：用户优惠券分类，根据优惠券状态
 *
 * @Author wzy
 * @Date 2020/6/29 22:30
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /**
     * 可使用的
     */
    private List<Coupon> usable;
    /**
     * 已使用的
     */
    private List<Coupon> used;

    /** 过期的 **/
    private List<Coupon> expired;

    /**
     * 对当前的优惠券进行分类
     * @param coupons 优惠券
     */
    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            // 只有用户查看优惠券的时候才会对优惠券进行判断是否过期，延迟删除
            boolean isTimeExpire;
            long curTime = new Date().getTime();

            // 是否为固定时间过期,判断优惠券是否过期
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )) {
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration()
                        .getDeadLine() <= curTime;
            } else {
                isTimeExpire = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                        ).getTime() <= curTime;
            }

            // 状态如果已使用直接加入集合
            if (c.getStatus() == CouponStatus.USABLE) {
                used.add(c);
                //如果过期则要考虑本身状态，和当前判断是否过期，存到过期优惠券列表中
            } else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {
                expired.add(c);
            } else {
                // 最后一种情况剩下的直接保存到可使用的优惠券列
                usable.add(c);
            }

        });
        return new CouponClassify(usable, used, expired);
    }
}
