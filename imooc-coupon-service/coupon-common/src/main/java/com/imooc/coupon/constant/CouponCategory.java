package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 描述：优惠券分类枚举
 *
 * @Author wzy
 * @Date 2020/6/24 9:07
 * @Version V1.0
 **/
@Getter
@AllArgsConstructor
public enum CouponCategory {

    MANJIAN("满减券", "001"),
    ZHEKOU("折扣券", "002"),
    LIJIAN("立减券", "003");
    /**
     * 优惠券描述信息
     */
    private String description;

    /**
     * 优惠券分类编码
     */
    private String code;

    /**
     * 获取枚举方法
     * @param code 分类编码
     * @return 优惠券对象
     */
    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                // 返回任意一个枚举，如果不存在则抛出异常
                .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    }

}
