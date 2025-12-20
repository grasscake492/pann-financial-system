// 创建文件：TypeDistributionResponse.java
package org.dto.response;

import java.math.BigDecimal;

/**
 * 类型分布响应
 */
public class TypeDistributionResponse {
    private String articleType;        // 稿件类型
    private Integer recordCount;       // 记录数
    private BigDecimal totalFee;       // 总稿费
    private Double percentage;         // 占比（百分比）
    private Long departmentId;         // 部门ID（可选）
    private String departmentName;     // 部门名称（可选）

    // 构造方法
    public TypeDistributionResponse() {}

    public TypeDistributionResponse(String articleType, Integer recordCount,
                                    BigDecimal totalFee, Double percentage) {
        this.articleType = articleType;
        this.recordCount = recordCount;
        this.totalFee = totalFee;
        this.percentage = percentage;
    }

    // getters and setters
    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
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

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

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

    @Override
    public String toString() {
        return "TypeDistributionResponse{" +
                "articleType='" + articleType + '\'' +
                ", recordCount=" + recordCount +
                ", totalFee=" + totalFee +
                ", percentage=" + percentage +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}