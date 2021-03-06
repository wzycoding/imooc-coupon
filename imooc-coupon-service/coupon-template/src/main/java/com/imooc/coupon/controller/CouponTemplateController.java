package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.annotation.ImoocCouponPermission;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>描述：优惠券模板相关的功能控制器</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/27 23:54
 **/
@Slf4j
@RestController
public class CouponTemplateController {
    /**
     * 构建优惠券模板服务
     */
    private final IBuildTemplateService buildTemplateService;

    /**
     * 优惠券模板基础服务
     */
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * 构建优惠券模板
     * 127.0.0.1:7001/coupon-template/template/build
     */
    @PostMapping("/template/build")
    @ImoocCouponPermission(readOnly = false, description = "buildTemplate")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request)
            throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 构造优惠券模板详情
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     */
    @GetMapping("/template/info")
    @ImoocCouponPermission(description = "buildTemplateInfo")
    public CouponTemplate buildTemplateInfo(@RequestParam Integer id)
            throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查找可用的优惠券模板，sdk的含义是给第三方调用
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     */
    @GetMapping("/template/sdk/all")
    @IgnorePermission
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板ids到 CouponTemplateSDK的映射
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam Collection<Integer> ids
    ) {
        log.info("FindIds2TemplateSDK : {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
