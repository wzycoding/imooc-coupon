package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * 描述：Pre类型抽象过滤器类
 *
 * @Author wzy
 * @Date 2020/6/22 16:50
 * @Version V1.0
 **/
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter{

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

}
