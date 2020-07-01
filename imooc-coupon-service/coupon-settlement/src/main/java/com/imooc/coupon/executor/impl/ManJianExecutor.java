package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 描述：满减优惠券结算规则执行器
 *
 * @Author wzy
 * @Date 2020/7/1 13:16
 * @Version V1.0
 **/
@Slf4j
@Service
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MAJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum
                (settlement.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (null != probability) {
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        // 获取满减基准值
        double base = (double)templateSDK.getRule().getDiscount().getBase();
        // 减多少
        double quota = templateSDK.getRule().getDiscount().getQuota();

        // 如果不符合标准，则直接返回商品总价
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        // 计算使用优惠券之后的价格
        settlement.setCost(retain2Decimals((goodsSum - quota) > minCost()
                ? (goodsSum - quota) : minCost()));
        log.debug("Use ManJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement);

        return settlement;
    }
}
