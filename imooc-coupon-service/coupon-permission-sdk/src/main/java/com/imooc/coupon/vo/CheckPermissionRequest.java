package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：权限校验请求对象定义
 *
 * @Author wzy
 * @Date 2020/7/17 13:33
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * uri
     */
    private String uri;
    /**
     * HTTP请求方法
     */
    private String httpMethod;
}
