import axios from 'axios';

import { getToken, clearLoginState } from './auth';
import CryptoJS from 'crypto-js';
// 引入依赖：axios（请求库）、storage工具（取token）、权限工具（判断登录状态）
import { getStorage } from './storage';
import { isLogin } from './permission';
// 创建axios实例，配置基础路径和超时时间
const service = axios.create({
    baseURL: 'http://pannfmis', // 接口基础地址（对应接口文档测试地址）
    timeout: 10000 // 请求超时时间：10秒
});

// 请求拦截器：添加请求头、token、加密处理
service.interceptors.request.use(
    (config) => {
        // 1. 已登录则添加token（接口文档登录返回JWT令牌，需携带访问）
        if (isLogin()) {
            config.headers['Authorization'] = `Bearer ${getToken()}`;
        }

        // 2. 核心修复：按规则生成签名+加密（先处理参数，再加密）
        // 2.1 兜底参数：确保参数是对象（不管是params还是data）
        const requestParams = {
            ...(config.params || {}), // GET参数兜底
            ...(config.data || {})    // POST参数兜底
        };

        // 2.2 生成签名+加密（用之前验证过的encryptData，自动加timestamp+sign）
        const encryptedData = encryptData(requestParams);
        // 2.3 重置请求参数：加密后统一放在data里（适配大多数接口）
        config.data = encryptedData;
        config.params = {}; // 清空params，避免重复传参
        return config;
    },
    (error) => {
        // 请求错误统一提示
        console.error('请求发送失败：', error.message);
        return Promise.reject(error);
    }
);

// 响应拦截器：统一处理返回码、解密、错误提示
service.interceptors.response.use(
    (response) => {
        // 1. 核心修复：只解密后端返回的data字段（不是整个response.data）
        // 注意：后端返回格式应该是 { res_code: '0000', res_msg: '成功', data: '加密字符串' }
        const originalRes = response.data; // 后端原始返回体
        let decryptedData = {};

        // 只解密data字段，其他字段（res_code/res_msg）是明文
        if (originalRes.data) {
            decryptedData = decryptData(originalRes.data);
        }
        const result = {
            res_code: originalRes.res_code, // 保留状态码
            res_msg: originalRes.res_msg,   // 保留提示信息
            data: decryptedData             // 解密后的用户数据
        };
        // 2. 统一处理返回码（对应接口文档返回码说明）
        switch (originalRes.res_code) { // 用原始返回的res_code（明文）
            case '0000': // 请求成功：返回解密后的数据
                return result;
            case '0003': // 权限不足：跳转登录页
                console.error('权限不足或登录失效');
                // 此处可添加路由跳转逻辑（如跳转到登录页）
                // router.push('/login');
                return Promise.reject({
                    res_code: originalRes.res_code,
                    res_msg: originalRes.res_msg
                });
            default: // 其他错误：提示错误信息
                console.error('接口请求失败：', originalRes.res_msg);
                return Promise.reject({
                    res_code: originalRes.res_code,
                    res_msg: originalRes.res_msg
                });
        }
    },
    (error) => {
        // 网络错误统一提示
        console.error('网络错误：', error.message);
        return Promise.reject('网络异常，请稍后重试');
    }
);
// 固定配置（与前后端约定一致）
const CONFIG = {
    signSecretKey: 'Pann2025Key',
    encryptKeyStr: 'Pann2025EncKey00', // 补2个0，凑16位
    encryptIVStr: 'Pann2025IV123450',  // 补1个0，凑16位
};

/**
 * 生成签名（严格按规则：字段名升序拼接值 + timestamp + 固定秘钥 → MD5小写）
 */
