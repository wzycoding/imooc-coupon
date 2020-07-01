package com.imooc.coupon.executor;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.vo.SettlementInfo;

/**
 * 描述：优惠券模板规则处理器接口定义
 *
 * @Author wzy
 * @Date 2020/7/1 9:00
 * @Version V1.0
 **/
public interface RuleExecutor {
    /**
     * 规则类型的标记,有点类似于ZuulFilter
     * @return {@link RuleFlag}
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则的计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息，如果用户传递参数有误，要做一些修正的工作
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
