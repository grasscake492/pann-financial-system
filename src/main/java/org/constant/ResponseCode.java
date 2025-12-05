package org.constant;

public class ResponseCode {
    // 成功
    public static final String SUCCESS = "0000";

    // 通用错误
    public static final String FAILED = "0001";
    public static final String PARAM_ERROR = "0002";
    public static final String PERMISSION_DENIED = "0003";
    public static final String SYSTEM_ERROR = "0008";

    // 认证相关
    public static final String LOGIN_FAILED = "0004";
    public static final String EMAIL_EXISTS = "0005";
    public static final String STUDENT_NUMBER_EXISTS = "0006";
    public static final String USER_NOT_FOUND = "0009";
    public static final String PASSWORD_ERROR = "0010";
    public static final String TOKEN_INVALID = "0011";
}