function generateSign(params) {
    if (!params || typeof params !== 'object') return '';

    // 加入必传的timestamp（13位时间戳）
    const paramsWithTs = { ...params, timestamp: Date.now() };
    // 按字段名字母升序排序
    const sortedKeys = Object.keys(paramsWithTs).sort();
    // 拼接所有字段值（无分隔符）+ 固定秘钥
    const signStr = sortedKeys.reduce((str, key) => {
        return str + (paramsWithTs[key] === undefined || paramsWithTs[key] === null ? '' : paramsWithTs[key]);
    }, '') + CONFIG.signSecretKey;

    // MD5加密并转小写
    return CryptoJS.MD5(signStr).toString().toLowerCase();
}

/**
 * 加密函数（重构：解决字节对齐问题）
 */
function encryptData(params) {
    if (!params || typeof params !== 'object') return '';

    // 1. 生成timestamp和sign
    const timestamp = Date.now();
    const sign = generateSign({ ...params, timestamp });
    // 2. 整合待加密数据并转UTF-8字节
    const dataToEncrypt = JSON.stringify({ ...params, timestamp, sign });
    const dataUtf8 = CryptoJS.enc.Utf8.parse(dataToEncrypt);

    // 3. 解析密钥/IV（确保字节长度严格16位）
    const encryptKey = CryptoJS.enc.Utf8.parse(CONFIG.encryptKeyStr);
    const encryptIV = CryptoJS.enc.Utf8.parse(CONFIG.encryptIVStr);

    // 4. AES-CBC加密（显式处理填充，避免字节错位）
    const encrypted = CryptoJS.AES.encrypt(dataUtf8, encryptKey, {
        iv: encryptIV,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
        // 强制UTF-8编码
        format: {
            stringify: function (cipherParams) {
                return cipherParams.ciphertext.toString(CryptoJS.enc.Base64);
            },
            parse: function (str) {
                return CryptoJS.lib.CipherParams.create({
                    ciphertext: CryptoJS.enc.Base64.parse(str)
                });
            }
        }
    });

    // 5. 仅返回Base64编码的密文（剔除多余信息）
    return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
}

/**
 * 解密函数（重构：修复UTF-8解析异常）
 */
function decryptData(encryptedData) {
    // 1. 先校验入参：确保是字符串且不为空

    if (!encryptedData || typeof encryptedData !== 'string') {
        console.error('解密失败：加密字符串为空或格式错误');
        return {};
    }

    try {
        // 2. 解析密钥/IV（用你自己的CONFIG配置，不用改）
        const encryptKey = CryptoJS.enc.Utf8.parse(CONFIG.encryptKeyStr);
        const encryptIV = CryptoJS.enc.Utf8.parse(CONFIG.encryptIVStr);

        // 3. 关键：把前端传的Base64加密字符串转成WordArray（AES解密要求的格式）
        const ciphertext = CryptoJS.enc.Base64.parse(encryptedData);

        // 4. AES-CBC解密（修复填充和编码）
        const decrypted = CryptoJS.AES.decrypt(
            { ciphertext: ciphertext }, // 传入正确的密文
            encryptKey,
            {
                iv: encryptIV,
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7,
            }
        );

        // 5. 把解密后的结果转成字符串（先Latin1再UTF8，解决乱码）
        const plainTextLatin1 = decrypted.toString(CryptoJS.enc.Latin1);
        const plainTextUtf8 = CryptoJS.enc.Utf8.stringify(CryptoJS.enc.Latin1.parse(plainTextLatin1));

        // 6. 解析JSON并返回（容错处理）
        const result = JSON.parse(plainTextUtf8 || '{}');
        return result;
    } catch (error) {
        return {};
    }
}
// 导出使用
export { encryptData, decryptData, generateSign };

// 导出常用请求方法（GET/POST/PUT/DELETE）
export const request = ({ method, url, params, data }) => {
    switch (method.toLowerCase()) {
        case 'get':
            return service.get(url, { params })
        case 'post':
            return service.post(url, data)
        case 'put':
            return service.put(url, data)
        case 'delete':
            return service.delete(url, { params })
        default:
            throw new Error(`不支持的请求方法：${method}`)
    }
}