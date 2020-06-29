package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 描述：Redis相关的操作服务接口定义
 *
 * @Author wzy
 * @Date 2020/6/29 10:07
 * @Version V1.0
 **/
@Slf4j
@Service
public class RedisServerImpl implements IRedisService {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServerImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCacheCoupons(long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);

        // couponStr有可能为空
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(0, null))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }

        return couponStrs.stream().map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 避免缓存穿透
     * @param userId 用户id
     * @param status 优惠券状态列表
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {}, Status: {}",
                userId, JSON.toJSONString(status));
        // key是couponId, value序列化的Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        // 存放无效的优惠券信息
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
        // 使用Redis里的SessionCallback 把数据命令放到Redis的pipeline，pipeline可以一下提交
        // 多条命令，来减少网络延迟
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                status.forEach(s -> {
                    // 获取redisKey
                    String redisKey = status2RedisKey(s, userId);
                    // key value key:redisKey value: Map :key couponId, :value coupon
                    operations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };

        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        // 构造redis key
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        // 获取优惠券码，因为优惠券码不存在顺序关系，有可能返回null
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);

        log.info("Acquire Coupon Code : {}, {}, {}",
                templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId,
                                    List<Coupon> coupons,
                                    Integer status) throws CouponException {
        log.info("Add Coupon To Cache: {}, {}, {}",
                userId, JSON.toJSONString(coupons), status);
        // 保存到缓存中，coupon的个数
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }

        return result;
    }

    /**
     * 新增加优惠券到Cache中
     * @param userId 用户id
     * @param coupons 优惠券列表
     * @return 优惠券的个数
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {

        // 如果status 是USABLE,代表新增加的优惠券
        // 只会影响一个 Cache： USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable");
        Map<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c -> {
            needCachedObject.put(
                    c.getId().toString(), JSON.toJSONString(c)
            );
        });
        String redisKey =
                status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} Coupon To Cache: {}, {}",
                needCachedObject.size(), userId, redisKey);
        // 设置随机的过期时间，避免缓存雪崩
        redisTemplate.expire(redisKey,
                getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

        return needCachedObject.size();
    }

    /**
     * 将已使用的优惠券加入到 Cache中，如果status，表示用户操作是使用当前的优惠券，
     * 将会影响到Cache：USABLE、USED
     *
     * @param userId 用户id
     * @param coupons 优惠券列表
     * @return 优惠券数量
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons)
            throws CouponException{
        log.debug("Add Coupon To Cache For Used.");
        Map<String, String> needCacheForUsed = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );

        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );

        // 获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons =
                getCacheCoupons(userId, CouponStatus.USABLE.getCode());

        // 当前的可用的优惠券个数一定要大于1，因为使用优惠券之前必须存在可用优惠券，
        // 即使不存在，至少有一个无效的优惠券，emptyCoupon id为-1
        // 所以一定要大于1
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> {
            needCacheForUsed.put(
                    c.getId().toString(), JSON.toJSONString(c)
            );
        });

        // 校验当前的优惠券的参数是否与当前cache匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        // 参数传进来的CouponIds
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        // 判断传入的ids是否是可用优惠券id的子集
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal ToCache!");
        }

        List<String> needCleanKey = paramIds.stream().map(Object::toString).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                // 1.已使用的优惠券，加入到Cache缓存
                operations.opsForHash().putAll(
                        redisKeyForUsed, needCacheForUsed
                );
                // 2.可用的优惠券需要清理
                // delete(redisKey, 字典key集合)
                operations.opsForHash().delete(redisKeyForUsable,
                        needCleanKey.toArray());

                // 3. 修改过期时间，使用了相关信息，就要填充新的过期时间，重置过期时间
                operations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );

                // 重置过期时间
                operations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        // 打印日志执行pipeline
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return needCacheForUsed.size();
    }

    /**
     * 将过期优惠券加入的Cache中，
     * status 是Expired，代表是已有的优惠券过期了，影响到两个Cache
     * USABLE, EXPIRED
     * @param userId 用户id
     * @param coupons 优惠券列表
     * @return 优惠券个数
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException{
        log.debug("Add Coupon To Cache For Expired.");

        // 最终需要保存的 Cache, 需要保存的优惠券信息
        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );

        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );

        List<Coupon> curUsableCoupons = getCacheCoupons(userId, CouponStatus.USABLE.getCode());

        // 当前可用的优惠券个数一定是大于1的,因为有一个无效的优惠券在可用的优惠券里面
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForExpired.put(c.getId().toString(), JSON.toJSONString(c)));
        // 校验当前的优惠券是否于Cache中的匹配，也就是说过期的优惠券的id在可用优惠券中是否存在

        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("Current Coupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("Current Coupons Is Not Equal To Cache");
        }

        List<String> needCleanKey = paramIds.stream().map(i -> i.toString())
                .collect(Collectors.toList());

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                // 1、向过期优惠券中添加优惠券列表
                operations.opsForHash().putAll(redisKeyForExpired, needCachedForExpired);

                // 2、从可用优惠券列表删除过期优惠券
                operations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());

                // 3、重置过期时间
                // 重置可用优惠券缓存过期时间
                operations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                // 重置过期优惠券过期时间
                operations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);

                return null;
            }
        };

        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return needCachedForExpired.size();
    }

    /**
     * 根据status获取对应的Redis Key
     * @param status 状态
     * @param userId 用户id
     * @return Redis Key
     */
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus) {
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED, userId);
            break;
            case USABLE:
                redisKey = String.format("%s%s",
                    Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                    Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
            default:break;
        }
        return redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩，key在同一时间失效
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max]直接的随机秒数
     */
    private long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );
    }
}
