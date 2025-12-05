package org.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidatorUtil {

    // 学号正则：8-12位数字
    private static final Pattern STUDENT_NUMBER_PATTERN = Pattern.compile("^\\d{8,12}$");

    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // 密码正则：至少包含字母和数字，6-20位
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$");

    /**
     * 验证学号格式
     */
    public boolean isValidStudentNumber(String studentNumber) {
        return studentNumber != null && STUDENT_NUMBER_PATTERN.matcher(studentNumber).matches();
    }

    /**
     * 验证邮箱格式
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证密码格式
     */
    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证真实姓名（中文或英文，2-50个字符）
     */
    public boolean isValidRealName(String realName) {
        return realName != null && realName.length() >= 2 && realName.length() <= 50;
    }
}