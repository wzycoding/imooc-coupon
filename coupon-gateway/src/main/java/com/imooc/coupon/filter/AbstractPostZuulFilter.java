package com.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * 描述：Post类型的抽象过滤器类
 *
 * @Author wzy
 * @Date 2020/6/22 16:52
 * @Version V1.0
 **/
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
