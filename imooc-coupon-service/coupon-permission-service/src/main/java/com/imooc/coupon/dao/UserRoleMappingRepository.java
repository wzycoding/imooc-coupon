package com.imooc.coupon.dao;

import com.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * �û���ɫӳ���dao
 *
 * @author wzy
 * @date 2020-08-17 0:30
 **/
public interface UserRoleMappingRepository
        extends JpaRepository<UserRoleMapping, Long> {
    /**
     * ͨ���û�id���Ҽ�¼
     *
     * @param userId �û�id
     * @return ���ؼ�¼
     */
    UserRoleMapping findByUserId(Long userId);
}
