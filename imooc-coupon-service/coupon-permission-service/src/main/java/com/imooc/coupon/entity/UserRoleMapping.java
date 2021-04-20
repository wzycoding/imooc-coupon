package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 描述：用户与角色映射关系实体类
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/17 23:22
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_user_role_mapping")
public class UserRoleMapping {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 角色id
     */
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    /**
     * 用户id
     */
    @Basic
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
