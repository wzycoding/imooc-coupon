package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author wzy
 * @date 2020-08-20 23:23
 **/
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 管理员角色
     */
    ADMIN("管理员"),
    /**
     * 超级管理员角色
     */
    SUPER_ADMIN("超级管理员"),
    /**
     * 普通用户
     */
    CUSTOMER("普通用户");

    /**
     * 角色名称
     */
    private String roleName;
}
