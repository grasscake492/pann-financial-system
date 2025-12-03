package org.service.impl;

import org.entity.User;
import org.service.UserService;
import org.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectByUserId(userId);
    }

    @Override
    public User getUserByStudentNumber(String studentNumber) {
        return userMapper.selectByStudentNumber(studentNumber);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

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
    public User login(String studentNumber, String password) {
        User user = userMapper.selectByStudentNumber(studentNumber);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        return userMapper.updatePassword(userId, encodedPassword) > 0;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectByUserId(userId);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            return userMapper.updatePassword(userId, encodedPassword) > 0;
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        // 保留原密码，不覆盖
        User existing = userMapper.selectByUserId(user.getUserId());
        if (existing != null) {
            user.setPassword(existing.getPassword());
            return userMapper.updateUser(user) > 0;
        }
        return false;
    }

    @Override
    public boolean createUser(User user) {
        // 创建用户时加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean deleteUser(Long userId) {
        return userMapper.deleteByUserId(userId) > 0;
    }
}
