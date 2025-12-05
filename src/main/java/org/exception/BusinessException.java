package org.exception;

public class BusinessException extends RuntimeException {
    private String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = "0001";
    }

    public String getCode() {
        return code;
    }
}