package org.service;

import org.entity.User;

import java.util.List;

/**
 * 管理员用户管理业务接口
 * 仅管理员可调用
 */
public interface AdminUserService {

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 分页查询用户
     */
    List<User> getUsersByPage(int page, int size);

    /**
     * 根据用户ID查询用户
     */
    User getUserById(Long userId);

    /**
     * 创建新用户
     */
    boolean createUser(User user);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);

    /**
     * 管理员重置用户密码
     */
    boolean resetPassword(Long userId, String newPassword);
}
