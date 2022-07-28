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
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 12:33
 **/
@SuppressWarnings("all")
public interface ITemplateBaseService {
    /**
     * 根据优惠券模板id获取优惠券模板信息（查看详情接口）
     * [运营人员用]
     *
     * @param templateId 模板id
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException 抛出异常
     */
    CouponTemplate buildTemplateInfo(Integer templateId) throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * 【其他微服务用，如分发微服务】
     *
     * @return {@link CouponTemplateSDK} 所有可用优惠券模板信息
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板ids 到CouponTemplateSDK的映射\
     * 【其他微服务用，如分发微服务】
     *
     * @param ids 模板 ids
     * @return Map<key: 模板id, value: CouponTemplateSDK>
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
