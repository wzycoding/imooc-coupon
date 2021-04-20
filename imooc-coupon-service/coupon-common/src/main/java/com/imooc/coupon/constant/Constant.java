package com.imooc.coupon.constant;

/**
 * 描述：通用常量定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/27 14:48
 **/
public class Constant {
    /**
     * kafka消息topic
     **/
    public static final String TOPIC = "imooc_user_coupon_op";

    /**
     * Redis key前缀定义
     */
    public static class RedisPrefix {
        /**
         * 优惠券码 key 前缀
         **/
        public static final String COUPON_TEMPLATE =
                "imooc_coupon_template_code_";

        /**
         * 用户当前所有可用的优惠券 key 前缀
         **/
        public static final String USER_COUPON_USABLE =
                "imooc_user_coupon_usable_";

        /**
         * 用户当前所有已使用的优惠券 key 前缀
         **/
        public static final String USER_COUPON_USED =
                "imooc_user_coupon_used_";

        /**
         * 用户当前所有已过期的优惠券 key 前缀
         **/
        public static final String USER_COUPON_EXPIRED =
                "imooc_user_coupon_used_";
    }

}
