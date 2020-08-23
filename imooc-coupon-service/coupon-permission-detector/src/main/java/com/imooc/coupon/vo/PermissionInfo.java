package com.imooc.coupon.vo;

import lombok.Data;

/**
 * 接口的权限信息组装类定义
 *
 * @author wzy
 * @date 2020-08-22 0:25
 **/
@Data
public class PermissionInfo {
    /**
     * Controller的URL
     */
    private String url;

    /**
     * 方法类型
     */
    private String method;

    /**
     * 是否是只读的
     */
    private Boolean isRead;

    /**
     * 方法的描述信息
     */
    private String description;

    /**
     * 扩展属性
     */
    private String extra;

    @Override
    public String toString() {
        return "PermissionInfo{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", isRead=" + isRead +
                ", description='" + description + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
