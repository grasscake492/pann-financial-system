package org.result;

public class result<T> {
    private boolean success;
    private String message;
    private T data;

    public result() {}

    public result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> result<T> success(T data) {
        return new result<>(true, "success", data);
    }

    public static <T> result<T> success(String message) {
        return new result<>(true, message, null);
    }

    public static <T> result<T> error(String message) {
        return new result<>(false, message, null);
    }

    // getter / setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
