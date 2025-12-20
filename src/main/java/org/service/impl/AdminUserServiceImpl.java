package org.service.impl;

import org.dao.UserMapper;
import org.entity.User;
import org.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员用户管理业务实现
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public List<User> getUsersByPage(int page, int size) {
        int offset = (page - 1) * size;
        return userMapper.selectUsersByPage(offset, size);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectByUserId(userId);
    }

    @Override
    public boolean createUser(User user) {
        // 管理员创建用户时加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        User existing = userMapper.selectByUserId(user.getUserId());
        if (existing == null) {
            return false;
        }
        // 如果未传密码，保留原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Long userId) {
        return userMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        String encoded = passwordEncoder.encode(newPassword);
        return userMapper.updatePassword(userId, encoded) > 0;
    }
}
