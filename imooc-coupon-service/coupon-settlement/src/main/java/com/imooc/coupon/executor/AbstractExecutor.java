package com.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：规则执行器抽象类，定义通用方法
 *
 * @Author wzy
 * @Date 2020/7/1 9:18
 * @Version V1.0
 **/
public abstract class AbstractExecutor {

    /**
     * 校验商品类型与优惠券是否匹配
     * 需要注意：
     * 1.这里实现的是单品类优惠券校验，多品类优惠券重载此方法
     * 2.商品只需要有一个优惠券要求的商品类型去匹配就可以
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        // 获得要结算的商品的goodsType
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());

        // 只包含一张优惠券,因为是单品类
        // 获得优惠券模板的goodsType
        List<Integer> templateGoodsType = JSON.parseObject(settlement.getCouponAndTemplateInfos().get(0).getTemplate().
                getRule().getUsage().getGoodsType(), List.class);

        // 判断是否存在交集
        return CollectionUtils.isNotEmpty(
                // CollectionUtils.intersection 判断是否存在交集方法
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }

    /**
     * 处理商品类型与优惠券限制不匹配的情况
     * @param settlement {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum 商品原始总价
     * @return {@link SettlementInfo} 已经修改的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(
            SettlementInfo settlement, double goodsSum
    ) {
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlement);
        // 当商品类型不满足时，直接返回总价
        if (!isGoodsTypeSatisfy) {
            // 设置为原始总价
            settlement.setCost(goodsSum);
            // 清空优惠券
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        return null;
    }

    /**
     * 商品总价
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        // 单价 * 数量求和
        return goodsInfos.stream()
                .mapToDouble(g -> g.getPrice() * g.getCount()).sum();
    }

    /**
     * 保留两位小数
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * 最小支付费用：满减之后可能变成0或者负数，显然不合理
     * 设置一个最小值
     */
    protected double minCost() {
        return 0.1;
    }

}
