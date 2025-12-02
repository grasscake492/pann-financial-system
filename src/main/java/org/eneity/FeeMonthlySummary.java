package org.eneity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 稿费月汇总表实体类
 * 对应数据库表: fee_monthly_summary
 * 每月一张表，汇总用户月度稿费
 */
public class FeeMonthlySummary {
    private Long summaryId;        // 汇总ID，主键，自增
    private Long userId;           // 用户ID
    private String realName;       // 用户真实姓名
    private String studentNumber;  // 用户学号
    private Long departmentId;     // 部门ID，接收稿费来自的部门id
    private BigDecimal totalAmount; // 月度总金额
    private String statisticalMonth; // 统计月份(YYYY-MM格式)
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public FeeMonthlySummary() {}

    // Getter和Setter方法
    public Long getSummaryId() { return summaryId; }
    public void setSummaryId(Long summaryId) { this.summaryId = summaryId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatisticalMonth() { return statisticalMonth; }
    public void setStatisticalMonth(String statisticalMonth) { this.statisticalMonth = statisticalMonth; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}