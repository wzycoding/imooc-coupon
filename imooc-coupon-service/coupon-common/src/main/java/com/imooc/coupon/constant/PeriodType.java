package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 描述：有效期类型枚举
 *
 * @Author wzy
 * @Date 2020/6/24 15:08
 * @Version V1.0
 **/
@Getter
@AllArgsConstructor
public enum PeriodType {

    REGULAR("固定的(固定日期)", 1),
    SHIFT("变动的(以领取之日开始计算)", 2);
    /**
     * 有效期描述
     */
    private String description;

    /**
     * 有效期编码
     */
    private Integer code;

    /**
     * 根据有效期编码获取枚举信息
     * @param code 有效期编码
     * @return 枚举信息
     */
    public static PeriodType of(Integer code) {
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exits"));
    }

}
