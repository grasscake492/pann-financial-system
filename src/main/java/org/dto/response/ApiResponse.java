package org.dto.response;

public class ApiResponse<T> {
    private String resCode;
    private String resMsg;
    private T data;

    // 构造函数
    public ApiResponse() {}

    public ApiResponse(String resCode, String resMsg, T data) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.data = data;
    }

    // 成功响应静态方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0000", "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("0000", message, data);
    }

    // 错误响应静态方法
    public static <T> ApiResponse<T> error(String resCode, String resMsg) {
        return new ApiResponse<>(resCode, resMsg, null);
    }

    // Getter和Setter方法
    public String getResCode() { return resCode; }
    public void setResCode(String resCode) { this.resCode = resCode; }

    public String getResMsg() { return resMsg; }
    public void setResMsg(String resMsg) { this.resMsg = resMsg; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}