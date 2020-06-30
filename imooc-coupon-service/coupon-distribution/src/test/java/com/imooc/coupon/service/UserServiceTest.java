package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constants.CouponStatus;
import com.imooc.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 描述：用户服务功能测试用例
 *
 * @Author wzy
 * @Date 2020/7/1 0:01
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    // fake一个userId
    private Long fakeUserId = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void testFindCouponByStatus() throws CouponException {
        System.out.println(JSON.toJSONString(
                userService.findCouponByStatus(fakeUserId, CouponStatus.USABLE.getCode())
        ));

        System.out.println(JSON.toJSONString(
                userService.findCouponByStatus(fakeUserId, CouponStatus.USED.getCode())
        ));

        System.out.println(JSON.toJSONString(
                userService.findCouponByStatus(fakeUserId, CouponStatus.EXPIRED.getCode())
        ));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException {
        System.out.println(JSON.toJSONString(
                userService.findAvailableTemplate(fakeUserId)
        ));
    }
}
