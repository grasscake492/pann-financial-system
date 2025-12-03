package org.controller;

import org.entity.User;
import org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ---------------- 查询操作 ----------------

    @ResponseBody
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @ResponseBody
    @RequestMapping(value = "/studentNumber/{studentNumber}", method = RequestMethod.GET)
    public User getUserByStudentNumber(@PathVariable("studentNumber") String studentNumber) {
        return userService.getUserByStudentNumber(studentNumber);
    }

    @ResponseBody
    @RequestMapping(value = "/email/{email}", method = RequestMethod.GET)
    public User getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ResponseBody
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public List<User> getUsersByPage(@RequestParam("page") int page,
                                     @RequestParam("size") int size) {
        return userService.getUsersByPage(page, size);
    }

    // ---------------- 用户登录 ----------------

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestParam("studentNumber") String studentNumber,
                      @RequestParam("password") String password) {
        return userService.login(studentNumber, password);
    }

    // ---------------- 密码操作 ----------------

    @ResponseBody
    @RequestMapping(value = "/{userId}/resetPassword", method = RequestMethod.POST)
    public boolean resetPassword(@PathVariable("userId") Long userId,
                                 @RequestParam("newPassword") String newPassword) {
        return userService.resetPassword(userId, newPassword);
    }

    @ResponseBody
    @RequestMapping(value = "/{userId}/updatePassword", method = RequestMethod.POST)
    public boolean updatePassword(@PathVariable("userId") Long userId,
                                  @RequestParam("oldPassword") String oldPassword,
                                  @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(userId, oldPassword, newPassword);
    }

    // ---------------- 用户 CRUD ----------------

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT)
    public boolean updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public boolean createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @ResponseBody
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public boolean deleteUser(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId);
    }
}
