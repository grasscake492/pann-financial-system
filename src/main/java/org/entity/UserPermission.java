package org.entity;

import java.util.Date;

/**
 * 用户权限关联表实体类
 * 对应数据库表: user_permissions
 * 存储用户与权限的关联关系
 */
public class UserPermission {
    private Long id;               // 关联ID，主键，自增
    private Long userId;           // 用户ID
    private Long permissionId;     // 权限ID
    private Date createdAt;        // 创建时间

    public UserPermission() {}

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
