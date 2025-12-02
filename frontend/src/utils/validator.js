/**
 * 注册表单校验
 * @param {Object} form - 注册表单数据（包含studentNumber/realName/password/email）
 * @returns {Object} 校验错误信息（空对象代表无错误）
 */
export const validateRegisterForm = (form) => {
    const errors = {};

    // 学号校验：非空 + 10-20位数字
    if (!form.studentNumber) {
        errors.studentNumber = '学号不能为空';
    } else if (!/^\d{10,20}$/.test(form.studentNumber)) {
        errors.studentNumber = '学号必须是10-20位数字';
    }

    // 真实姓名校验：非空 + 2-10位中文
    if (!form.realName) {
        errors.realName = '真实姓名不能为空';
    } else if (!/^[\u4e00-\u9fa5]{2,10}$/.test(form.realName)) {
        errors.realName = '姓名必须是2-10位中文';
    }

    // 密码校验：非空 + 8-20位（包含数字+字母）
    if (!form.password) {
        errors.password = '密码不能为空';
    } else if (!/^(?=.*\d)(?=.*[a-zA-Z]).{8,20}$/.test(form.password)) {
        errors.password = '密码需8-20位，且包含数字和字母';
    }

    // 邮箱校验：非空 + 合法格式
    if (!form.email) {
        errors.email = '邮箱不能为空';
    } else if (!/^[\w-]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,4}$/.test(form.email)) {
        errors.email = '邮箱格式不正确';
    }

    return errors;
};

/**
 * 登录表单校验
 * @param {Object} form - 登录表单数据（包含studentNumber/password）
 * @returns {Object} 校验错误信息
 */
export const validateLoginForm = (form) => {
    const errors = {};

    if (!form.studentNumber) {
        errors.studentNumber = '学号不能为空';
    }
    if (!form.password) {
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