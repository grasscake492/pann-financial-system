package org.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "学号不能为空")
    @JsonProperty("student_number")
    private String studentNumber;

    @NotBlank(message = "密码不能为空")
    private String password;

    // Getter和Setter方法
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}