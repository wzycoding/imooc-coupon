package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Role Dao
 *
 * @author wzy
 * @date 2020-08-17 0:26
 **/
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
