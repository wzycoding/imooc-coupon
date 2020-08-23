package com.imooc.coupon;

import com.imooc.coupon.permission.PermissionClient;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * 权限探测监听器，Spring容器启动之后自动运行
 *
 * @author wzy
 * @date 2020-08-22 11:18
 **/
@Slf4j
@Component
public class PermissionDetectListener
        implements ApplicationListener<ApplicationReadyEvent> {
    private static final String KEY_SERVER_CTX = "server.servlet.context-path";
    private static final String KEY_SERVER_NAME = "spring.application.name";


    /**
     * 当容器启动就回运行方法业务逻辑
     */
    @Override
    @SuppressWarnings("all")
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        new Thread(() -> {
            // 扫描权限（注解）
            List<PermissionInfo> infoList = scanPermission(ctx);

            // 注册权限
            registerPermission(infoList, ctx);
        }).start();
    }

    /**
     * 注册接口权限
     */
    private void registerPermission(List<PermissionInfo> infoList,
                                    ApplicationContext ctx) {
        log.info("*********************register permission********************************");
        PermissionClient permissionClient = ctx.getBean(PermissionClient.class);
        if (null == permissionClient) {
            log.error("no permissionClient bean found");
        }

        //取出service name
        String serviceName = ctx.getEnvironment().getProperty(KEY_SERVER_NAME);
        log.info("serviceName : {}", serviceName);
        boolean result = new PermissionRegistry(
                permissionClient, serviceName
        ).register(infoList);

        if (result) {
            log.info("*********************done permission********************************");
        }
    }


    /**
     * 扫描微服务中的 Controller 接口权限信息
     */
    private List<PermissionInfo> scanPermission(ApplicationContext ctx) {
        // 取出 context前缀
        String pathPrefix = ctx.getEnvironment().getProperty(KEY_SERVER_CTX);

        // 取出Controller的映射bean
        RequestMappingHandlerMapping mappingBean =
                (RequestMappingHandlerMapping) ctx.getBean("requestMappingHandlerMapping");

        // 扫描权限
        List<PermissionInfo> permissionInfoList = new AnnotationScanner(pathPrefix).scanPermission(
                mappingBean.getHandlerMethods()
        );

        permissionInfoList.forEach(p -> log.info("{}", p));
        log.info("{} permission found", permissionInfoList.size());
        log.info("*********************done scanning********************************");
        return permissionInfoList;
    }
}
