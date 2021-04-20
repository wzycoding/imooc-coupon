package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.GoodsType;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import com.imooc.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * 描述：结算规则执行管理器测试用例
 * 对 Executor的分发与结算逻辑进行测试
 *
 * @author wzy
 * @version V1.0
 * //
 * @date 2020/7/2 19:23
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecuteManagerTest {
    /**
     * fake一个userId
     */
    private Long fakeUserId = 20001L;

    @Autowired
    private ExecuteManager manager;

    @Test
    public void testComputeRule() throws CouponException {
        // 满减优惠券结算测试
//        log.info("ManJian Coupon Executor Test");
//        SettlementInfo manJianSettlement = fakeManJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(manJianSettlement);
//        System.out.println(JSON.toJSONString(result));

        // 折扣优惠券结算测试
//        log.info("ZheKou Coupon Executor Test");
//        SettlementInfo zheKouSettlement = fakeZheKouCouponSettlement();
//        SettlementInfo result = manager.computeRule(zheKouSettlement);
//        System.out.println(JSON.toJSONString(result));

        // 立减优惠券结算测试
//        log.info("LiJian Coupon Executor Test");
//        SettlementInfo liJianSettlement = fakeLiJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(liJianSettlement);
//        System.out.println(JSON.toJSONString(result));

        // 满减折扣优惠券结算测试
        log.info("ManJian ZheKou Coupon Executor Test");
        SettlementInfo manJianZheKouSettlement = fakeManAndJianZheKouSettlement();
        SettlementInfo result = manager.computeRule(manJianZheKouSettlement);
        System.out.println(result);
    }

    /**
     * fake满减优惠券的结算信息
     *
     * @return
     */
    private SettlementInfo fakeManJianCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(1);
        templateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        templateSDK.setKey("100120190801");

        TemplateRule rule = new TemplateRule();
        // 满199-20
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("浙江省", "杭州市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()))));
        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * fake折扣优惠券的计算信息
     *
     * @return
     */
    private SettlementInfo fakeZheKouCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("100220190712");

        TemplateRule rule = new TemplateRule();
        // 满1 85折
        rule.setDiscount(new TemplateRule.Discount(85, 1));
        rule.setUsage(new TemplateRule.Usage("浙江省", "杭州市",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()))));

        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;

    }

    /**
     * fake结算信息
     */
    private SettlementInfo fakeLiJianCouponSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();

        ctInfo.setId(1);
        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(3);
        templateSDK.setKey("100320190501");
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setUsage(new TemplateRule.Usage("浙江省", "杭州市",
                JSON.toJSONString(Arrays.asList(GoodsType.WENYU.getCode(),
                        GoodsType.JIAJU.getCode()))));
        rule.setDiscount(new TemplateRule.Discount(10, 20));

        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }


    /**
     * fake满减折扣优惠券对象
     */
    public SettlementInfo fakeManAndJianZheKouSettlement() {
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo manJianCtInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        SettlementInfo.CouponAndTemplateInfo zheKouCtInfo =
                new SettlementInfo.CouponAndTemplateInfo();

        manJianCtInfo.setId(1);
        zheKouCtInfo.setId(2);

        CouponTemplateSDK manJianTemplateSDK = new CouponTemplateSDK();
        CouponTemplateSDK zheKouTemplateSDK = new CouponTemplateSDK();

        manJianTemplateSDK.setId(4);
        manJianTemplateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        manJianTemplateSDK.setKey("100420190801");

        zheKouTemplateSDK.setId(5);
        zheKouTemplateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        zheKouTemplateSDK.setKey("100420180202");

        TemplateRule manJianRule = new TemplateRule();
        TemplateRule zheKouRule = new TemplateRule();

        manJianRule.setDiscount(new TemplateRule.Discount(100, 20));
        manJianRule.setUsage(new TemplateRule.Usage("浙江省", "杭州市",
                JSON.toJSONString(Arrays.asList(GoodsType.WENYU.getCode(), GoodsType.JIAJU.getCode()))));
        manJianRule.setWeight(JSON.toJSONString(Collections.singletonList("1004201802020005")));

        zheKouRule.setDiscount(new TemplateRule.Discount(85, 20));
        zheKouRule.setUsage(new TemplateRule.Usage("浙江省", "杭州市",
                JSON.toJSONString(Arrays.asList(GoodsType.WENYU.getCode(), GoodsType.JIAJU.getCode()))));
        zheKouRule.setWeight(JSON.toJSONString(Collections.singletonList("1004201908010004")));

        manJianTemplateSDK.setRule(manJianRule);
        zheKouTemplateSDK.setRule(zheKouRule);

        manJianCtInfo.setTemplate(manJianTemplateSDK);
        zheKouCtInfo.setTemplate(zheKouTemplateSDK);

        info.setCouponAndTemplateInfos(Arrays.asList(manJianCtInfo, zheKouCtInfo));

        return info;
    }
}
