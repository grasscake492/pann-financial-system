// src/main/java/org/dto/request/RoyaltyUpdateRequest.java
package org.dto.request;

import java.math.BigDecimal;

public class RoyaltyUpdateRequest {
    private String articleTitle;
    private String articleType;
    private BigDecimal feeAmount;
    private String statisticalMonth;
    private String description;
    private Long departmentId;

    // 构造方法
    public RoyaltyUpdateRequest() {}

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "RoyaltyUpdateRequest{" +
                "articleTitle='" + articleTitle + '\'' +
                ", articleType='" + articleType + '\'' +
                ", feeAmount=" + feeAmount +
                ", statisticalMonth='" + statisticalMonth + '\'' +
                ", description='" + description + '\'' +
                ", departmentId=" + departmentId +
                '}';
    }
}