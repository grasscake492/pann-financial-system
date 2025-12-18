package org.constant;

public class ResponseCode {
    // 成功
    public static final ResponseCode SUCCESS = new ResponseCode("0000", "成功");

    // 通用错误
    public static final ResponseCode FAILED = new ResponseCode("0001", "操作失败");
    public static final ResponseCode PARAM_ERROR = new ResponseCode("0002", "参数错误");
    public static final ResponseCode PERMISSION_DENIED = new ResponseCode("0003", "权限不足");
    public static final ResponseCode SYSTEM_ERROR = new ResponseCode("0008", "系统错误");

    // 认证相关
    public static final ResponseCode LOGIN_FAILED = new ResponseCode("0004", "登录失败");
    public static final ResponseCode EMAIL_EXISTS = new ResponseCode("0005", "邮箱已存在");
    public static final ResponseCode STUDENT_NUMBER_EXISTS = new ResponseCode("0006", "学号已存在");
    public static final ResponseCode USER_NOT_FOUND = new ResponseCode("0009", "用户不存在");
    public static final ResponseCode PASSWORD_ERROR = new ResponseCode("0010", "密码错误");
    public static final ResponseCode TOKEN_INVALID = new ResponseCode("0011", "令牌无效");
    public static final ResponseCode UNAUTHORIZED = new ResponseCode("0013", "未授权访问");

    // 数据相关
    public static final ResponseCode DATA_NOT_FOUND = new ResponseCode("0012", "数据不存在");

    // 系统内部错误（用于控制器中的INTERNAL_ERROR）
    public static final ResponseCode INTERNAL_ERROR = SYSTEM_ERROR; // 复用SYSTEM_ERROR

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseCode{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}