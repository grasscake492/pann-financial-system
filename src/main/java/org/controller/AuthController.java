package org.controller;

import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.result.result;
import org.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 负责用户登录 / 注册 / 修改密码 / 退出登录
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * POST /auth/login
     */
    @PostMapping("/login")
    public result login(@RequestBody @Valid LoginRequest loginRequest,
                        BindingResult bindingResult) {

        // 参数校验失败直接返回
        if (bindingResult.hasErrors()) {
            return result.error(bindingResult.getFieldError().getDefaultMessage());
        }

        return authService.login(loginRequest);
    }

    /**
     * 用户注册
     * POST /auth/register
     */
    @PostMapping("/register")
    public result register(@RequestBody @Valid RegisterRequest registerRequest,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return result.error(bindingResult.getFieldError().getDefaultMessage());
        }

        return authService.register(registerRequest);
    }

    /**
     * 修改密码
     * POST /auth/change-password
     */
    @PostMapping("/change-password")
    public result changePassword(@RequestParam Long userId,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {

        return authService.changePassword(userId, oldPassword, newPassword);
    }

    /**
     * 退出登录
     * 前端删除 token 即可
     */
    @PostMapping("/logout")
    public result logout() {
        return result.success("退出成功");
    }
}
