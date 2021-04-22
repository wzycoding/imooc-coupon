package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>结算信息对象定义</h1>
 * 包含：
 * 1、userId
 * 2、商品信息（列表）
 * 3、优惠券列表
 * 4、结算结果金额
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 23:39
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品信息
     */
    private List<GoodsInfo> goodsInfos;

    /**
     * 优惠券列表
     */
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    /**
     * 是否使结算生效（用于区分是用于结算还是用于核销），true核销，false结算
     */
    private Boolean employ;

    /**
     * 结果结算金额，通过结算微服务返回结算结果
     */
    private Double cost;

    /**
     * 优惠券和模板信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo {
        /**
         * coupon的主键
         */
        private Integer id;

        /**
         * 优惠券对应的模板对象
         */
        private CouponTemplateSDK template;
    }
}
