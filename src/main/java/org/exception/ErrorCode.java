package org.exception;

import lombok.Getter;

/**
 * 错误码枚举类
 * 统一管理所有业务异常码
 *
 * @author PANN开发团队
 * @version 1.0
 */
@Getter
public enum ErrorCode {

    // ==================== 成功码 ====================
    SUCCESS(200, "成功"),

    // ==================== 系统级错误码 (1000-1999) ====================
    SYSTEM_ERROR(1000, "系统异常"),
    DATABASE_ERROR(1001, "数据库操作异常"),
    NETWORK_ERROR(1002, "网络异常"),
    FILE_OPERATION_ERROR(1003, "文件操作异常"),
    EXTERNAL_SERVICE_ERROR(1004, "外部服务异常"),
    CONFIGURATION_ERROR(1005, "系统配置错误"),
    ILLEGAL_OPERATION(1006, "非法操作"),

    // ==================== 参数级错误码 (2000-2999) ====================
    PARAM_ERROR(2000, "参数错误"),
    PARAM_REQUIRED(2001, "参数缺失"),
    PARAM_FORMAT_ERROR(2002, "参数格式错误"),
    PARAM_INVALID(2003, "参数无效"),
    PARAM_LENGTH_ERROR(2004, "参数长度错误"),
    PARAM_RANGE_ERROR(2005, "参数范围错误"),
    JSON_PARSE_ERROR(2006, "JSON解析错误"),
    DATE_FORMAT_ERROR(2007, "日期格式错误"),
    AMOUNT_FORMAT_ERROR(2008, "金额格式错误"),

    // ==================== 权限级错误码 (3000-3999) ====================
    PERMISSION_DENIED(3000, "权限不足"),
    NOT_LOGIN(3001, "用户未登录"),
    TOKEN_INVALID(3002, "令牌无效"),
    TOKEN_EXPIRED(3003, "令牌已过期"),
    TOKEN_MISSING(3004, "令牌缺失"),
    ACCESS_DENIED(3005, "访问被拒绝"),
    OPERATION_FORBIDDEN(3006, "操作被禁止"),
    ADMIN_REQUIRED(3007, "需要管理员权限"),
    OPERATION_ADMIN_REQUIRED(3008, "需要运营部管理员权限"),

    // ==================== 用户级错误码 (4000-4999) ====================
    USER_NOT_FOUND(4000, "用户不存在"),
    USER_DISABLED(4001, "用户已禁用"),
    STUDENT_NUMBER_EXISTS(4002, "学号已存在"),
    EMAIL_EXISTS(4003, "邮箱已存在"),
    PHONE_EXISTS(4004, "手机号已存在"),
    USER_INFO_INCOMPLETE(4005, "用户信息不完整"),
    USER_VERIFY_FAILED(4006, "用户验证失败"),

    // ==================== 认证级错误码 (5000-5099) ====================
    LOGIN_FAILED(5000, "登录失败"),
    PASSWORD_ERROR(5001, "密码错误"),
    ACCOUNT_LOCKED(5002, "账号已锁定"),
    ACCOUNT_EXPIRED(5003, "账号已过期"),
    CREDENTIALS_EXPIRED(5004, "凭证已过期"),
    CAPTCHA_ERROR(5005, "验证码错误"),
    AUTHENTICATION_FAILED(5006, "认证失败"),
    SESSION_EXPIRED(5007, "会话已过期"),

    // ==================== 业务级错误码 (6000-6999) ====================
    // 稿费相关错误码 (6000-6049)
    ROYALTY_RECORD_NOT_FOUND(6000, "稿费记录不存在"),
    ROYALTY_RECORD_EXISTS(6001, "稿费记录已存在"),
    ROYALTY_AMOUNT_ERROR(6002, "稿费金额错误"),
    ROYALTY_MONTH_ERROR(6003, "稿费月份错误"),
    ROYALTY_DEPARTMENT_ERROR(6004, "稿费部门错误"),
    ROYALTY_EXPORT_ERROR(6005, "稿费导出失败"),
    ROYALTY_CALCULATION_ERROR(6006, "稿费计算错误"),

    // 代领相关错误码 (6050-6099)
    PROXY_RECORD_NOT_FOUND(6050, "代领记录不存在"),
    PROXY_RULE_VIOLATION(6051, "违反代领规则"),
    PROXY_SELF_FORBIDDEN(6052, "不能自己代领自己"),
    PROXY_USER_NOT_FOUND(6053, "代领用户不存在"),
    PROXY_MONTH_ERROR(6054, "代领月份错误"),
    PROXY_AMOUNT_EXCEEDED(6055, "代领金额超限"),
    PROXY_CANNOT_REVOKE(6056, "代领记录无法撤销"),

