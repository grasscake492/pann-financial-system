package org.dto.request;

import javax.validation.constraints.NotNull;

/**
 * 更新用户角色请求
 * 仅管理员可调用
 */
public class UpdateUserRoleRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    // Getter / Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
