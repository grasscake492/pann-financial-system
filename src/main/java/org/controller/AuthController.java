package org.controller;

import org.service.AuthService;
import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.dto.request.ChangePasswordRequest;
import org.dto.response.ApiResponse;
import org.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * POST /auth/register
     */
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    /**
     * 用户登录
     * POST /auth/login
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                HttpServletRequest request) {
        return authService.login(loginRequest, request);
    }

    /**
     * 修改密码
     * PUT /auth/change-password
     */
    @PutMapping("/change-password")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                         HttpServletRequest request) {
        // 从请求中获取用户ID（通过JWT过滤器设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("0003", "用户未登录");
        }

        return authService.changePassword(changePasswordRequest, userId);
    }

    /**
     * 退出登录
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        return authService.logout(request);
    }

    /**
     * 健康检查接口（用于测试）
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("认证服务运行正常", null);
    }
}