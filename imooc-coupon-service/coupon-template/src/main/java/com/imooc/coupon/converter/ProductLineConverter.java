package com.imooc.coupon.converter;

import com.imooc.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 描述：产品线枚举属性转换器
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 9:16
 **/
@Converter
public class ProductLineConverter implements AttributeConverter<ProductLine, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
