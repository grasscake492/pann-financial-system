package org.eneity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 编辑部稿费月表实体类
 * 对应数据库表: editorial_department_monthly
 */
public class EditorialDepartmentMonthly {
    private Long recordId;         // 记录ID
    private String userIds;        // 用户ID数组(JSON格式)
    private String realNames;      // 用户真实姓名数组(JSON格式)
    private String studentNumbers; // 用户学号数组(JSON格式)
    private String articleTitle;   // 稿件标题
    private String articleType;    // 稿件类型
    private BigDecimal feeAmount;  // 稿费发放金额
    private String statisticalMonth; // 统计月份(YYYY-MM)
    private Long departmentId;     // 部门ID
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    // 构造方法
    public EditorialDepartmentMonthly() {}

    // Getter和Setter方法 (与NewsDepartmentMonthly类似)
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getUserIds() { return userIds; }
    public void setUserIds(String userIds) { this.userIds = userIds; }

    public String getRealNames() { return realNames; }
    public void setRealNames(String realNames) { this.realNames = realNames; }

    public String getStudentNumbers() { return studentNumbers; }
    public void setStudentNumbers(String studentNumbers) { this.studentNumbers = studentNumbers; }

    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }

    public String getArticleType() { return articleType; }
    public void setArticleType(String articleType) { this.articleType = articleType; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public String getStatisticalMonth() { return statisticalMonth; }
    public void setStatisticalMonth(String statisticalMonth) { this.statisticalMonth = statisticalMonth; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
