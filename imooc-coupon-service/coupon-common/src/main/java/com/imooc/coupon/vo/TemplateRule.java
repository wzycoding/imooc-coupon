package com.imooc.coupon.vo;

import com.imooc.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 描述：优惠券规则对象定义
 *
 * @Author wzy
 * @Date 2020/6/24 15:14
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule {

    /**
     * 优惠券过期规则
     */
    private Expiration expiration;

    /**
     * 折扣规则
     */
    private Discount discount;

    /**
     * 限制规则：每个人最多可以领几张的限制
     */
    private Integer limitation;

    /**
     * 使用范围限制：地域 + 商品类型
     */
    private Usage usage;

    /**
     * 权重（可以和哪些优惠券叠加使用，需要同一类一定不能叠加）List[],优惠券的唯一编码
     */
    private String weight;

    /**
     * 对属性进行校验
     */
    public boolean validate() {
        return expiration.validate() && discount.validate()
                && limitation > 0 && usage.validate()
                && StringUtils.isNotBlank(weight);
    }

    /**
     * 有效期限的规则
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expiration {

        /** 有效期规则, 对应PeriodType的code字段 （针对优惠券） **/
        private Integer period;

        /** 只对变动类型的有效期有效: 有效间隔 （针对优惠券）
         * 这里要看优惠券领取的时间**/
        private Integer gap;

        /** 优惠券[模板]的失效日期（时间戳）注意这里是优惠券模板，不是优惠券本身
         * 如果是固定时间过期，那么对优惠券也有效 **/
        private Long deadLine;

        boolean validate() {
            // 最简化校验
            return null != PeriodType.of(period) && gap > 0 && deadLine > 0;
        }
    }

    /**
     * 折扣规则，需要与类型配合决定
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {

        /**
         * 额度： 满减（20），折扣（85 代表0.85），立减（10）
         * 类型会在数据库字段中有标识
         */
        private Integer quota;

        /**
         * 基准：需要满多少才可用
         */
        private Integer base;

        /**
         * 校验
         * @return
         */
        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    /**
     * 使用范围
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 省份
         */
        private String province;
        /**
         * 城市
         */
        private String city;

        /**
         * 商品类型 List[文娱，生鲜，家居，全品类]
         */
        private String goodsType;

        /**
         * 简单校验
         */
        boolean validate() {
            return StringUtils.isNotBlank(province)
                    && StringUtils.isNotBlank(city)
                    && StringUtils.isNotBlank(goodsType);
        }

    }

}
