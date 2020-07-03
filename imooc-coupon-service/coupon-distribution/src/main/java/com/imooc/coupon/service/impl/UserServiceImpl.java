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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
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
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId)
            throws CouponException{
        long curTime = new Date().getTime();

        List<CouponTemplateSDK> templateSDKS =
                templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count: {}",
                templateSDKS.size());
        // 因为优惠券模板的过期策略是靠定时任务，所以存在一定延迟，要在获取模板时校验一下
        templateSDKS = templateSDKS.stream().filter(
                t -> t.getRule().getExpiration().getDeadLine() > curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key是templateId
        // value中的(left)key是 Template limitation(领取上限), (right)value是优惠券模板
        // 这里的Pair 和 Map的区别就是Pair是一对值，而Map是key和value
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());

        templateSDKS.forEach(
                t -> limit2Template.put(t.getId(),
                        Pair.of(t.getRule().getLimitation(), t))
        );

        // 最终返回的结果
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        // 用户当前可用优惠券
        List<Coupon> userUsableCoupons = findCouponByStatus(
                userId, CouponStatus.USABLE.getCode()
        );
        log.debug("Current User Has Usable Coupons: {}, {}", userId,
                userUsableCoupons.size());

        // key是templateId, value用户已经领取的优惠券list,根据模板id进行分组
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        // 根据Template 的Rule判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            // 如果用户有优惠券 且领取的数量大于等于领取限制
            if (templateId2Coupons.containsKey(k)
                    && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }
            result.add(templateSDK);
        });
        return result;
    }

    /**
     * 用户领取优惠券
     * 1、从TemplateClient 拿到对应的优惠券模板，并检查是否过期
     * 2、根据limitation 判断用户是否可以领取
     * 3、save to db
     * 4、填充CouponTemplateSDK
     * 5、save to cache
     * @param request {@link AcquireTemplateRequest}
     */

    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request)
            throws CouponException {
        Map<Integer, CouponTemplateSDK> ids2TemplateSDK = templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();

        // todo:这里应该做优惠券模板是否过期做检查

        // 优惠券模板是需要存在的
        if(ids2TemplateSDK.size() <= 0) {
            log.error("Can Not Acquire Template From TemplateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }
        // 用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode()
        );

        // 获取优惠券模板id到优惠券的映射
        Map<Integer, List<Coupon>> templateId2Coupons =
                userUsableCoupons.stream()
                        .collect(Collectors.groupingBy(Coupon::getTemplateId));

        // 判断用户领取是否超出限制 todo:这里的limitation不可信
        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
                && templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
        request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }
        // 尝试去获取优惠券码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon newCoupon = new Coupon(
                request.getTemplateSDK().getId(), request.getUserId(),
                couponCode, CouponStatus.USABLE

        );
        // 返回一个带id的优惠券对象
        newCoupon = couponDao.save(newCoupon);

        // 填充coupon对象的CouponTemplateSDK, 一定要放在缓存之前填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        // 放到缓存中
        redisService.addCouponToCache(
                request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode()
        );

        return newCoupon;
    }

    /**
     * 结算核销优惠券
     * 这里要注意，规则相关处理需要由 Settlement 系统去做，当前系统仅仅做业务和校验。
     * @param info {@link SettlementInfo}
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info)
            throws CouponException {
        // 当没有传递优惠券时，直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos =
                info.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(ctInfos)) {
            log.info("Empty Infos For Settle.");
            double goodsSum = 0.0;

            // 计算商品总价
            for (GoodsInfo goodsInfo : info.getGoodsInfos()) {
                goodsSum += goodsInfo.getPrice() * goodsInfo.getCount();
            }
            // 没有优惠券也就不存在优惠券的核销， SettlementInfo 其他的字段不需要修改
            info.setCost(retain2Decimals(goodsSum));
        }

        // 校验传递的优惠券是不是用户自己的
        List<Coupon> coupons = findCouponByStatus(
                info.getUserId(), CouponStatus.USABLE.getCode());
        // 优惠券id -> 优惠券对象 Function.identity()对象本身
        Map<Integer, Coupon> id2Coupon = coupons.stream()
                .collect(Collectors.toMap(Coupon::getId,
                        Function.identity())
                );
        // 用户的优惠券列表为空，或者传进来的优惠券列表不是用户优惠券列表的子集
        if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                .collect(Collectors.toList()), id2Coupon.keySet()
        )) {
            log.info("{}", id2Coupon.keySet());
            log.info("{}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                    .collect(Collectors.toList()));
            log.error("User Coupon Has Some Problem, It Is Not SubCollection" +
                    "Of Coupons");
            throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection" +
                    "Of Coupons");
        }
        log.debug("Current Settlement Coupons Is User's: {}", ctInfos.size());
        // 优惠券id转到优惠券本身
        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

        // 通过结算服务获取结算信息
        SettlementInfo processedInfo =
                settlementClient.computeRule(info).getData();

        // 如果返回的couponAndTemplateInfos为空，则说明出错了，
        // 因为校验没有问题一定会用到优惠券模板微服务

        // 如果是核销，并且优惠券不为空，是需要核销的
        if (processedInfo.getEmploy() && CollectionUtils.isNotEmpty(
                processedInfo.getCouponAndTemplateInfos()
        )) {
            log.info("Settle Info Coupon: {}, {}", info.getUserId(),
                    JSON.toJSONString(settleCoupons));
            // 更新缓存
            redisService.addCouponToCache(
                    info.getUserId(), settleCoupons, CouponStatus.USABLE.getCode());
            // 更新db
            kafkaTemplate.send(Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList()))));
        }
        return processedInfo;
    }

    /**
     * 保留两位小数
     * @param value 目标数值
     */
    private double retain2Decimals(double value) {
        // BigDecimal.ROUND_HALF_UP表示四舍五入
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
