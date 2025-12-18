// src/main/java/org/dto/response/ProxyResponse.java
package org.dto.response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代领记录响应DTO
 */
public class ProxyResponse {

    private Long proxyId;
    private Long feeRecordId;
    private Long departmentId;
    private Long originalUserId;
    private String originalUserName;
    private String originalStudentNumber;
    private Long proxyUserId;
    private String proxyUserName;
    private String proxyStudentNumber;
    private String articleTitle;
    private BigDecimal feeAmount;
    private String proxyMonth;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    // Getters and Setters
    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }

    public Long getFeeRecordId() {
        return feeRecordId;
    }

    public void setFeeRecordId(Long feeRecordId) {
        this.feeRecordId = feeRecordId;
    }

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

    public String getOriginalUserName() {
        return originalUserName;
    }

    public void setOriginalUserName(String originalUserName) {
        this.originalUserName = originalUserName;
    }

    public String getOriginalStudentNumber() {
        return originalStudentNumber;
    }

    public void setOriginalStudentNumber(String originalStudentNumber) {
        this.originalStudentNumber = originalStudentNumber;
    }

    public Long getProxyUserId() {
        return proxyUserId;
    }

    public void setProxyUserId(Long proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getProxyStudentNumber() {
        return proxyStudentNumber;
    }

    public void setProxyStudentNumber(String proxyStudentNumber) {
        this.proxyStudentNumber = proxyStudentNumber;
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

    public String getProxyMonth() {
        return proxyMonth;
    }

    public void setProxyMonth(String proxyMonth) {
        this.proxyMonth = proxyMonth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}