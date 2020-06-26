package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 描述：优惠券模板基础(view, delete)服务定义
 *
 * @Author wzy
 * @Date 2020/6/26 12:33
 * @Version V1.0
 **/
public interface ITemplateBaseService {
    /**
     * 根据优惠券模板id获取优惠券模板信息
     * [运营用]
     * @param id 模板id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;
    /**
     *
     * 查找所有可用的优惠券模板
     * 【其他微服务用】
     * @return {@link CouponTemplateSDK} s
     *
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板ids 到CouponTemplateSDK的映射\
     * 【其他微服务用】
     * @param ids 模板 ids
     * @return Map<key: 模板id, value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
