// 创建文件：StatisticalSummaryResponse.java
package org.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 统计汇总响应
 */
public class StatisticalSummaryResponse {
    private Integer totalRecords;            // 总记录数
    private BigDecimal totalFee;            // 总稿费金额
    private Integer departmentCount;        // 涉及部门数
    private Integer userCount;              // 涉及用户数
    private String earliestMonth;           // 最早月份
    private String latestMonth;             // 最近月份
    private Date generatedAt;               // 生成时间
    private List<DepartmentSummary> departmentSummaries; // 各部门汇总

    // 部门汇总内部类
    public static class DepartmentSummary {
        private Long departmentId;
        private String departmentName;
        private Integer recordCount;
        private BigDecimal totalFee;
        private String lastUpdatedMonth;

        // getters and setters
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

        public String getLastUpdatedMonth() {
            return lastUpdatedMonth;
        }

        public void setLastUpdatedMonth(String lastUpdatedMonth) {
            this.lastUpdatedMonth = lastUpdatedMonth;
        }
    }

    // getters and setters
    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(Integer departmentCount) {
        this.departmentCount = departmentCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public String getEarliestMonth() {
        return earliestMonth;
    }

    public void setEarliestMonth(String earliestMonth) {
        this.earliestMonth = earliestMonth;
    }

    public String getLatestMonth() {
        return latestMonth;
    }

    public void setLatestMonth(String latestMonth) {
        this.latestMonth = latestMonth;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<DepartmentSummary> getDepartmentSummaries() {
        return departmentSummaries;
    }

    public void setDepartmentSummaries(List<DepartmentSummary> departmentSummaries) {
        this.departmentSummaries = departmentSummaries;
    }

    @Override
    public String toString() {
        return "StatisticalSummaryResponse{" +
                "totalRecords=" + totalRecords +
                ", totalFee=" + totalFee +
                ", departmentCount=" + departmentCount +
                ", userCount=" + userCount +
                ", earliestMonth='" + earliestMonth + '\'' +
                ", latestMonth='" + latestMonth + '\'' +
                ", generatedAt=" + generatedAt +
                ", departmentSummaries=" + departmentSummaries +
                '}';
    }
}