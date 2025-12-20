package org.controller;

import org.entity.User;
import org.result.result;
import org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 普通用户接口
 * 只能操作自己的信息
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     * 实际项目中 userId 应从 session / token 中获取
     */
    @GetMapping("/me")
    public result<User> getCurrentUser(@RequestParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return result.error("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return result.success(user);
    }

    /**
     * 修改当前用户基本信息
     */
    @PutMapping("/me")
    public result<?> updateProfile(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? result.success("更新成功")
                : result.error("更新失败");
    }

    /**
     * 修改当前用户密码
     */
    @PostMapping("/me/password")
    public result<?> updatePassword(@RequestParam("userId") Long userId,
                                    @RequestParam("oldPassword") String oldPassword,
                                    @RequestParam("newPassword") String newPassword) {

        boolean success = userService.updatePassword(userId, oldPassword, newPassword);
        return success ? result.success("密码修改成功")
                : result.error("原密码错误");
    }
}
