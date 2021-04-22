package com.imooc.coupon.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券状态</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 15:03
 **/
@Getter
@AllArgsConstructor
public enum CouponStatus {
    /**
     * 优惠券状态枚举
     */
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);

    /**
     * 优惠券状态描述信息
     */
    private String description;
    /**
     * 优惠券状态编码
     */
    private Integer code;

    /**
     * 根据code获取CouponStatus
     */
    public static CouponStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }
}
