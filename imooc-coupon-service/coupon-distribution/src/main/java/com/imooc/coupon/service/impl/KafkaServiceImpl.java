package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IKafkaService;
import com.imooc.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 描述：Kafka相关的服务接口实现
 * 核心思想，将Cache中的coupon的状态变化同步到DB中
 * 先修改cache然后在投放到kafka再去修改数据库优惠券状态
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/29 14:05
 **/
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    /**
     * coupon的dao接口
     */
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * 接收Kafka消息
     * 消息被序列化到record
     *
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-x")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        // ofNullable可以为空
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        // 如果存在消息
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(), CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage: {}", JSON.toJSONString(message));

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }
    }

    /**
     * 修改优惠券状态为已使用
     * 为什么要包一层，可能会给用户不同的提示，处理方式不一样，比如使用，
     * 这种哟会力度比较多，比较重要的操作可能就会发送短信，
     * 方面加入其它逻辑而不需要对status进行判断
     */
    private void processUsedCoupons(CouponKafkaMessage message,
                                    CouponStatus status) {
        // TODO 给用户发送推送
        processCouponsByStatus(message, status);
    }

    /**
     * 修改优惠券状态为已过期
     */
    private void processExpiredCoupons(CouponKafkaMessage message,
                                       CouponStatus status) {
        processCouponsByStatus(message, status);
    }

    /**
     * 根据状态去处理优惠券信息
     *
     * @param message kafkaMsg
     * @param status  优惠券状态
     */
    private void processCouponsByStatus(CouponKafkaMessage message,
                                        CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(
                message.getIds()
        );
        if (CollectionUtils.isEmpty(coupons) ||
                coupons.size() != message.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}",
                    JSON.toJSONString(message));
            // todo 发送邮件排查问题
            return;
        }

        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }
}
