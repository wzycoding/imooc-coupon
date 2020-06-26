package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 描述：优惠券模板dao接口
 * T 实体类的类型
 * ID 主键类型
 * @Author wzy
 * @Date 2020/6/26 10:06
 * @Version V1.0
 **/
public interface CouponTemplateDao
        extends JpaRepository<CouponTemplate, Integer> {
    /**
     * 根据模板名称查询模板
     * where name = ?
     */
    CouponTemplate findByName(String name);

    /**
     * 根据 available和 expired标记查找模板记录
     * where available = ... and expired...
     */
    List<CouponTemplate> findAllByAvailableAndExpired(
            Boolean available, Boolean expired
    );


    /**
     * 根据 expired 标记查找模板记录
     * where expired = ...
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);


}
