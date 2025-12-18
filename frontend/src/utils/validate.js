// src/utils/validate.js

// 1. 邮箱格式校验
export const validateEmail = (email) => {
    const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!email.trim()) return '邮箱不能为空';
    if (!reg.test(email.trim())) return '请输入有效的邮箱地址（如：xxx@qq.com）';
    return ''; // 无错误返回空
};

// 2. 密码校验（8-16位）
export const validatePassword = (pwd) => {
    if (!pwd.trim()) return '密码不能为空';
    if (pwd.length < 8 || pwd.length > 16) return '密码需8-16位';
    return '';
};

// 3. 确认密码校验
export const validateConfirmPwd = (pwd, confirmPwd) => {
    if (!confirmPwd.trim()) return '请确认密码';
    if (confirmPwd !== pwd) return '两次密码输入不一致';
    return '';
};

// 4. 非空校验（通用：姓名/学号等）
export const validateRequired = (value, label) => {
    if (!value.trim()) return `${label}不能为空`;
    return '';
};

// 5. 注册表单整体校验（整合所有规则）
export const validateRegisterForm = (form) => {
    const errors = {};
    // 真实姓名
    errors.username = validateRequired(form.username, '真实姓名');
    // 学号
    errors.account = validateRequired(form.account, '学号');
    // 邮箱
    errors.email = validateEmail(form.email);
    // 密码
    errors.password = validatePassword(form.password);
    // 确认密码
    errors.confirmPwd = validateConfirmPwd(form.password, form.confirmPwd);
    // 过滤空错误（只保留有问题的字段）
    Object.keys(errors).forEach(key => {
        if (!errors[key]) delete errors[key];
    });
    return errors;
};

// 6. 登录表单整体校验（复用非空规则）
export const validateLoginForm = (form) => {
    const errors = {};
    errors.student_number = validateRequired(form.student_number, '学号');
    errors.password = validateRequired(form.password, '密码');
    Object.keys(errors).forEach(key => {
        if (!errors[key]) delete errors[key];
    });
    return errors;
};