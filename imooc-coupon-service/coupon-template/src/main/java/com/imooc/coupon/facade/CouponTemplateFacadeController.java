package com.imooc.coupon.facade;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板内部接口
 *
 * @author wzy
 * @version 1.0
 * @date 2022/7/28 3:44 下午
 */
@Slf4j
@RestController
@RequestMapping("/template/sdk")
public class CouponTemplateFacadeController {
    /**
     * 优惠券模板基础服务
     */
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateFacadeController(ITemplateBaseService templateBaseService) {
        this.templateBaseService = templateBaseService;
    }

    /**
     * 查找可用的优惠券模板，sdk的含义是给第三方调用
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     */
    @GetMapping("/all")
    @IgnorePermission
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板ids到 CouponTemplateSDK的映射
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     */
    @GetMapping("/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam Collection<Integer> ids
    ) {
        log.info("FindIds2TemplateSDK : {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}