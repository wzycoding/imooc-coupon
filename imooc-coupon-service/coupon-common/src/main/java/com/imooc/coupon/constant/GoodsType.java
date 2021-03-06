package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>商品类型枚举</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 23:10
 **/
@Getter
@AllArgsConstructor
public enum GoodsType {
    /**
     * 商品类型枚举
     */
    WENYU("文娱", 1),
    SHENGXIAN("生鲜", 2),
    JIAJU("家居", 3),
    OTHERS("其他", 4),
    ALL("全品类", 5);

    /**
     * 商品类型描述
     */
    private String description;
    /**
     * 商品类型编码
     */
    private Integer code;

    /**
     * 根据商品类型编码获取商品类型枚举
     */
    public static GoodsType of(Integer code) {
        Objects.requireNonNull(code);

        return Stream.of(values()).filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " is not exist"));
    }
}
