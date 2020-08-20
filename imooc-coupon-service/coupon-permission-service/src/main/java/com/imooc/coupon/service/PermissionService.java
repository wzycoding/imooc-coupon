package com.imooc.coupon.service;

import com.imooc.coupon.constant.RoleEnum;
import com.imooc.coupon.dao.PathRepository;
import com.imooc.coupon.dao.RolePathMappingRepository;
import com.imooc.coupon.dao.RoleRepository;
import com.imooc.coupon.dao.UserRoleMappingRepository;
import com.imooc.coupon.entity.Path;
import com.imooc.coupon.entity.Role;
import com.imooc.coupon.entity.RolePathMapping;
import com.imooc.coupon.entity.UserRoleMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 权限服务
 *
 * @author wzy
 * @date 2020-08-20 23:21
 **/
@Slf4j
@Service
public class PermissionService {

    private final PathRepository pathRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RolePathMappingRepository rolePathMappingRepository;


    public PermissionService(PathRepository pathRepository, RoleRepository roleRepository, UserRoleMappingRepository userRoleMappingRepository, RolePathMappingRepository rolePathMappingRepository) {
        this.pathRepository = pathRepository;
        this.roleRepository = roleRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.rolePathMappingRepository = rolePathMappingRepository;
    }

    /**
     * 做权限校验
     *
     * @param userId     用户id
     * @param uri        uri
     * @param httpMethod 请求方法
     * @return 权限校验是否成功
     */
    public Boolean checkPermission(Long userId, String uri, String httpMethod) {
        // 获取用户相关的角色,要求每个用户都要有角色
        UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(userId);
        // 如果用户角色映射表找不到记录，直接返回false
        if (null == userRoleMapping) {
            log.error("userId not exist is UserRoleMapping: {}", userId);
            return false;
        }
        Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
        // 如果找不到对应的Role记录，直接返回false
        if (Boolean.FALSE == role.isPresent()) {
            log.error("roleId not exist in Role: {}",
                    userRoleMapping.getRoleId());
            return false;
        }
        // 如果是超级管理员，则返回true
        if (role.get().getRoleTag().equals(RoleEnum.SUPER_ADMIN.name())) {
            return true;
        }

        Path path = pathRepository.findByPathPatternAndHttpMethod(uri, httpMethod);

        // 如果路径没有注册, 说明这个路径不需要做权限控制
        if (path == null) {
            return true;
        }

        RolePathMapping rolePathMapping = rolePathMappingRepository.findByRoleIdAndPathId(
                role.get().getId(), path.getId()
        );
        return rolePathMapping != null;
    }

}
