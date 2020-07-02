package com.imooc.coupon.executor;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：优惠券结算规则执行管理器
 * 即根据用户的请求（SettlementInfo）找到对应的Executor，去做结算
 * BeanPostProcessor bean后置处理器，当系统中所有的bean都被创建处理之后，才会调用这个类
 * @Author wzy
 * @Date 2020/7/2 9:56
 * @Version V1.0
 **/
@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);

    /**
     * 优惠券结算规则计算入口
     * 注意：一定要保证传递尽量的优惠券个数 >= 1
     * @param settlement
     * @return
     * @throws CouponException
     */
    public SettlementInfo computeRule(SettlementInfo settlement)
            throws CouponException {

        SettlementInfo result = null;

        // 单类优惠券
        if (settlement.getCouponAndTemplateInfos().size() == 1) {
            // 获取优惠券的类别
            CouponCategory category = CouponCategory.of(settlement.getCouponAndTemplateInfos().get(0)
                    .getTemplate().getCategory());
            switch (category) {
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MAJIAN).computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU).computeRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN).computeRule(settlement);
                    break;
            }
        } else {
            // 多类优惠券
            List<CouponCategory> categories = new ArrayList<>(
                    settlement.getCouponAndTemplateInfos().size()
            );

            settlement.getCouponAndTemplateInfos().forEach( ct -> {
                categories.add(CouponCategory.of(ct.getTemplate().getCategory()));
            });

            if (categories.size() != 2) {
                throw new CouponException("Not Support For More " +
                        "Template Category");
            } else {
                if (categories.contains(CouponCategory.MANJIAN)
                        && categories.contains(CouponCategory.ZHEKOU)) {
                    result = executorIndex.get(RuleFlag.MANIAN_ZHEKOU)
                            .computeRule(settlement);
                } else {
                    throw new CouponException("Not Support For Other " +
                            "Template Category");
                }
            }
        }
        return result;
    }
    /**
     * 在bean初始化之前执行（before）
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        // 如果不是RuleExecutor的实例
        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        // 已经注册过这个类型的规则执行器
        if (executorIndex.containsKey(executor.ruleConfig())) {
            throw new IllegalStateException("There is already an executor" +
                    "for rule flag: " + ruleFlag);
        }

        log.info("Load executor {} for rule flag {}, ", executor.getClass(), ruleFlag);
        // 将优惠券结算执行器放到map中
        executorIndex.put(ruleFlag, executor);
        return bean;
    }

    /**
     * 在bean初始化之后执行（after）
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return null;
    }
}
