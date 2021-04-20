package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>有效期类型枚举<h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/24 15:08
 **/
@Getter
@AllArgsConstructor
public enum PeriodType {
    /**
     * 有效期类型枚举
     */
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
     *
     * @param code 有效期编码
     * @return 枚举信息
     */
    public static PeriodType of(Integer code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exits"));
    }
}
