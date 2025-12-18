// src/main/java/org/dto/response/RoyaltyListResponse.java
package org.dto.response;

import java.util.List;

public class RoyaltyListResponse {
    private Integer total;
    private List<RoyaltyResponse> list;

    // 构造方法
    public RoyaltyListResponse() {}

    public RoyaltyListResponse(Integer total, List<RoyaltyResponse> list) {
        this.total = total;
        this.list = list;
    }

    // Getters and Setters
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<RoyaltyResponse> getList() {
        return list;
    }

    public void setList(List<RoyaltyResponse> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "RoyaltyListResponse{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}