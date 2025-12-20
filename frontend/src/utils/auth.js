// src/utils/auth.js
import router from '@/router'; // 新增：引入路由实例，用于登录态失效时跳转
import { useUserStore } from '@/stores/user'
// 存储token的本地缓存key（自定义，避免冲突）
const TOKEN_KEY = 'pann_token';
// 存储用户角色的本地缓存key（新增）
const ROLE_KEY = 'pann_user_role';
// 存储用户信息的本地缓存key（新增）
const USER_INFO_KEY = 'pann_user_info';

/**
 * 存储token到localStorage（登录成功后调用）
 * @param {string} token - 后端返回的登录令牌
 */
export const setToken = (token) => {
    localStorage.setItem(TOKEN_KEY, token);
};

/**
 * 从localStorage获取token
 * @returns {string} token值（无则返回空字符串）
 */
export const getToken = () => {
    return localStorage.getItem(TOKEN_KEY) || '';
};

/**
 * 删除localStorage中的token（退出登录时调用）
 */
export const removeToken = () => {
    localStorage.removeItem(TOKEN_KEY);
};

export function isLogin() {
    // 只看本地存储里有没有登录凭证（token/用户信息），有就是已登录
    const token = localStorage.getItem('pann_token') // 登录后存的token，没有就改你实际的key
    const userInfo = localStorage.getItem('pann_user_info')
    // 有token 且 有用户信息 = 已登录，否则未登录
    return !!token && !!userInfo
}

// 存储用户角色到localStorage
/**
 * 存储用户角色（登录成功后调用）
 * @param {string} role - 用户角色（admin/operator/visitor/user）
 */
export const setRole = (role) => {
    localStorage.setItem(ROLE_KEY, role);
};

// 从localStorage获取用户角色
/**
 * 获取用户角色
 * @returns {string} 角色值（无则返回空字符串）
 */
export const getRole = () => {
    return localStorage.getItem(ROLE_KEY) || '';
};

// 删除localStorage中的用户角色（退出登录时调用）
export const removeRole = () => {
    localStorage.removeItem(ROLE_KEY);
};
/**
 * 存储用户信息到localStorage
 * @param {Object} userInfo - 用户信息对象（包含user_id/real_name/student_number/email等）
 */
export const setUserInfo = (userInfo) => {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
};

/**
 * 从localStorage获取用户信息
 * @returns {Object|null} 用户信息对象（无则返回null）
 */
export const getUserInfo = () => {
    const info = localStorage.getItem(USER_INFO_KEY);
    return info ? JSON.parse(info) : null;
};

/**
 * 删除localStorage中的用户信息（退出登录时调用）
 */
export const removeUserInfo = () => {
    localStorage.removeItem(USER_INFO_KEY);
};

// 批量清空token和角色（退出登录统一调用）
/**
 * 退出登录时清空所有登录态（token+角色+用户信息）
 */
export const clearLoginState = () => {
    removeToken();
    removeRole();
    removeUserInfo(); // 新增：清空用户信息
    // 新增：退出登录后跳转登录页（适配首页退出操作）
    router.push('/login');
};

// 首页专用登录态校验函数（带跳转逻辑）
/**
 * 校验登录态，无登录态则自动跳转登录页
 * @returns {boolean} 校验结果（true=已登录，false=未登录）
 */
export const checkHomeLoginStatus = () => {
    const hasToken = isLogin();
    if (!hasToken) {
        // 未登录：跳转登录页并记录来源页（方便登录后返回首页）
        router.push({
            path: '/login',
            query: { redirect: '/home' } // 携带跳转来源参数
        });
        return false;
    }
    return true;
};

// 新增：权限校验基础函数（后续首页/其他页面可扩展）
/**
 * 校验用户是否拥有指定角色
 * @param {string|Array<string>} allowRoles - 允许的角色列表（如['admin', 'operator']）
 * @returns {boolean} 权限校验结果
 */
export const hasRole = (allowRoles) => {
    const currentRole = getRole();
    if (!currentRole) return false;
    // 兼容单个角色字符串或角色数组
    if (typeof allowRoles === 'string') {
        return currentRole === allowRoles;
    }
    return allowRoles.includes(currentRole);
};