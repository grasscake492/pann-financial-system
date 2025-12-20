// 创建文件：DepartmentMonthlySummaryResponse.java
package org.dto.response;

import java.math.BigDecimal;

/**
 * 部门月度汇总响应
 */
public class DepartmentMonthlySummaryResponse {
    private Long departmentId;        // 部门ID
    private String departmentName;    // 部门名称
    private Integer recordCount;      // 记录数
    private BigDecimal totalFee;      // 总稿费金额
    private String statisticalMonth;  // 统计月份

    // 构造方法
    public DepartmentMonthlySummaryResponse() {}

    public DepartmentMonthlySummaryResponse(Long departmentId, String departmentName,
                                            Integer recordCount, BigDecimal totalFee,
                                            String statisticalMonth) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.recordCount = recordCount;
        this.totalFee = totalFee;
        this.statisticalMonth = statisticalMonth;
    }

    // Getters and Setters
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getStatisticalMonth() {
        return statisticalMonth;
    }

    public void setStatisticalMonth(String statisticalMonth) {
        this.statisticalMonth = statisticalMonth;
    }

    @Override
    public String toString() {
        return "DepartmentMonthlySummaryResponse{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", recordCount=" + recordCount +
                ", totalFee=" + totalFee +
                ", statisticalMonth='" + statisticalMonth + '\'' +
                '}';
    }
}