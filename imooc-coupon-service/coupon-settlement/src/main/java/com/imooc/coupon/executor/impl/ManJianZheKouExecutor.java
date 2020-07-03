package com.imooc.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：满减 + 折扣优惠券规则执行器
 *
 * @Author wzy
 * @Date 2020/7/1 17:57
 * @Version V1.0
 **/
@Slf4j
@Service
public class ManJianZheKouExecutor extends AbstractExecutor
        implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANIAN_ZHEKOU;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));

        // 商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );

        if (null != probability) {
            log.debug("ManJian And ZheKou Is Not Match To GoodsType");
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        // 这里已经确定包含两张优惠券：满减和折扣不需要重新校验
        for (SettlementInfo.CouponAndTemplateInfo ct : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory())
                    == CouponCategory.MANJIAN) {
                manJian = ct;
            } else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        // 当前的优惠券和满减券如果不能共用（一起使用），那么需要清空优惠券返回商品原价
        if (!isTemplateCanShared(manJian, zheKou)) {
            log.debug("Current ManJian And ZheKou Can Not Shared");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        // 做结算
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();

        double manjianBase = (double) manJian.getTemplate()
                .getRule().getDiscount().getBase();
        double manjianQuota = (double) manJian.getTemplate()
                .getRule().getDiscount().getQuota();

        // 最终价格
        double targetSum = goodsSum;
        if (targetSum >= manjianBase) {
            targetSum -= manjianQuota;
            ctInfos.add(manJian);
        }

        // 计算折扣
        double zhekouQuota = (double) zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        targetSum *= zhekouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());

        return settlement;
    }

    /**
     * 当前两张优惠券是否可以共用
     * 即校验 TemplateRule中的weight是否满足条件
     * @param manJian
     * @param zheKou
     * @return
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                                        SettlementInfo.CouponAndTemplateInfo zheKou) {
        // 优惠券模板也有唯一识别码，保存在key属性中，没有填充id四位
        // 获取优惠券模板key是因为weight中存的就是template_key
        String manjianKey = manJian.getTemplate().getKey()
                + String.format("%04d", manJian.getTemplate().getId());

        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());

        List<String> allSharedKeysForManJian = new ArrayList<>();
        allSharedKeysForManJian.add(manjianKey);
        allSharedKeysForManJian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZheKou = new ArrayList<>();
        allSharedKeysForZheKou.add(zhekouKey);
        allSharedKeysForZheKou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));
        // 判断两张优惠券是否可以同时使用，如果满减可叠加的优惠券是当前两张优惠券的子集，或者
        // 折扣可叠加的优惠券时当前两张优惠券的子集，就表示两张优惠券可以同时使用
        return CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForManJian)  ||
                CollectionUtils.isSubCollection(Arrays.asList(manjianKey, zhekouKey), allSharedKeysForZheKou);
    }

    /**
     * 校验商品类型和优惠券是否匹配
     * 1、满减 + 折扣的校验
     * 2、如果想要使用多类优惠券，则必须要所有的商品类型都必须包含在内，即差集为空
     * @param settlement
     * @return
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());

        // 优惠券模板所支持的商品类型
        List<Integer> templateGoodsType = new ArrayList<>();

        settlement.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        // 如果想要使用多类优惠券，则必须要所有的商品类型都必须包含在内，即差集为空
        // goodsType - templateGoodsType 差集是空的
        // templateGoodsType - goodsType 不一定为空
        // 因为优惠券可能能用于多种品类，而商品只属于一种品类

        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, templateGoodsType
        ));
    }
}
