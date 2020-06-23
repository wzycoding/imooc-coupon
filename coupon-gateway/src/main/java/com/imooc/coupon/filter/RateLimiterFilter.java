package com.imooc.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：限流过滤器, 使用guava，使用的是令牌桶算法
 *
 * @Author wzy
 * @Date 2020/6/22 23:33
 * @Version V1.0
 **/
@Slf4j
@Component
@SuppressWarnings("all")
public class RateLimiterFilter extends AbstractPreZuulFilter {
    // 每秒可以获取到两个令牌
    RateLimiter rateLimiter = RateLimiter.create(2.0);
    @Override
    protected Object cRun() {
        // 可以对某一ip或者某一URL进行扩展实现
        HttpServletRequest request = context.getRequest();
        // 尝试获取令牌
        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        } else {
            log.error("rate limit: {}", request.getRequestURI());
            // 限流
            return fail(402, "error: rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
