package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 描述：分发目标枚举
 *
 * @Author wzy
 * @Date 2020/6/24 9:25
 * @Version V1.0
 **/
@Getter
@AllArgsConstructor
public enum DistributeTarget {

    SINGLE("单用户", 1),
    MULTI("多用户", 2);
    /**
     * 分发目标描述
     */
    private String description;

    /**
     * 分发目标编码
     */
    private Integer code;

    public static DistributeTarget of(Integer code) {
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
