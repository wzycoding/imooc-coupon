package com.imooc.coupon.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * 描述：jackson的自定义配置
 *
 * @author wzy
 * @date 2020/6/23 10:29
 * @version V1.0
 **/
@Configuration
public class JacksonConfig {
    /**
     * 声明jackson的 ObjectMapper Bean  ObjectMapper是线程安全的
     * @return ObjectMapper 对象
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //设置输出日期格式格式化方式
        mapper.setDateFormat(new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }


}