    // 部门相关错误码 (6100-6149)
    DEPARTMENT_NOT_FOUND(6100, "部门不存在"),
    DEPARTMENT_DISABLED(6101, "部门已禁用"),
    DEPARTMENT_ADMIN_NOT_FOUND(6102, "部门管理员不存在"),
    DEPARTMENT_ADMIN_EXISTS(6103, "部门管理员已存在"),

    // 统计相关错误码 (6150-6199)
    STATISTICS_ERROR(6150, "统计信息错误"),
    STATISTICS_MONTH_ERROR(6151, "统计月份错误"),
    STATISTICS_DEPARTMENT_ERROR(6152, "统计部门错误"),

    // 文件相关错误码 (6200-6249)
    FILE_UPLOAD_ERROR(6200, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(6201, "文件下载失败"),
    FILE_FORMAT_ERROR(6202, "文件格式错误"),
    FILE_SIZE_EXCEEDED(6203, "文件大小超限"),
    FILE_NOT_FOUND(6204, "文件不存在"),
    EXCEL_EXPORT_ERROR(6205, "Excel导出失败"),
    PDF_EXPORT_ERROR(6206, "PDF导出失败"),

    // ==================== 数据级错误码 (7000-7999) ====================
    DATA_NOT_FOUND(7000, "数据不存在"),
    DATA_EXISTS(7001, "数据已存在"),
    DATA_DUPLICATE(7002, "数据重复"),
    DATA_INTEGRITY_ERROR(7003, "数据完整性错误"),
    DATA_VALIDATION_ERROR(7004, "数据验证错误"),
    DATA_ACCESS_ERROR(7005, "数据访问错误"),
    DATA_CONSISTENCY_ERROR(7006, "数据一致性错误"),

    // ==================== 业务规则错误码 (8000-8999) ====================
    WORK_STUDENT_CANNOT_RECEIVE(8000, "勤工助学学生不能领取稿费"),
    MONTHLY_LIMIT_EXCEEDED(8001, "月度稿费限额超限"),
    PROXY_REQUIRED(8002, "需要代领"),
    PROXY_NOT_ALLOWED(8003, "不允许代领"),
    DEPT_ADMIN_ONLY(8004, "仅限部门管理员操作"),
    SUPER_ADMIN_ONLY(8005, "仅限超级管理员操作"),

    // ==================== 工作流错误码 (9000-9099) ====================
    WORKFLOW_ERROR(9000, "工作流错误"),
    APPROVAL_REQUIRED(9001, "需要审批"),
    APPROVAL_DENIED(9002, "审批被拒绝"),
    WORKFLOW_STATUS_ERROR(9003, "工作流状态错误"),

    // ==================== 其他错误码 (9500-9999) ====================
    RATE_LIMIT_EXCEEDED(9500, "请求频率超限"),
    API_CALL_LIMIT(9501, "API调用次数超限"),
    SERVICE_UNAVAILABLE(9502, "服务不可用"),
    SERVICE_BUSY(9503, "服务繁忙"),
    REQUEST_TIMEOUT(9504, "请求超时"),
    RESOURCE_EXHAUSTED(9505, "资源耗尽"),
    CONCURRENT_ACCESS_ERROR(9506, "并发访问错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误描述
     */
    private final String message;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误描述
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取对应的ErrorCode枚举
     * @param code 错误码
     * @return 对应的ErrorCode枚举，如果不存在则返回null
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }

    /**
     * 根据错误码获取错误描述
     * @param code 错误码
     * @return 错误描述，如果不存在则返回"未知错误"
     */
    public static String getMessageByCode(int code) {
        ErrorCode errorCode = getByCode(code);
        return errorCode != null ? errorCode.getMessage() : "未知错误";
    }

    /**
     * 检查错误码是否存在
     * @param code 错误码
     * @return 是否存在
     */
    public static boolean contains(int code) {
        return getByCode(code) != null;
    }

    /**
     * 检查是否为成功码
     * @return 是否为成功码
     */
    public boolean isSuccess() {
        return this.code == SUCCESS.code;
    }

    /**
     * 检查是否为系统级错误
     * @return 是否为系统级错误
     */
    public boolean isSystemError() {
        return this.code >= 1000 && this.code < 2000;
    }

    /**
     * 检查是否为参数级错误
     * @return 是否为参数级错误
     */
    public boolean isParamError() {
        return this.code >= 2000 && this.code < 3000;
    }

    /**
     * 检查是否为权限级错误
     * @return 是否为权限级错误
     */
    public boolean isPermissionError() {
        return this.code >= 3000 && this.code < 4000;
    }

    /**
     * 检查是否为业务级错误
     * @return 是否为业务级错误
     */
    public boolean isBusinessError() {
        return this.code >= 6000 && this.code < 7000;
    }
}