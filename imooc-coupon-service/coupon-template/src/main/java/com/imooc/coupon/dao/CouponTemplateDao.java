package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>优惠券模板dao接口</h1>
 * T 实体类的类型
 * ID 主键类型
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 10:06
 **/
public interface CouponTemplateDao
        extends JpaRepository<CouponTemplate, Integer> {
    /**
     * 根据模板名称查询模板
     * where name = ?
     *
     * @param name 模板名称
     * @return 优惠券模板
     */
    CouponTemplate findByName(String name);

    /**
     * 根据 available和 expired标记查找模板记录
     * where available = ... and expired...
     *
     * @param available 是否可以用
     * @param expired   是否过期
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findAllByAvailableAndExpired(
            Boolean available, Boolean expired
    );


    /**
     * 根据 expired 标记查找模板记录
     * where expired = ...
     *
     * @param expired 是否失效
     * @return 优惠券模板列表
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);


}
