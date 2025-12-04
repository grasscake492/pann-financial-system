package org.service.impl;

import org.service.AuthService;
import org.dao.UserMapper;
import org.dao.AdminMapper;
import org.dao.LoginLogMapper;
import org.entity.User;
import org.entity.Admin;
import org.entity.LoginLog;
import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.dto.request.ChangePasswordRequest;
import org.dto.response.LoginResponse;
import org.dto.response.ApiResponse;
import org.util.JwtUtil;
import org.util.BCryptUtil;
import org.util.ValidatorUtil;
import org.constant.UserRole;
import org.constant.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptUtil bCryptUtil;

    @Autowired
    private ValidatorUtil validatorUtil;

    @Override
    @Transactional
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        try {
            // 1. 参数验证
            if (!validatorUtil.isValidStudentNumber(registerRequest.getStudentNumber())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "学号格式不正确");
            }

            if (!validatorUtil.isValidRealName(registerRequest.getRealName())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "真实姓名格式不正确");
            }

            if (!validatorUtil.isValidPassword(registerRequest.getPassword())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "密码格式不正确");
            }

            if (!validatorUtil.isValidEmail(registerRequest.getEmail())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "邮箱格式不正确");
            }

            // 2. 检查学号是否已存在
            if (userMapper.countByStudentNumber(registerRequest.getStudentNumber()) > 0) {
                return ApiResponse.error(ResponseCode.STUDENT_NUMBER_EXISTS, "学号已存在");
            }

            // 3. 检查邮箱是否已存在
            if (userMapper.countByEmail(registerRequest.getEmail()) > 0) {
                return ApiResponse.error(ResponseCode.EMAIL_EXISTS, "邮箱已存在");
            }

            // 4. 密码加密
            String encodedPassword = bCryptUtil.encode(registerRequest.getPassword());

            // 5. 创建用户对象
            User user = new User(
                    registerRequest.getStudentNumber(),
                    registerRequest.getRealName(),
                    encodedPassword,
                    registerRequest.getEmail()
            );

            // 6. 保存用户
            int result = userMapper.insert(user);
            if (result > 0) {
                // 返回用户信息（不包含密码）
                User savedUser = userMapper.findByStudentNumber(registerRequest.getStudentNumber());
                LoginResponse response = new LoginResponse();
                response.setUserId(savedUser.getUserId());
                response.setStudentNumber(savedUser.getStudentNumber());
                response.setRealName(savedUser.getRealName());
                response.setEmail(savedUser.getEmail());
                response.setRole(UserRole.USER);

                return ApiResponse.success("注册成功", response);
            } else {
                return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "注册失败，请稍后重试");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "系统异常，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletRequest httpRequest) {
        try {
            // 1. 参数验证
            if (loginRequest.getStudentNumber() == null || loginRequest.getStudentNumber().trim().isEmpty()) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "学号不能为空");
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "密码不能为空");
            }

            // 2. 根据学号查询用户
            User user = userMapper.findByStudentNumber(loginRequest.getStudentNumber());
            if (user == null) {
                // 记录登录失败日志
                recordLoginLog(null, getClientIpAddress(httpRequest), "FAILED_USER_NOT_FOUND");
                return ApiResponse.error(ResponseCode.LOGIN_FAILED, "学号或密码错误");
            }

            // 3. 验证密码
            if (!bCryptUtil.matches(loginRequest.getPassword(), user.getPassword())) {
                // 记录登录失败日志
                recordLoginLog(user.getUserId(), getClientIpAddress(httpRequest), "FAILED_PASSWORD_ERROR");
                return ApiResponse.error(ResponseCode.LOGIN_FAILED, "学号或密码错误");
            }

            // 4. 检查用户角色和权限
            String role = UserRole.USER;
            List<String> permissions = new ArrayList<>();

            // 检查是否是管理员
            Admin admin = adminMapper.findByUserId(user.getUserId());
            if (admin != null) {
                if (admin.getIsSuperAdmin()) {
                    role = UserRole.SUPER_ADMIN;
                    permissions.add("ALL");
                } else {
                    role = UserRole.DEPARTMENT_ADMIN;
                    // 这里可以根据部门分配具体权限
                    permissions.add("FEE_MANAGE");
                    permissions.add("USER_VIEW");
                }
            } else {
                // 普通用户权限
                permissions.add("FEE_VIEW");
                permissions.add("PROFILE_EDIT");
            }

            // 5. 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), user.getStudentNumber(), role);

            // 6. 创建响应对象
            LoginResponse loginResponse = new LoginResponse(
                    user.getUserId(),
                    user.getStudentNumber(),
                    user.getRealName(),
                    user.getEmail(),
                    token,
                    role,
                    permissions
            );

            // 7. 记录登录成功日志
            recordLoginLog(user.getUserId(), getClientIpAddress(httpRequest), "SUCCESS");

            return ApiResponse.success("登录成功", loginResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "系统异常，请稍后重试");
        }
    }

    @Override
    public ApiResponse<?> changePassword(ChangePasswordRequest changePasswordRequest, Long userId) {
        try {
            // 1. 参数验证
            if (!validatorUtil.isValidPassword(changePasswordRequest.getNewPassword())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "新密码格式不正确");
            }

            // 2. 查询用户
            User user = userMapper.findByUserId(userId);
            if (user == null) {
                return ApiResponse.error(ResponseCode.USER_NOT_FOUND, "用户不存在");
            }

            // 3. 验证旧密码
            if (!bCryptUtil.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                return ApiResponse.error(ResponseCode.PASSWORD_ERROR, "旧密码错误");
            }

            // 4. 加密新密码
            String newEncodedPassword = bCryptUtil.encode(changePasswordRequest.getNewPassword());

            // 5. 更新密码
            int result = userMapper.updatePassword(userId, newEncodedPassword);
            if (result > 0) {
                return ApiResponse.success("密码修改成功", null);
            } else {
                return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "密码修改失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "系统异常，请稍后重试");
        }
    }

    @Override
    public ApiResponse<?> logout(HttpServletRequest request) {
        // 在无状态JWT架构中，退出登录主要由客户端删除token
        // 服务端可以记录退出日志或将token加入黑名单（这里简化处理）
        return ApiResponse.success("退出成功", null);
    }

    @Override
    public boolean validateToken(String token, Long userId) {
        return jwtUtil.validateToken(token, userId);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, String ipAddress, String status) {
        try {
            LoginLog loginLog = new LoginLog(userId, ipAddress, status);
            loginLogMapper.insert(loginLog);
        } catch (Exception e) {
            // 登录日志记录失败不影响主要业务
            e.printStackTrace();
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}