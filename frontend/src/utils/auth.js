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

/**
 * 严格的登录状态检查函数
 * 现在需要满足三个条件：token、用户信息、userStore的验证状态
 */
export function isLogin() {
    // 获取userStore实例
    let userStore;
    try {
        // 尝试获取userStore实例
        userStore = useUserStore();
    } catch (error) {
        // 如果在setup之外调用，可能需要其他方式
        console.warn('无法获取userStore，使用本地存储检查');
        userStore = null;
    }

    // 1. 检查本地存储的token
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
        return false;
    }

    // 2. 检查本地存储的用户信息
    const userInfoStr = localStorage.getItem(USER_INFO_KEY);
    if (!userInfoStr) {
        return false;
    }

    // 3. 验证用户信息是否为有效JSON
    try {
        const userInfo = JSON.parse(userInfoStr);
        if (!userInfo.user_id) {
            return false;
        }
    } catch (error) {
        console.error('用户信息解析失败:', error);
        return false;
    }

    // 4. 如果有userStore实例，检查其验证状态
    if (userStore) {
        // 等待userStore完成验证（如果尚未验证）
        if (!userStore.authChecked && token) {
            // 注意：这里不能直接调用异步函数，因为isLogin需要同步返回
            // 可以在外部调用验证，这里只检查结果
            console.log('userStore尚未完成验证');
        }

        // 如果userStore有验证结果，使用它
        if (userStore.authChecked) {
            return userStore.userInfo.isLogin === true;
        }
    }

    // 5. 检查用户角色
    const userRole = localStorage.getItem(ROLE_KEY);
    if (!userRole) {
        return false;
    }

    // 6. 所有条件都满足，返回true
    return true;
}

/**
 * 简化的登录状态检查（仅检查本地存储）
 * 用于路由守卫等不需要userStore验证的场景
 */
export function checkLocalLogin() {
    const token = localStorage.getItem(TOKEN_KEY);
    const userInfo = localStorage.getItem(USER_INFO_KEY);
    const userRole = localStorage.getItem(ROLE_KEY);

    return !!(token && userInfo && userRole);
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

/**
 * 批量清空token和角色（退出登录统一调用）
 * 退出登录时清空所有登录态（token+角色+用户信息）
 */
export const clearLoginState = () => {
    removeToken();
    removeRole();
    removeUserInfo();

    // 尝试清除userStore状态
    try {
        const userStore = useUserStore();
        userStore.clearUserInfo();
    } catch (error) {
        console.warn('清除userStore状态失败:', error);
    }

    // 退出登录后跳转登录页（适配首页退出操作）
    router.push('/login');
};

/**
 * 首页专用登录态校验函数（带跳转逻辑）
 * 校验登录态，无登录态则自动跳转登录页
 * @returns {boolean} 校验结果（true=已登录，false=未登录）
 */
export const checkHomeLoginStatus = () => {
    const hasToken = checkLocalLogin();
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

/**
 * 新增：权限校验基础函数（后续首页/其他页面可扩展）
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

/**
 * 新增：快速检查登录状态，用于路由守卫等场景
 * 这个函数会尝试使用userStore验证，但会快速返回结果
 */
export const quickCheckLogin = async () => {
    // 先检查本地存储
    if (!checkLocalLogin()) {
        return false;
    }

    // 如果有token但userStore未验证，可以在这里验证
    try {
        const userStore = useUserStore();
        if (!userStore.authChecked) {
            // 尝试快速验证，设置超时
            const timeoutPromise = new Promise((_, reject) =>
                setTimeout(() => reject(new Error('验证超时')), 1000)
            );

            const validationPromise = userStore.initAndValidateUser();
            await Promise.race([validationPromise, timeoutPromise]);
        }

        return userStore.isValidLogin;
    } catch (error) {
        console.warn('快速登录检查失败:', error);
        // 验证失败，但本地有数据，可能还在登录状态
        return checkLocalLogin();
    }
};

/**
 * 新增：验证并清理无效的登录数据
 * 用于应用启动时清理无效的token
 */
export const validateAndCleanLogin = async () => {
    const token = getToken();
    const userInfo = getUserInfo();
    const role = getRole();

    // 检查数据完整性
    const isComplete = token && userInfo && role;

    if (!isComplete) {
        console.log('登录数据不完整，执行清理');
        clearLoginState();
        return false;
    }

    // 验证用户信息有效性
    if (!userInfo.user_id || !userInfo.department_name) {
        console.log('用户信息不完整，执行清理');
        clearLoginState();
        return false;
    }

    return true;
};

/**
 * 新增：初始化应用登录状态
 * 在main.js或App.vue中调用
 */
export const initLoginState = async () => {
    console.log('初始化登录状态...');

    // 先验证并清理
    const isValid = await validateAndCleanLogin();

    if (!isValid) {
        console.log('登录数据无效，已清理');
        return false;
    }

    // 如果有有效的登录数据，尝试验证
    try {
        const userStore = useUserStore();
        if (userStore.userInfo.token && !userStore.authChecked) {
            console.log('发现token，开始验证...');
            await userStore.initAndValidateUser();
            return userStore.isValidLogin;
        }
    } catch (error) {
        console.error('初始化登录状态失败:', error);
        return false;
    }

    return false;
};