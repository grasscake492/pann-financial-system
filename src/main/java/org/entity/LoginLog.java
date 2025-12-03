package org.entity;

import java.util.Date;

/**
 * 登录日志表实体类
 * 对应数据库表: login_logs
 * 记录用户登录日志信息
 */
public class LoginLog {
    private Long logId;            // 日志ID，主键，自增
    private Long userId;           // 用户ID
    private String ipAddress;      // IP地址
    private Date loginAt;          // 登录时间
    private String status;         // 登录状态

    public LoginLog() {}

    // Getter和Setter方法
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Date getLoginAt() { return loginAt; }
    public void setLoginAt(Date loginAt) { this.loginAt = loginAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
