//导入util工具
import { request, storage, format, permission } from '@/utils/index.js'

/**
 * 获取用户个人信息接口（文档编号2.5.5）
 * @returns {Promise} - 请求结果（含用户ID、学号、姓名等信息）
 */
export const fetchUserProfile = () => {
    return request({
        method: 'GET',
        url: '/user/profile/xxx'
    })
}

/**
 * 更新用户信息接口（文档编号2.5.6）
 * @param {string} userId - 用户ID（必填，替换url里的xxx）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {string} [params.real_name] - 真实姓名（选填）
 * @param {string} [params.email] - 邮箱（选填）
 * @returns {Promise} - 请求结果（含更新后用户信息）
 */
export const updateUserProfile = (userId, params) => { // 新增userId参数
    return request({
        method: 'PUT',
        url: `/user/profile/${userId}`, // xxx替换为${userId}
        data: params
    })
}
/**
 * 获取用户列表接口（管理员专属，文档编号2.5.7）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {number} [params.page] - 页码（选填）
 * @param {number} [params.size] - 每页数量（选填）
 * @param {string} [params.keyword] - 搜索关键词（选填）
 * @returns {Promise} - 请求结果（含用户列表、总记录数）
 */
export const getUserList = (params) => {
    return request({
        method: 'GET',
        url: '/admin/users/xxx',
        params: params // GET请求参数放params
    })
}

/**
 * 修改用户角色接口（系统管理员专属，文档编号2.5.8）
 * @param {Object} params - 请求参数
 * @param {string} params.sign - 签名（必填）
 * @param {string} params.user_id - 用户ID（必填）
 * @param {boolean} params.is_super_admin - 是否最高级管理员（必填）
 * @param {string} [params.department_id] - 部门ID（选填）
 * @returns {Promise} - 请求结果（含修改后用户角色相关信息）
 */
export const updateUserRole = (params) => {
    return request({
        method: 'PUT',
        url: '/admin/users/role/xxx',
        data: params
    })
}