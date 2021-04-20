package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 描述：优惠券模板基础服务的测试
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/28 12:20
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {
    @Autowired
    private ITemplateBaseService baseService;

    @Test
    public void testBuildTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(12)
        ));

        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(13)
        ));
    }

    @Test
    public void testFindAllUsableTemplate() {
        System.out.println(JSON.toJSONString(
                baseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindIds2TemplateSDK() {
        System.out.println(JSON.toJSONString(
                baseService.findIds2TemplateSDK(Arrays.asList(12))
        ));
    }

}
