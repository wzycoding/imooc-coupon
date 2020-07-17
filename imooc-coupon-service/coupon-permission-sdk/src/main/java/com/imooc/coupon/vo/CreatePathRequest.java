package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述：路径创建请求对象定义
 *
 * @Author wzy
 * @Date 2020/7/17 13:35
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePathRequest {

    private List<PathInfo> pathInfos;

    /**
     * 路径信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PathInfo {
        /**
         * 路径模式
         */
        private String pathPattern;

        /**
         * Http方法类型
         */
        private String httpMethod;

        /**
         * 路径名称
         */
        private String pathName;

        /**
         * 服务名称
         */
        private String serviceName;

        /**
         * 读写模式：有没有操作数据库：READ,WRITE
         */
        private String opMode;
    }
}
