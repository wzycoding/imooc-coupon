package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>分发目标枚举</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/24 9:25
 **/
@Getter
@AllArgsConstructor
public enum DistributeTarget {
    /**
     * 分发目标枚举定义
     */
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
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
