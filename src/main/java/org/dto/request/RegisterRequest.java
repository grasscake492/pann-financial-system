package org.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "\\d{8,12}", message = "学号格式不正确")
    private String studentNumber;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$",
            message = "密码必须包含字母和数字，长度6-20位")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // Getter和Setter方法
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}