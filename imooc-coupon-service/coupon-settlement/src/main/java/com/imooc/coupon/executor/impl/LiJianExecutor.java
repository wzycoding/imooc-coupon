package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 描述：立减优惠券结算规则执行器
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/1 17:06
 **/
@Slf4j
@Service
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        // 获取商品总价
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability =
                processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (probability != null) {
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        // 立减优惠券直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        // 获取优惠额度
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券后的价格
        settlement.setCost(
                retain2Decimals(
                        (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost())
        );

        // 打印原始价格和优惠价格
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}", goodsSum,
                settlement.getCost());

        return settlement;
    }
}
