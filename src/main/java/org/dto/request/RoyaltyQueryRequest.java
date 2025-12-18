// src/main/java/org/dto/request/RoyaltyQueryRequest.java
package org.dto.request;

import java.util.Date;

public class RoyaltyQueryRequest {
    private String startDate;
    private String endDate;
    private Integer page;
    private Integer size;
    private Long userId;
    private Long departmentId;
    private String status;

    // 构造方法
    public RoyaltyQueryRequest() {}

    // Getters and Setters
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoyaltyQueryRequest{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", page=" + page +
                ", size=" + size +
                ", userId=" + userId +
                ", departmentId=" + departmentId +
                ", status='" + status + '\'' +
                '}';
    }
}