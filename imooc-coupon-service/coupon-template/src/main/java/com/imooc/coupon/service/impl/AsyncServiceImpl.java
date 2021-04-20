package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 描述：异步服务实现
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/27 15:15
 **/
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    /**
     * CouponTemplate Dao
     */
    private final CouponTemplateDao templateDao;

    // key 和 value 都是String类型
    /**
     * Redis 操作工具
     */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    @Async(value = "getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        // 计算一下执行的总时间
        Stopwatch watch = Stopwatch.createStarted();

        // 创建优惠券码
        Set<String> couponCodes = buildCouponCode(template);

        // imooc_coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());

        // rightPushAll 返回存入的数量，打印出来
        log.info("Push CouponCode To Redis {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));
        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();

        log.info("Construct Coupon By Template Cost: {}ms",
                watch.elapsed(TimeUnit.SECONDS));
        // TODO: 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /**
     * 构造优惠券码  18位：
     * 每一张优惠券码（对应于一张优惠券，18位）
     * 前四位：产品线 + 类型
     * 中间六位：日期的随机（200909）
     * 后八位：0-9的随机数构成
     *
     * @param template
     * @return Set<String> 与template.count相同个数的优惠券码
     */
    @SuppressWarnings("all")
    public Set<String> buildCouponCode(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> result = new HashSet<>(template.getCount());
        // 前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        // 获取创建时间六位字符串
        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());

        for (int i = 0; i < template.getCount(); ++i) {
            // 初步得到，有可能重复
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();

        watch.stop();
        log.info("Build coupon Code Cost: {} ms",
                watch.elapsed(TimeUnit.SECONDS));
        return result;
    }

    /**
     * 构造优惠券码的后十四位
     *
     * @param date 创建优惠券的日期
     * @return 十四位优惠券码
     */
    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        // 中间六位，将字符转换为char，获取日期里所有字符
        List<Character> chars = date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());

        // 洗牌算法非常高效
        Collections.shuffle(chars);

        // map相当于做转换， Collectors.joining()相当于组合为String
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());

        // RandomStringUtils.random(1, bases) 因为第一位不想为0，所以使用基准的数组，里面不包含0
        // RandomStringUtils.randomNumeric(7) 生成剩下的七位 0-9 也就不需要基准
        String suffix8 = RandomStringUtils.random(1, bases)
                + RandomStringUtils.randomNumeric(7);


        return mid6 + suffix8;
    }

}
