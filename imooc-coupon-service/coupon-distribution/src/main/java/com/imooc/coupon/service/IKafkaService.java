package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 描述：Kafka相关的服务接口定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 17:43
 **/
public interface IKafkaService {
    /**
     * 消费优惠券 kafka消息
     *
     * @param record {@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
