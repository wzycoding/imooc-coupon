package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：微服务直接的优惠券模板信息定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 12:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {
    /**
     * 优惠券模板主键
     */
    private Integer id;

    /**
     * 优惠券模板的名称
     */
    private String name;

    /**
     * 优惠券模板logo
     */
    private String logo;

    /**
     * 优惠券模板
     */
    private String desc;

    /**
     * 优惠券分类
     */
    private String category;

    /**
     * 产品线
     */
    private Integer productLine;

    /**
     * 优惠券模板的编码
     */
    private String key;

    /**
     * 目标用户
     */
    private Integer target;

    /**
     * 优惠券规则
     */
    private TemplateRule rule;

}
