package org.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代领记录表实体类
 * 对应数据库表: proxy_receipt_records
 * 存储稿费代领记录信息
 */
public class ProxyReceiptRecords {
    private Long proxyId;              // 代领ID，主键，自增
    private Long feeRecordId;          // 稿费记录ID
    private Long departmentId;         // 部门ID，接收稿费来自的部门ID
    private Long originalUserId;       // 原始用户ID
    private String originalRealName;   // 原始用户真实姓名
    private String originalStudentNumber; // 原始用户学号
    private Long proxyUserId;          // 代领用户ID
    private String proxyRealName;      // 代领用户真实姓名
    private String proxyStudentNumber; // 代领用户学号
    private String articleTitle;       // 稿件标题
    private BigDecimal feeAmount;      // 稿费金额
    private String proxyMonth;         // 代领月份(YYYY-MM格式)
    private Date createdAt;            // 创建时间
    private Date updatedAt;            // 更新时间

    public ProxyReceiptRecords() {}

    // Getter和Setter方法
    public Long getProxyId() { return proxyId; }
    public void setProxyId(Long proxyId) { this.proxyId = proxyId; }

    public Long getFeeRecordId() { return feeRecordId; }
    public void setFeeRecordId(Long feeRecordId) { this.feeRecordId = feeRecordId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getOriginalUserId() { return originalUserId; }
    public void setOriginalUserId(Long originalUserId) { this.originalUserId = originalUserId; }

    public String getOriginalRealName() { return originalRealName; }
    public void setOriginalRealName(String originalRealName) { this.originalRealName = originalRealName; }

    public String getOriginalStudentNumber() { return originalStudentNumber; }
    public void setOriginalStudentNumber(String originalStudentNumber) { this.originalStudentNumber = originalStudentNumber; }

    public Long getProxyUserId() { return proxyUserId; }
    public void setProxyUserId(Long proxyUserId) { this.proxyUserId = proxyUserId; }

    public String getProxyRealName() { return proxyRealName; }
    public void setProxyRealName(String proxyRealName) { this.proxyRealName = proxyRealName; }

    public String getProxyStudentNumber() { return proxyStudentNumber; }
    public void setProxyStudentNumber(String proxyStudentNumber) { this.proxyStudentNumber = proxyStudentNumber; }

    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public String getProxyMonth() { return proxyMonth; }
    public void setProxyMonth(String proxyMonth) { this.proxyMonth = proxyMonth; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}