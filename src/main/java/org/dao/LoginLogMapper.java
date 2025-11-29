package org.dao;

import org.eneity.LoginLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录日志表数据访问接口
 * 对应数据库表: login_logs
 * 提供登录日志数据的CRUD操作
 */
@Repository
public interface LoginLogMapper {

    /**
     * 根据日志ID查询
     * @param logId 日志ID
     * @return 登录日志
     */
    LoginLog selectByLogId(Long logId);

    /**
     * 根据用户ID查询登录日志
     * @param userId 用户ID
     * @return 登录日志列表
     */
    List<LoginLog> selectByUserId(Long userId);

    /**
     * 查询所有登录日志
     * @return 登录日志列表
     */
    List<LoginLog> selectAllLoginLogs();

    /**
     * 查询最近登录日志
     * @param limit 数量限制
     * @return 登录日志列表
     */
    List<LoginLog> selectRecentLogs(int limit);

    /**
     * 分页查询登录日志
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 登录日志列表
     */
    List<LoginLog> selectByPage(int offset, int limit);

    /**
     * 统计登录日志数量
     * @return 日志总数
     */
    int countLoginLogs();

    /**
     * 插入登录日志
     * @param loginLog 登录日志对象
     * @return 影响的行数
     */
    int insertLoginLog(LoginLog loginLog);

    /**
     * 删除登录日志
     * @param logId 日志ID
     * @return 影响的行数
     */
    int deleteByLogId(Long logId);

    /**
     * 根据用户ID删除登录日志
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Long userId);

    /**
     * 清理过期登录日志
     * @param days 保留天数
     * @return 影响的行数
     */
    int cleanExpiredLogs(int days);
}
