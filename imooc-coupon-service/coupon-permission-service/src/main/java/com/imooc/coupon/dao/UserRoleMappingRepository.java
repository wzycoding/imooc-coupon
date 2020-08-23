package com.imooc.coupon.dao;

import com.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户角色映射表dao
 *
 * @author wzy
 * @date 2020-08-17 0:30
 **/
public interface UserRoleMappingRepository
        extends JpaRepository<UserRoleMapping, Long> {
    /**
     * 通过用户id查找记录
     *
     * @param userId 用户id
     * @return 返回记录
     */
    UserRoleMapping findByUserId(Long userId);
}
