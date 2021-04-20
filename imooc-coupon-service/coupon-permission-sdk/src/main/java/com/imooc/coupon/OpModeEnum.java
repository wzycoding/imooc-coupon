package com.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：操作模式的枚举定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/17 13:28
 **/
@Getter
@AllArgsConstructor
public enum OpModeEnum {
    READ("读"),
    WRITE("写");

    String mode;
}
