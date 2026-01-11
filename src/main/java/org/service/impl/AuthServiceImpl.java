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
     * 用户登录 - 临时兼容明文密码（仅用于测试）
     */
    @Override
    public result login(LoginRequest loginRequest) {
        User user = userMapper.selectByStudentNumber(loginRequest.getStudentNumber());
        if (user == null) {
            return result.error("用户不存在");
        }

        // 添加调试日志
        System.out.println("=== 登录调试信息 ===");
        System.out.println("登录用户学号: " + loginRequest.getStudentNumber());
        System.out.println("输入密码: " + loginRequest.getPassword());
        System.out.println("数据库密码: " + user.getPassword());

        boolean passwordCorrect;

        // 判断数据库中的密码格式
        boolean isBcryptEncoded = user.getPassword() != null &&
                (user.getPassword().startsWith("$2a$") ||
                        user.getPassword().startsWith("$2b$") ||
                        user.getPassword().startsWith("$2y$"));

        System.out.println("密码格式: " + (isBcryptEncoded ? "BCrypt加密" : "明文"));

        if (isBcryptEncoded) {
            // BCrypt格式验证
            passwordCorrect = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        } else {
            // 明文格式验证
            passwordCorrect = loginRequest.getPassword().equals(user.getPassword());

            // 如果密码正确，只是打印信息，不实际更新数据库
            if (passwordCorrect) {
                System.out.println("注意：用户 " + loginRequest.getStudentNumber() + " 的密码是明文存储，建议手动更新为BCrypt加密格式");
                System.out.println("可以执行以下SQL更新密码：");
                System.out.println("UPDATE users SET password = '" + passwordEncoder.encode(loginRequest.getPassword()) + "' WHERE user_id = " + user.getUserId() + ";");
            }
        }

        if (!passwordCorrect) {
            return result.error("密码错误");
        }

        // 登录成功，返回用户信息（不返回密码）
        user.setPassword(null);
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
