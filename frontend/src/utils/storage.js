// 本地存储工具：封装localStorage操作，处理JSON序列化/反序列化
// 避免重复写JSON.stringify/parse，统一处理存取值逻辑

// 存储前缀：防止key冲突（可选，项目复杂时推荐）
const STORAGE_PREFIX = 'pann_';

/**
 * 存值到本地存储
 * @param {string} key - 存储键名
 * @param {any} value - 存储值（支持对象、数组等类型）
 */
/**
 * 设置token到本地存储
 * @param {string} token - 要存储的令牌字符串
 */
export const setToken = (token) => {
    // 校验参数类型（非字符串转字符串，防止存错）
    if (typeof token !== 'string') {
        token = String(token)
    }
    // 存到localStorage，加前缀
    localStorage.setItem(`${STORAGE_PREFIX}token`, token)
}

/**
 * 获取本地存储的token
 * @returns {string|null} 存储的token（无则返回null）
 */
export const getToken = () => {
    return localStorage.getItem(`${STORAGE_PREFIX}token`)
}

/**
 * 清除本地存储的token
 */
export const removeToken = () => {
    localStorage.removeItem(`${STORAGE_PREFIX}token`)
}

// 配套的setRole/setUserInfo方法（你代码里也用到了）
export const setRole = (role) => {
    localStorage.setItem(`${STORAGE_PREFIX}user_role`, role)
}

export const setUserInfo = (userInfo) => {
    // 对象转JSON字符串存储
    localStorage.setItem(`${STORAGE_PREFIX}user_info`, JSON.stringify(userInfo))
}


export const setStorage = (key, value) => {
    try {
        // 序列化值（对象/数组转字符串）
        const stringValue = JSON.stringify(value);
        localStorage.setItem(STORAGE_PREFIX + key, stringValue);
    } catch (error) {
        console.error('本地存储存值失败：', error.message);
    }
};

/**
 * 从本地存储取值
 * @param {string} key - 存储键名
 * @returns {any} 解析后的值（对象/数组/基本类型，无值返回null）
 */
export const getStorage = (key) => {
    try {
        const stringValue = localStorage.getItem(STORAGE_PREFIX + key);
        if (!stringValue) return null;

        // 核心修复：先尝试转JSON，失败则返回原字符串（一步兼容所有场景）
        try {
            // 能转JSON（比如用户信息对象）→ 转成JSON返回
            return JSON.parse(stringValue);
        } catch (e) {
            // 转不了JSON（比如token密文字符串）→ 直接返回原字符串
            return stringValue;
        }
    } catch (error) {
        console.error('本地存储取值失败：', error.message);
        return null;
    }
};

/**
 * 删除本地存储的指定键
 * @param {string} key - 存储键名
 */
export const removeStorage = (key) => {
    try {
        localStorage.removeItem(STORAGE_PREFIX + key);
    } catch (error) {
        console.error('本地存储删值失败：', error.message);
    }
};

/**
 * 清空所有本地存储（谨慎使用）
 */
export const clearStorage = () => {
    try {
        localStorage.clear();
    } catch (error) {
        console.error('本地存储清空失败：', error.message);
    }
};