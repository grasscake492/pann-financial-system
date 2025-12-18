// src/main/java/org/dto/request/RoyaltyAddRequest.java
package org.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.List;

public class RoyaltyAddRequest {
    private List<Long> userId;
    private List<String> realNames;
    private List<String> studentNumbers;
    private String articleTitle;
    private String articleType;
    private BigDecimal feeAmount;

    @JsonFormat(pattern = "yyyy-MM")
    private String statisticalMonth;

    private Long departmentId;

    // 构造方法
    public RoyaltyAddRequest() {}

    // Getters and Setters
    public List<Long> getUserIds() {
        return userId;
    }

    public void setUserIds(List<Long> userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "RoyaltyAddRequest{" +
                "userIds=" + userId +
                ", realNames=" + realNames +
                ", studentNumbers=" + studentNumbers +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleType='" + articleType + '\'' +
                ", feeAmount=" + feeAmount +
                ", statisticalMonth='" + statisticalMonth + '\'' +
                ", departmentId=" + departmentId +
                '}';
    }
}