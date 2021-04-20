package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 描述：优惠券模板实体类自定义序列化器
 * 为什么要有序列化器呢？因为比如ProductLine这种枚举类，直接序列化为Json，
 * 并不会使用对应的描述而是使用整个枚举对象，因为我们序列化返回的值是给用户看的。所以我们需要对默认的序列化规则
 * 进行调整。
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 9:45
 **/
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {

    /**
     * 序列化方法
     *
     * @param template
     * @param generator          json生成器
     * @param serializerProvider
     * @throws IOException
     */
    @Override
    public void serialize(CouponTemplate template,
                          JsonGenerator generator,
                          SerializerProvider serializerProvider) throws IOException {
        // 开始序列化对象
        generator.writeStartObject();

        generator.writeStringField("id", template.getId().toString());
        generator.writeStringField("name", template.getName());
        generator.writeStringField("logo", template.getLogo());
        generator.writeStringField("desc", template.getDescription());
        generator.writeStringField("category",
                template.getCategory().getDescription());
        generator.writeStringField("productLine",
                template.getProductLine().getDescription());
        generator.writeStringField("count", template.getCount().toString());
        generator.writeStringField("createTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(template.getCreateTime()));
        generator.writeStringField("userId", template.getUserId().toString());
        generator.writeStringField("key", template.getKey() + String.format("%04d", template.getId()));
        generator.writeStringField("target", template.getTarget().getDescription());
        generator.writeStringField("rule",
                JSON.toJSONString(template.getRule()));

        // 结束序列化对象
        generator.writeEndObject();
    }
}
