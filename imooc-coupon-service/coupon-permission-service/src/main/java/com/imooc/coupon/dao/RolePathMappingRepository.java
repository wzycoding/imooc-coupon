package com.imooc.coupon.dao;

import com.imooc.coupon.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 角色路径映射表dao接口
 *
 * @author wzy
 * @date 2020-08-17 0:27
 **/
public interface RolePathMappingRepository
        extends JpaRepository<RolePathMapping, Integer> {
    /**
     * 通过角色id和路径id查询出记录
     *
     * @param roleId 角色id
     * @param pathId 路径id
     * @return 角色路径映射信息
     */
    RolePathMapping findByRoleIdAndPathId(Integer roleId, Integer pathId);
}
