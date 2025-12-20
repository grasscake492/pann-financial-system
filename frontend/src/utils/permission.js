// 权限控制工具：基于用户角色和权限列表，提供权限判断方法
// 统一权限规则，避免页面中重复写权限判断逻辑
import { getStorage } from './storage';

/**
 * 判断用户是否已登录（基于本地存储的token）
 * @returns {boolean} 已登录返回true，未登录返回false
 */
export const isLogin = () => {
    const token = getStorage('token');
    return !!token; // token存在则视为已登录
};

/**
 * 判断是否是超级管理员（适配管理员表is_super_admin字段）
 * @returns {boolean} 是超级管理员返回true，否则返回false
 */
export const isSuperAdmin = () => {
    const userInfo = getStorage('userInfo');
    // 用户信息存在且是超级管理员才返回true
    return userInfo?.role === 'admin' && userInfo?.is_super_admin === true;
};

/**
 * 判断是否是部门管理员（适配管理员表department_id字段）
 * @returns {boolean} 是部门管理员返回true，否则返回false
 */
export const isDeptAdmin = () => {
    const userInfo = getStorage('userInfo');
    // 用户信息存在且有部门ID（非超级管理员）视为部门管理员
    return userInfo?.role === 'admin' && !!userInfo?.department_id && !userInfo?.is_super_admin;
};

/**
 * 判断用户是否有指定权限（适配权限表permission_code字段）
 * @param {string} permissionCode - 权限代码（如FEE_WRITE、SYSTEM_MANAGE）
 * @returns {boolean} 有权限返回true，否则返回false
 */
export const hasPermission = (permissionCode) => {
    const userInfo = getStorage('userInfo');
    if (!userInfo?.permissions) return false;
    // 权限列表包含目标权限码则返回true
    return userInfo.permissions.includes(permissionCode);
};

/**
 * 权限拦截：未登录则跳转到登录页（需结合项目路由实现）
 * @returns {boolean} 已登录返回true，未登录返回false并跳转
 */
export const loginGuard = () => {
    if (!isLogin()) {
        console.log('未登录，跳转登录页');
        // 此处替换为项目路由跳转逻辑（如window.location.href = '/login'）
        return false;
    }
    return true;
};