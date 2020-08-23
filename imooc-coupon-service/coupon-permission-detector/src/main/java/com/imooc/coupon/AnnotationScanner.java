package com.imooc.coupon;

import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.annotation.ImoocCouponPermission;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 接口权限信息扫描器
 *
 * @author wzy
 * @date 2020-08-22 0:31
 **/
@Slf4j
public class AnnotationScanner {

    /**
     * 微服务路径前缀
     */
    private String pathPrefix;

    private static final String IMOOC_COUPON_PKG = "com.imooc.coupon";


    public AnnotationScanner(String pathPrefix) {
        this.pathPrefix = trimPath(pathPrefix);
    }

    /**
     * 构造所有Controller的权限信息
     */
    List<PermissionInfo> scanPermission(
            Map<RequestMappingInfo, HandlerMethod> mappingMap
    ) {
        List<PermissionInfo> result = new ArrayList<>();
        mappingMap.forEach((mapInfo, method)
                -> result.addAll(buildPermission(mapInfo, method)));
        return result;
    }

    /**
     * 构造Controller的权限信息
     *
     * @param mapInfo       @RequestMapping信息
     * @param handlerMethod 对应方法的详情信息 对应方法的详情，包括方法、类、参数
     */
    private List<PermissionInfo> buildPermission(
            RequestMappingInfo mapInfo, HandlerMethod handlerMethod
    ) {
        Method javaMethod = handlerMethod.getMethod();
        Class baseClass = javaMethod.getDeclaringClass();

        //忽略 com.imooc.coupon 下的mapping
        if (!isImoocCouponPackage((baseClass.getName()))) {
            log.debug("ignore method: {}", javaMethod.getName());
            return Collections.emptyList();
        }
        // 判断是否忽略此方法
        IgnorePermission ignorePermission = javaMethod.getAnnotation(
                IgnorePermission.class
        );
        if (null != ignorePermission) {
            log.debug("ignore method: {}", javaMethod.getName());
            return Collections.emptyList();
        }

        ImoocCouponPermission couponPermission = javaMethod.getAnnotation(
                ImoocCouponPermission.class
        );
        if (null == couponPermission) {
            // 如果没有标注且没有IgnorePermission，在日志中记录
            log.error("lack @ImoocCouponPermission -> {}#{}",
                    javaMethod.getDeclaringClass().getName(),
                    javaMethod.getName());
            return Collections.emptyList();
        }
        // 取出URL
        Set<String> urlSet = mapInfo.getPatternsCondition().getPatterns();

        // 取出method
        boolean isAllMethods = false;
        Set<RequestMethod> methodSet = mapInfo.getMethodsCondition().getMethods();
        if (CollectionUtils.isEmpty(methodSet)) {
            isAllMethods = true;
        }

        List<PermissionInfo> infoList = new ArrayList<>();
        for (String url : urlSet) {
            // 支持的 http method为全量
            if (isAllMethods) {
                PermissionInfo info = buildPermissionInfo(
                        HttpMethodEnum.ALL.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.description(),
                        couponPermission.extra()
                );
                infoList.add(info);
                continue;
            }
            // 支持部分http method
            for (RequestMethod method : methodSet) {
                PermissionInfo info = buildPermissionInfo(
                        method.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.description(),
                        couponPermission.extra()
                );
                infoList.add(info);
                log.info("permission detected: {}", info);
            }
        }
        return infoList;
    }

    /**
     * 构造单个接口的权限信息
     */
    private PermissionInfo buildPermissionInfo(
            String reqMethod, String javaMethod, String path,
            boolean readOnly, String desp, String extra
    ) {
        PermissionInfo info = new PermissionInfo();
        info.setMethod(reqMethod);
        info.setUrl(path);
        info.setIsRead(readOnly);
        info.setDescription(
                // 如果注解中没有描述则使用方法名称
                StringUtils.isEmpty(desp) ? javaMethod : desp
        );

        return info;
    }

    /**
     * 判断当前类是否在我们定义的包中
     */
    private boolean isImoocCouponPackage(String className) {
        return className.startsWith(IMOOC_COUPON_PKG);
    }


    /**
     * 保证path以/开头且不以/结尾
     * 如果user -> /user, /user/ -> /user
     */
    private String trimPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
