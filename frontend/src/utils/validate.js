/**
 * 注册表单校验（对齐Register.vue字段名 + 接口文档约束）
 * @param {Object} form - 注册表单数据（包含account/username/password/email/confirmPwd）
 * @returns {Object} 校验错误信息（空对象代表无错误）
 */
export const validateRegisterForm = (form) => {
    const errors = {};

    // 学号校验：仅非空（格式/长度由后端校验，避免前端约束过严）
    if (!form.account?.trim()) {
        errors.account = '学号不能为空';
    }

    // 真实姓名校验：仅非空（取消中文限制，适配特殊姓名场景）
    if (!form.username?.trim()) {
        errors.username = '真实姓名不能为空';
    }

    // 密码校验：非空 + 8-16位（简化复杂度，和Register.vue一致）
    if (!form.password?.trim()) {
        errors.password = '密码不能为空';
    } else if (form.password.length < 8 || form.password.length > 16) {
        errors.password = '密码需8-16位';
    }

    // 确认密码校验：新增（和密码一致）
    if (!form.confirmPwd?.trim()) {
        errors.confirmPwd = '请确认密码';
    } else if (form.confirmPwd !== form.password) {
        errors.confirmPwd = '两次密码输入不一致';
    }

    // 邮箱校验：非空 + 通用格式（和Register.vue一致）
    if (!form.email?.trim()) {
        errors.email = '邮箱不能为空';
    } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email.trim())) {
        errors.email = '请输入有效的邮箱地址（如：xxx@qq.com）';
    }

    return errors;
};

/**
 * 登录表单校验（对齐Login.vue字段名：account/password）
 * @param {Object} form - 登录表单数据（包含account/password）
 * @returns {Object} 校验错误信息
 */
export const validateLoginForm = (form) => {
    const errors = {};

    // 学号（账号）校验：仅非空（和登录页现有逻辑一致）
    if (!form.account?.trim()) {
        errors.account = '账号不能为空';
    }

    // 密码校验：仅非空（和登录页现有逻辑一致，复杂度由后端校验）
    if (!form.password?.trim()) {
        errors.password = '密码不能为空';
    }

    return errors;
};

/**
 * 通用手机号校验（若后续需要）
 * @param {string} phone - 手机号
 * @returns {string|null} 错误信息（null代表无错误）
 */
export const validatePhone = (phone) => {
    if (!phone) return '手机号不能为空';
    if (!/^1[3-9]\d{9}$/.test(phone)) return '手机号格式不正确';
    return null;
};