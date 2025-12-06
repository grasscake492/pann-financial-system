// src/utils/auth.js
import router from '@/router'; // 新增：引入路由实例，用于登录态失效时跳转

// 存储token的本地缓存key（自定义，避免冲突）
const TOKEN_KEY = 'pann_financial_token';
// 存储用户角色的本地缓存key（新增）
const ROLE_KEY = 'pann_user_role';

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

/**
 * 判断用户是否已登录（路由守卫核心函数）
 * @returns {boolean} 登录状态（有token则认为已登录）
 */
export const isLogin = () => {
    return !!getToken(); // 双重!!转为布尔值
};

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

// 批量清空token和角色（退出登录统一调用）
/**
 * 退出登录时清空所有登录态（token+角色）
 */
export const clearLoginState = () => {
    removeToken();
    removeRole();
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