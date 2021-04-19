package com.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 描述：通用的抽象过滤器类
 *
 * @author wzy
 * @date 2020/6/22 16:28
 * @version V1.0
 **/
public abstract class AbstractZuulFilter extends ZuulFilter {

    /** 用于在过滤器间传递消息，每个请求的数据保存在每个请求的ThreadLocal中
     *  扩展了ConcurrentHashMap **/
    RequestContext context;

    /** 标识请求不应该往下走了 **/
    private final static String NEXT = "next";

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        // 默认值为true因为SpringCloud内置过滤器并不包含这样一个key（NEXT）
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException {
        context = RequestContext.getCurrentContext();
        return cRun();
    }

    /**
     * 自定义钩子方法
     * @return
     */
    protected abstract Object cRun();

    /**
     * 执行失败方法
     * @param code 状态码
     * @param msg 响应内容
     * @return
     */
    Object fail(int code, String msg) {
        // 不需要往下走了
        context.set(NEXT, false);
        // 响应直接返回
        context.setSendZuulResponse(false);
        // 设置响应类型
        context.getResponse().setContentType("text/html;charset=UTF-8");
        // 设置状态码
        context.setResponseStatusCode(code);
        // 设置返回的信息
        context.setResponseBody(String.format("{\"result\": \"%s!\"}", msg));
        return null;
    }

    /**
     * 执行成功方法
     */
    Object success() {
        //set这个是为了让shouldFilter()方法可以直接取到值，来判断是否终止过滤器链
        //数据是存在TheadLocal当中，是线程安全的
        context.set(NEXT, true);
        return null;
    }
}
