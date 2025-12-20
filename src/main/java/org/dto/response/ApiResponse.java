// src/main/java/org/dto/response/ApiResponse.java
package org.dto.response;

import org.constant.ResponseCode;

public class ApiResponse<T> {
    private String resCode;
    private String resMsg;
    private T data;

    // 构造方法
    public ApiResponse() {}

    public ApiResponse(String resCode, String resMsg, T data) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.data = data;
    }

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), "操作成功", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message, null);
    }

    // 错误响应
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }

    // 新增：接受ResponseCode和自定义消息的方法
    public static <T> ApiResponse<T> error(ResponseCode code, String customMessage) {
        return new ApiResponse<>(code.getCode(), customMessage, null);
    }

    // 判断响应是否成功
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode().equals(this.resCode);
    }

    // Getters and Setters
    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "resCode='" + resCode + '\'' +
                ", resMsg='" + resMsg + '\'' +
                ", data=" + data +
                '}';
    }
}