package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * path表对应的dao接口
 *
 * @author wzy
 * @date 2020-08-17 0:20
 **/
public interface PathRepository extends JpaRepository<Path, Integer> {
    /**
     * 根据微服务名称查询所有的路径信息
     *
     * @param serviceName 微服务名称
     * @return 路径信息列表
     */
    List<Path> findAllByServiceName(String serviceName);

    /**
     * 根据路径模式 + 请求类型 查找数据记录
     *
     * @param pathPattern 路径模式
     * @param httpMethod  请求类型
     * @return 路径信息
     */
    Path findByPathPatternAndHttpMethod(String pathPattern, String httpMethod);

}
