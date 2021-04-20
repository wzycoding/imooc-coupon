package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：打印执行日志的Filter，发起请求->网关->pre filter->其他filter->指定服务返回结果->postFilter->返回给请求方
 *
 * @author wzy
 * @date 2020/6/23 8:31
 * @version V1.0
 **/
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();

        // 从PreRequestFilter 中设置的请求时间戳
        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();

        long duration = System.currentTimeMillis() - startTime;

        // 从网关通过的请求都会打印日志记录
        log.info("uri: {}, duration: {}", uri, duration);
        return null;
    }

    @Override
    public int filterOrder() {
        // 默认response Order 1000,需要减一才能执行到这个过滤器
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
