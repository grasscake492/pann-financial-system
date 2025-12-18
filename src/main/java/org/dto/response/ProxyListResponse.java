// src/main/java/org/dto/response/ProxyListResponse.java
package org.dto.response;

import java.util.List;

/**
 * 代领记录列表响应DTO
 */
public class ProxyListResponse {

    private Integer total;
    private Integer page;
    private Integer size;
    private List<ProxyResponse> list;

    // Getters and Setters
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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

    public List<ProxyResponse> getList() {
        return list;
    }

    public void setList(List<ProxyResponse> list) {
        this.list = list;
    }

    // 构造方法
    public ProxyListResponse() {}

    public ProxyListResponse(Integer total, Integer page, Integer size, List<ProxyResponse> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.list = list;
    }
}