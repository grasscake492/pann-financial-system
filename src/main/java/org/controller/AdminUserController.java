package org.controller;

import org.entity.User;
import org.result.result;
import org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员用户管理接口
 * 需要管理员权限
 */
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     */
    @GetMapping
    public result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // 不返回密码
        users.forEach(u -> u.setPassword(null));
        return result.success(users);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public result<List<User>> getUsersByPage(@RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        List<User> users = userService.getUsersByPage(page, size);
        users.forEach(u -> u.setPassword(null));
        return result.success(users);
    }

    /**
     * 根据用户ID查询
     */
    @GetMapping("/{userId}")
    public result<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return result.error("用户不存在");
        }
        user.setPassword(null);
        return result.success(user);
    }

    /**
     * 创建用户（管理员）
     */
    @PostMapping
    public result<?> createUser(@RequestBody User user) {
        boolean success = userService.createUser(user);
        return success ? result.success("创建成功")
                : result.error("创建失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    public result<?> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return success ? result.success("更新成功")
                : result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public result<?> deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteUser(userId);
        return success ? result.success("删除成功")
                : result.error("删除失败");
    }

    /**
     * 管理员重置用户密码
     */
    @PostMapping("/{userId}/reset-password")
    public result<?> resetPassword(@PathVariable Long userId,
                                   @RequestParam("newPassword") String newPassword) {

        boolean success = userService.resetPassword(userId, newPassword);
        return success ? result.success("密码已重置")
                : result.error("重置失败");
    }
}
