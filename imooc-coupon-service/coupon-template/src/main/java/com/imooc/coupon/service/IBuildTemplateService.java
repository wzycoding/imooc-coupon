package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.TemplateRequest;

/**
 * 描述：构建优惠券模板接口定义
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 10:29
 **/
public interface IBuildTemplateService {


    /**
     * <h2>创建优惠券模板</h2>
     * 【运营人员用】
     *
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     * @throws CouponException 抛出异常
     */
    CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException;


}
