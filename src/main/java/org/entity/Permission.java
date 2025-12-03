package org.entity;

import java.util.Date;

/**
 * 权限表实体类
 * 对应数据库表: permissions
 * 存储系统权限信息
 */
public class Permission {
    private Long permissionId;     // 权限ID，主键，自增
    private String permissionCode; // 权限代码
    private String permissionName; // 权限名称
    private String description;    // 权限描述
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public Permission() {}

    // Getter和Setter方法
    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }

    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}