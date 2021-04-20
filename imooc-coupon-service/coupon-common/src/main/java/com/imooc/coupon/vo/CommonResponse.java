package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述：通用响应对象，不过有的时候我们可能不想使用统一响应的数据结构，
 *      想传递一些自定义的数据结构，这里需要定义一个注解来忽略统一响应
 *
 * @author wzy
 * @date 2020/6/23 10:50
 * @version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    /**
     * 自定义构造方法
     * @param code 错误码
     * @param message 错误信息
     */
    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
