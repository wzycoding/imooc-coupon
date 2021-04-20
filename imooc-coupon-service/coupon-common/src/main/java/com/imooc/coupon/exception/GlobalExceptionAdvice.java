package com.imooc.coupon.exception;

import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1> 描述：全局异常处理</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/23 12:24
 **/
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * <h2>对CouponException 进行统一处理</h2>
     *
     * @param request request
     * @param ex      异常对象
     * @return 异常响应信息
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest request, CouponException ex
    ) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");

        response.setData(ex.getMessage());
        return response;
    }
}
