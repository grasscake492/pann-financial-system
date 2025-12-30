// src/main/java/org/dto/request/ProxyQueryRequest.java
package org.dto.request;


import jakarta.validation.constraints.Min;

/**
 * 查询代领记录请求DTO
 */
public class ProxyQueryRequest {

    private Long departmentId;
    private Long originalUserId;
    private Long proxyUserId;
    private String proxyMonth;

    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    private Integer size = 10;

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
}