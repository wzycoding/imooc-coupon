package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 描述：优惠券分类枚举属性转换器
 * AttributeConverter
 * X:是实体属性的类型
 * Y:是数据库字段的类型
 *
 * @Author wzy
 * @Date 2020/6/26 9:04
 * @Version V1.0
 **/
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory, String> {

    /**
     * 将实体属性转换为数据库的列: 插入和更新时的动作
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库的列转换为实体类的属性：查询操作执行的动作
     */
    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}
