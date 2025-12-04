package org.dto.response;

import java.util.List;

public class LoginResponse {
    private Long userId;
    private String studentNumber;
    private String realName;
    private String email;
    private String token;
    private String role;
    private List<String> permissions;

    // 构造函数
    public LoginResponse() {}

    public LoginResponse(Long userId, String studentNumber, String realName,
                         String email, String token, String role, List<String> permissions) {
        this.userId = userId;
        this.studentNumber = studentNumber;
        this.realName = realName;
        this.email = email;
        this.token = token;
        this.role = role;
        this.permissions = permissions;
    }

    // Getter和Setter方法
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}
