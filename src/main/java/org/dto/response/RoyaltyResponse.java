// src/main/java/org/dto/response/RoyaltyResponse.java
package org.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RoyaltyResponse {
    private Long recordId;
    private List<Long> userIds;
    private List<String> realNames;
    private List<String> studentNumbers;
    private String articleTitle;
    private String articleType;
    private BigDecimal feeAmount;
    private String statisticalMonth;
    private Long departmentId;
    private Date createdAt;
    private Date updatedAt;

    // 构造方法
    public RoyaltyResponse() {}

    // Getters and Setters
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<String> getRealNames() {
        return realNames;
    }

    public void setRealNames(List<String> realNames) {
        this.realNames = realNames;
    }

    public List<String> getStudentNumbers() {
        return studentNumbers;
    }

    public void setStudentNumbers(List<String> studentNumbers) {
        this.studentNumbers = studentNumbers;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getStatisticalMonth() {
        return statisticalMonth;
    }

    public void setStatisticalMonth(String statisticalMonth) {
        this.statisticalMonth = statisticalMonth;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RoyaltyResponse{" +
                "recordId=" + recordId +
                ", userIds=" + userIds +
                ", realNames=" + realNames +
                ", studentNumbers=" + studentNumbers +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleType='" + articleType + '\'' +
                ", feeAmount=" + feeAmount +
                ", statisticalMonth='" + statisticalMonth + '\'' +
                ", departmentId=" + departmentId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}