package com.imooc.coupon.exception;

import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：全局异常处理
 *
 * @Author wzy
 * @Date 2020/6/23 12:24
 * @Version V1.0
 **/
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 对CouponException 进行统一处理
     * @param request request
     * @param ex 异常对象
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
