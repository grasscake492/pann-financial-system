package org.dao;

import org.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户表数据访问接口
 * 对应数据库表: users
 * 提供用户数据的CRUD操作
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User selectByUserId(Long userId);

    /**
     * 根据学号查询用户
     * @param studentNumber 学号
     * @return 用户对象
     */
    User selectByStudentNumber(String studentNumber);


    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    User selectByEmail(String email);

    /**
     * 根据真实姓名查询用户
     * @param realName 真实姓名
     * @return 用户列表
     */
    List<User> selectByUserName(String realName);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> selectAllUsers();

    /**
     * 分页查询用户
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 用户列表
     */
    List<User> selectUsersByPage(int offset, int limit);

    /**
     * 统计用户总数
     * @return 用户总数
     */
    int countUsers();

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int insertUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int updateUser(User user);

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 影响的行数
     */
    int updatePassword(Long userId, String newPassword);

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Long userId);

    /**
     * 根据关键词搜索用户
     * @param keyword 关键词(学号或姓名)
     * @return 用户列表
     */
    List<User> searchUsers(String keyword);
}
