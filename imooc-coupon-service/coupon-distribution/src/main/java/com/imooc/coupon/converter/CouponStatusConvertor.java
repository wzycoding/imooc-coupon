package com.imooc.coupon.converter;

import com.imooc.coupon.constants.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 描述：优惠券状态枚举属性转换器
 *
 * @Author wzy
 * @Date 2020/6/28 15:28
 * @Version V1.0
 **/
@Converter
public class CouponStatusConvertor implements AttributeConverter<CouponStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
