package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 描述：用户角色实体类
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/17 14:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_role")
public class Role {

    /**
     * 主键
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色名称
     */
    @Basic
    @Column(name = "role_name", nullable = false)
    private String roleName;

    /**
     * 角色标签
     */
    @Basic
    @Column(name = "role_tag", nullable = false)
    private String roleTag;
}
