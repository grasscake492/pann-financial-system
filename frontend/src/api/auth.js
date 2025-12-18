//导入util工具
import { request } from '@/utils/request.js'


/**
 * 用户注册接口（文档编号2.5.1）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {string} params.student_number - 学号（必填）
 * @param {string} params.real_name - 真实姓名（必填）
 * @param {string} params.password - 密码（必填）
 * @param {string} params.email - 邮箱（必填）
 * @returns {Promise} - 请求结果
 */
export const userRegister = (params) => {
    return request({
        method: 'POST',
        url: '/auth/register/xxx',
        data: params
    })
}

/**
 * 用户登录接口（文档编号2.5.2）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {string} params.student_number - 学号（必填）
 * @param {string} params.password - 密码（必填）
 * @returns {Promise} - 请求结果（含token、用户信息等）
 */
export const userLogin = (params) => {
    console.log('准备发送登录请求，参数：', params);
    return request({
        method: 'POST',
        url: '/auth/login/xxx',
        data: params
    })
}
/**
 * 修改密码接口（文档编号2.5.3）
 * @param {string} userId - 用户ID（必填，替换url里的xxx）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {string} params.old_password - 旧密码（必填）
 * @param {string} params.new_password - 新密码（选填）
 * @returns {Promise} - 请求结果
 */
export const changePassword = (userId, params) => { // 新增userId参数
    return request({
        method: 'PUT',
        url: `/auth/change-password/${userId}`, // xxx替换为${userId}
        data: params
    })
}

/**
 * 退出登录接口（文档编号2.5.4）
 * @returns {Promise} - 请求结果
 */
export const userLogout = () => {
    return request({
        method: 'POST',
        url: '/auth/logout/xxx'
    })
}