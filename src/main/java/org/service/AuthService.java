package org.service;

import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.dto.request.ChangePasswordRequest;
import org.dto.response.LoginResponse;
import org.dto.response.ApiResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    /**
     * 用户注册
     */
    ApiResponse<?> register(RegisterRequest registerRequest);

    /**
     * 用户登录
     */
    ApiResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletRequest request);

    /**
     * 修改密码
     */
    ApiResponse<?> changePassword(ChangePasswordRequest changePasswordRequest, Long userId);

    /**
     * 退出登录
     */
    ApiResponse<?> logout(HttpServletRequest request);

    /**
     * 验证token
     */
    boolean validateToken(String token, Long userId);
}