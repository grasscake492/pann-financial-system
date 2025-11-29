package org.eneity;

/**
 * 部门表实体类
 * 对应数据库表: departments
 */
public class Department {
    private Long departmentId;     // 部门ID
    private String departmentName; // 部门名称

    // 构造方法
    public Department() {}

    // Getter和Setter方法
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
