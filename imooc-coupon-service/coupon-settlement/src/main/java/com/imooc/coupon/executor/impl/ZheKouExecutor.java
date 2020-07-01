package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 描述：折扣优惠券结算规则执行器
 *
 * @Author wzy
 * @Date 2020/7/1 14:37
 * @Version V1.0
 **/
@Slf4j
@Service
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = goodsCostSum(settlement.getGoodsInfos());

        // 都是针对单优惠券的，优惠券与购买商品是否匹配
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        // 判断是否匹配
        if (probability != null) {
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }

        // 折扣优惠券可以直接使用没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        // 折扣额度，需要除以100
        double quota = templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的值, 如果比最小值大则返回计算值，如果小于最小值
        // 则返回最小值
        settlement.setCost(
                retain2Decimals((goodsSum * (quota * 1.0 / 100))) > minCost() ?
                        retain2Decimals((goodsSum * (quota * 1.0 / 100))) : minCost()
        );
        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }
}
