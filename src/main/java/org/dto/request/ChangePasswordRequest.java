package org.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangePasswordRequest {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$",
            message = "新密码必须包含字母和数字，长度6-20位")
    private String newPassword;

    // Getter和Setter方法
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
