package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：优惠券kafka消息对象定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/29 14:16
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {
    /**
     * 优惠券状态
     */
    private Integer status;

    /**
     * Coupon主键
     */
    private List<Integer> ids;
}
