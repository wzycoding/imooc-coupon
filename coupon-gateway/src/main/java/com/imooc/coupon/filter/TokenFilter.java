package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * 描述：校验请求中传递Token过滤器,如果没有登录直接过滤掉
 *
 * @Author wzy
 * @Date 2020/6/22 23:22
 * @Version V1.0
 **/
@Component
@Slf4j
public class TokenFilter extends AbstractPreZuulFilter{


    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));
        Object token = request.getParameter("token");
        if (null == token) {
            log.error("error: token is empty");
            // 401 表示没有权限访问
            return fail(401, "error: token is empty");
        }
        // 继续向下走
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
