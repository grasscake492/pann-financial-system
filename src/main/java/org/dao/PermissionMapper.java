package org.dao;

import org.entity.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限表数据访问接口
 * 对应数据库表: permissions
 * 提供权限数据的CRUD操作
 */
@Repository
public interface PermissionMapper {

    /**
     * 根据权限ID查询
     * @param permissionId 权限ID
     * @return 权限对象
     */
    Permission selectByPermissionId(Long permissionId);

    /**
     * 根据权限代码查询
     * @param permissionCode 权限代码
     * @return 权限对象
     */
    Permission selectByPermissionCode(String permissionCode);

    /**
     * 查询所有权限
     * @return 权限列表
     */
    List<Permission> selectAllPermissions();

    /**
     * 插入权限
     * @param permission 权限对象
     * @return 影响的行数
     */
    int insertPermission(Permission permission);

    /**
     * 更新权限
     * @param permission 权限对象
     * @return 影响的行数
     */
    int updatePermission(Permission permission);

    /**
     * 删除权限
     * @param permissionId 权限ID
     * @return 影响的行数
     */
    int deleteByPermissionId(Long permissionId);
}