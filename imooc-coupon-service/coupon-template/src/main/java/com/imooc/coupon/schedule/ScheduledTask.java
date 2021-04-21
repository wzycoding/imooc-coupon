package com.imooc.coupon.schedule;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>清理过期优惠券模板定时任务</h1>
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/27 22:10
 **/
@Slf4j
@Component
public class ScheduledTask {
    /**
     * CouponTemplateDao
     */
    private final CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * 下线过期优惠券模板，每隔60分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("Start To Expire CouponTemplate");
        List<CouponTemplate> templates =
                templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)) {
            log.info("Done To Expire CouponTemplate.");
            return;
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates =
                new ArrayList<>(templates.size());
        templates.forEach(t -> {
            // 根据优惠券模板规则中的“过期规则”进行判断
            TemplateRule rule = t.getRule();
            // 比当前时间小，说明已经过期
            if (rule.getExpiration().getDeadLine() < cur.getTime()) {
                t.setExpired(true);
                expiredTemplates.add(t);
            }

        });
        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num: {}",
                    templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate.");
    }
}
