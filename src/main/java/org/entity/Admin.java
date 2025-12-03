package org.entity;

/**
 * 管理员表实体类
 * 对应数据库表: admins
 */
public class Admin {
    private Long adminId;          // 管理员ID
    private Long userId;           // 用户ID
    private String studentNumber;  // 学号
    private String realName;       // 真实姓名
    private String password;       // bcrypt加密密码
    private String email;          // 邮箱
    private Long departmentId;     // 部门ID
    private Boolean isSuperAdmin;  // 是否为最高级管理员

    // 构造方法
    public Admin() {}

    // Getter和Setter方法
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Boolean getIsSuperAdmin() { return isSuperAdmin; }
    public void setIsSuperAdmin(Boolean isSuperAdmin) { this.isSuperAdmin = isSuperAdmin; }
}
