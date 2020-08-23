package com.imooc.coupon.permission;

import com.imooc.coupon.vo.CheckPermissionRequest;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CreatePathRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 路径创建与权限校验功能feign接口实现
 *
 * @author wzy
 * @date 2020-08-21 21:48
 **/
@FeignClient(value = "eureka-client-coupon-permission")
public interface PermissionClient {

    /**
     * 批量创建路径信息
     *
     * @param request 请求对象
     * @return 批量创建的路径对象的id
     */
    @PostMapping(value = "/coupon-permission/create/path")
    CommonResponse<List<Integer>> createPath(
            @RequestBody CreatePathRequest request
    );

    /**
     * 校验权限信息
     *
     * @param request 权限信息请求对象
     * @return 校验是否通过
     */
    @PostMapping("/coupon-permission/check/permission")
    Boolean checkPermission(@RequestBody CheckPermissionRequest request);
}
