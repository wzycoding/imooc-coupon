package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限描述注解
 *
 * @author wzy
 * @date 2020-08-22 0:13
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImoocCouponPermission {
    /**
     * 接口描述信息
     */
    String description() default "";

    /**
     * 此接口是否位只读
     */
    boolean readOnly() default true;

    /**
     * 扩展属性
     * 最好以json格式去存储
     */
    String extra() default "";
}
