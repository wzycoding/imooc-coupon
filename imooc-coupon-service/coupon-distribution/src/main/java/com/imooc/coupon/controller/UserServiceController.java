package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：用户服务controller
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/30 15:59
 **/
@Slf4j
@RestController
public class UserServiceController {
    /**
     * 用户服务接口
     */
    private final IUserService userService;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户id和优惠券状态查找用户优惠券记录
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(@RequestParam Long userId,
                                            @RequestParam Integer status) throws CouponException {
        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponByStatus(userId, status);
    }

    /**
     * 根据用户id查找当前可以领取的用户模板
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam Long userId) throws CouponException {
        log.info("Find Available Template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }


    /**
     * 用户领取优惠券
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request)
            throws CouponException {
        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * 结算（核销优惠券）
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
