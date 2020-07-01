package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：规则类型枚举定义
 *
 * @Author wzy
 * @Date 2020/7/1 8:53
 * @Version V1.0
 **/
@Getter
@AllArgsConstructor
public enum RuleFlag {
    // 单类别优惠券进行结算
    MAJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券的计算规则"),

    // 多类别优惠券定义
    MANIAN_ZHEKOU("满减券 + 折扣券的计算规则");

    // TODO 更多优惠券类别组合

    /**
     * 对规则的描述
     */
    private String description;

}
