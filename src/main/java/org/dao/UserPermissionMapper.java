package org.dao;

import org.eneity.UserPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户权限关联表数据访问接口
 * 对应数据库表: user_permissions
 * 提供用户权限关联数据的CRUD操作
 */
@Repository
public interface UserPermissionMapper {

    /**
     * 根据关联ID查询
     * @param id 关联ID
     * @return 用户权限关联对象
     */
    UserPermission selectById(Long id);

    /**
     * 根据用户ID查询权限关联
     * @param userId 用户ID
     * @return 用户权限关联列表
     */
    List<UserPermission> selectByUserId(Long userId);

    /**
     * 根据权限ID查询权限关联
     * @param permissionId 权限ID
     * @return 用户权限关联列表
     */
    List<UserPermission> selectByPermissionId(Long permissionId);

    /**
     * 查询用户的所有权限ID
     * @param userId 用户ID
     * @return 权限ID列表
     */
    List<Long> selectPermissionIdsByUserId(Long userId);

    /**
     * 插入用户权限关联
     * @param userPermission 用户权限关联对象
     * @return 影响的行数
     */
    int insertUserPermission(UserPermission userPermission);

    /**
     * 删除用户权限关联
     * @param id 关联ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID和权限ID删除关联
     * @param userId 用户ID
     * @param permissionId 权限ID
     * @return 影响的行数
     */
    int deleteByUserAndPermission(Long userId, Long permissionId);

    /**
     * 根据用户ID删除所有权限关联
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Long userId);

    /**
     * 检查用户是否拥有某个权限
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否存在
     */
    boolean checkUserPermission(Long userId, String permissionCode);
}
