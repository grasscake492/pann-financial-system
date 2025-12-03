package org.service;

import org.entity.User;
import java.util.List;

public interface UserService {

    User getUserById(Long userId);

    User getUserByStudentNumber(String studentNumber);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    List<User> getUsersByPage(int page, int size);

    User login(String studentNumber, String password); // 登录通过学号

    boolean resetPassword(Long userId, String newPassword);

    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    boolean updateUser(User user);

    boolean createUser(User user);

    boolean deleteUser(Long userId);
}
