// src/main/java/org/dto/request/ProxyAddRequest.java
package org.dto.request;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 添加代领记录请求DTO
 */
public class ProxyAddRequest {

    @NotNull(message = "稿费记录ID不能为空")
    private Long feeRecordId;

    @NotNull(message = "部门ID不能为空")
    private Long departmentId;

    @NotNull(message = "原始用户ID不能为空")
    private Long originalUserId;

    @NotNull(message = "代领用户ID不能为空")
    private Long proxyUserId;

    @NotNull(message = "代领月份不能为空")
    private String proxyMonth;

    @NotNull(message = "稿费金额不能为空")
    private BigDecimal feeAmount;

    private String description;

    // Getters and Setters
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