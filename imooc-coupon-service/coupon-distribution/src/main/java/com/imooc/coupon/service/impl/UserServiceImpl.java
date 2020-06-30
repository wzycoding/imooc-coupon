package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述：用户服务相关的接口实现
 * 所有的操作过程，状态都保存在redis中，并通过kafka把消息传递到MySQL中
 * 为什么使用kafka而不是SpringBoot中的异步处理？
 *    因为需要保证一致性，使用高可用的Kafka
 * @Author wzy
 * @Date 2020/6/29 22:57
 * @Version V1.0
 **/
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    /**
     * Coupon Dao
     */
    private final CouponDao couponDao;
    /**
     * Redis服务
     */
    private final IRedisService redisService;
    /**
     * 模板微服务客户端
     */
    private final TemplateClient templateClient;

    /**
     * 结算微服务客户端
     */
    private final SettlementClient settlementClient;

    /**
     * kafka 客户端 <Topic类型，Record类型>
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao,
                           IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status)
            throws CouponException {
        List<Coupon> curCached = redisService.getCacheCoupons(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("Coupon Cache is not Empty: {}, {}", userId, status);
            preTarget = curCached;
            // 用户第一次访问或者缓存失效才会进入到else
        } else {
            log.debug("Coupon cache is empty, get coupon from db: {}, {}",
                    userId, status);

            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status)
            );
            // 如果数据库中没有记录直接返回就可以，Cache中已经加入了一张无效的优惠券
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }

            // 填充dbCoupons 的templateSDK字段,通过微服务调用
            Map<Integer, CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(
                            dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
                    ).getData();
            // 填充TemplateSDK字段
            dbCoupons.forEach(
                    dc -> {
                        dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()));
                    }
            );
            // 数据库中存在记录
            preTarget = dbCoupons;
            // 写回缓存 写入Cache
            redisService.addCouponToCache(
                    userId, preTarget, status
            );
        }
        // preTarget可能会存在无效优惠券,去除无效优惠券
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != -1).
                collect(Collectors.toList());

        // 如果当前获取的是可用优惠券，还需要对已过期优惠券的延迟处理，返回给用户前获取
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            // 分类优惠券并检查是否过期
            CouponClassify classify = CouponClassify.classify(preTarget);
            // 在获取的是可用优惠券的情况下，判断是否有过期优惠券，重新校验了是否过期
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCoupons By Status: {}, {}",
                        userId, status);
                // 会影响两个状态的优惠券USABLE、EXPIRED
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
            }
            // 发送到Kafka 中做异步处理
            kafkaTemplate.send(Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.EXPIRED.getCode(),
                            classify.getExpired().stream()
                            .map(Coupon::getId).collect(Collectors.toList()))
                    ));
            return classify.getUsable();
        }
        // 如果不是可用状态的优惠券，可以直接返回，只有对过期的优惠券才会延期处理
        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) {
        return null;
    }

    @Override
    public Coupon AcquireTemplate(AcquireTemplateRequest request) throws CouponException {
        return null;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
