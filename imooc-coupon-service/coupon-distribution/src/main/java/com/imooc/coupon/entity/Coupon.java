package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.converter.CouponStatusConvertor;
import com.imooc.coupon.serialization.CouponSerialize;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 描述：优惠券(用户领取的优惠券记录)实体类
 *
 * @Author wzy
 * @Date 2020/6/28 15:08
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupon")
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 关联优惠券模板id
     */
    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    /**
     * 领取用户
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 优惠券码
     */
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    /**
     * 领取时间
     */
    @CreatedDate
    @Column(name = "assign_time", nullable = false)
    private Date assignTime;

    /**
     * 优惠券状态
     */
    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConvertor.class)
    private CouponStatus status;

    /**
     * 用户优惠券对应的模板信息
     */
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * 返回一个无效的Coupon对象
     */
    public static Coupon invalidCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * 构造优惠券
     */
    public Coupon(Integer templateId, Long userId, String couponCode,
                  CouponStatus status) {
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
