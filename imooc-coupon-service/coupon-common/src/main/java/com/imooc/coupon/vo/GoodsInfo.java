package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：fake商品信息（微服务之间传递）
 *
 * @Author wzy
 * @Date 2020/6/28 23:26
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {
    /**
     * 商品类型 {@link com.imooc.coupon.constant.GoodsType}
     */
    private Integer type;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品数量
     */
    private Integer count;

    // TODO 名称，使用信息
}
