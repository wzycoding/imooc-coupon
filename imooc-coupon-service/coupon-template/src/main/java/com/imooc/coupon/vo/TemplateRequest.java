package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 描述：优惠券模板创建请求对象
 *
 * @Author wzy
 * @Date 2020/6/26 10:17
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    /**
     * 优惠券的名称
     */
    private String name;

    /**
     * 优惠券的 logo
     */
    private String logo;

    /**
     * 优惠券的描述
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
     * 总数
     */
    private Integer count;

    /**
     * 创建用户
     */
    private Long userId;

    /**
     * 目标用户
     */
    private Integer target;


    /**
     * 优惠券模板规则
     */
    private TemplateRule rule;

    /**
     * 属性校验方法
     */
    public boolean validate() {
        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count > 0 && userId > 0;
        return stringValid && enumValid && numValid && rule.validate();
    }

}
