package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：构建优惠券模板服务实现类
 *
 * @Author wzy
 * @Date 2020/6/27 17:41
 * @Version V1.0
 **/
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    /**
     * 异步服务dao
     */
    private final IAsyncService asyncService;

    /**
     * 优惠券模板dao
     */
    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public BuildTemplateServiceImpl(CouponTemplateDao couponTemplateDao, IAsyncService asyncService) {
        this.couponTemplateDao = couponTemplateDao;
        this.asyncService = asyncService;
    }

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException {
        // 参数合法性校验
        if (request.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        // 不允许出现同名优惠券模板，判断同名的优惠券模板是否存在
        if (null != couponTemplateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }
        // 构造CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        // 返回回来的对象有id
        template = couponTemplateDao.save(template);

        // 根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }

    /**
     * 将TemplateRequest转换为CouponTemplate
     * @param request request对象
     * @return CouponTemplate对象
     */
    private CouponTemplate requestToTemplate(TemplateRequest request) {
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
