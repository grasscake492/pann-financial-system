// src/utils/auth.js
// 存储token的本地缓存key（自定义，避免冲突）
const TOKEN_KEY = 'pann_financial_token';

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