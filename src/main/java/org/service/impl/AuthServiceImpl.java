package org.service.impl;

import org.dao.UserMapper;
import org.entity.User;
import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.result.result;
import org.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    // 密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    @Override
    public result login(LoginRequest loginRequest) {
        User user = userMapper.selectByStudentNumber(loginRequest.getStudentNumber());
        if (user == null) {
            return result.error("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return result.error("密码错误");
        }

        // 登录成功，返回用户信息或 token
        return result.success(user);
    }

    /**
     * 用户注册
     */
    @Override
    public result register(RegisterRequest registerRequest) {
        // 检查学号或邮箱是否已存在
        if (userMapper.selectByStudentNumber(registerRequest.getStudentNumber()) != null) {
            return result.error("学号已存在");
        }
        if (userMapper.selectByEmail(registerRequest.getEmail()) != null) {
            return result.error("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setStudentNumber(registerRequest.getStudentNumber());
        user.setRealName(registerRequest.getRealName());
        user.setEmail(registerRequest.getEmail());
        // 密码加密存储
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        int rows = userMapper.insertUser(user);
        if (rows > 0) {
            return result.success("注册成功");
        } else {
            return result.error("注册失败，请重试");
        }
    }

    /**
     * 修改密码
     */
    @Override
    public result changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            return result.error("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return result.error("旧密码错误");
        }

        // 更新新密码
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
        return result.success("密码修改成功");
    }
}
