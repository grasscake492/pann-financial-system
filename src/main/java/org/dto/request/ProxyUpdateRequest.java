// src/main/java/org/dto/request/ProxyUpdateRequest.java
package org.dto.request;

import java.math.BigDecimal;

/**
 * 修改代领记录请求DTO
 */
public class ProxyUpdateRequest {

    private Long departmentId;
    private Long originalUserId;
    private Long proxyUserId;
    private String proxyMonth;
    private Long feeRecordId;
    private String articleTitle;
    private BigDecimal feeAmount;
    private String description;

    // Getters and Setters
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getOriginalUserId() {
        return originalUserId;
    }

    public void setOriginalUserId(Long originalUserId) {
        this.originalUserId = originalUserId;
    }

    public Long getProxyUserId() {
        return proxyUserId;
    }

    public void setProxyUserId(Long proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    public String getProxyMonth() {
        return proxyMonth;
    }

    public void setProxyMonth(String proxyMonth) {
        this.proxyMonth = proxyMonth;
    }

    public Long getFeeRecordId() {
        return feeRecordId;
    }

    public void setFeeRecordId(Long feeRecordId) {
        this.feeRecordId = feeRecordId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}