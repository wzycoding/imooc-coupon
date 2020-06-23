package com.imooc.coupon.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 描述：定制http消息转换器
 *
 * @Author wzy
 * @Date 2020/6/23 10:25
 * @Version V1.0
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {
        // 清空消息转换器
        converters.clear();
        // SpringBoot底层用的HttpMessageConverter，java对象向->HTTP和 HTTP->java对象
        // 不需要Springboot帮我们挑选消息转换器
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
