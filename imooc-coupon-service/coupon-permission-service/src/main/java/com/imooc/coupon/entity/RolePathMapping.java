package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 描述：角色路径映射实体类
 *
 * @author wzy
 * @version V1.0
 * @date 2020/7/17 14:33
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_role_path_mapping")
public class RolePathMapping {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Role表的主键
     */
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    /**
     * Path表的主键
     */
    @Basic
    @Column(name = "path_id", nullable = false)
    private Integer pathId;


}
