import axios from 'axios'
import { getToken } from '@/utils/auth.js';
import { ElMessage } from 'element-plus'
// 导入CryptoJS,加密
import CryptoJS from 'crypto-js';

// 创建axios实例
const request = axios.create({
    //baseURL: process.env.NODE_ENV === 'development' ? '/api' : 'http://pannfmis',
    // 把原来的真实后端地址改成下面这行
    baseURL: 'http://localhost:3001/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 加密函数（和后端约定密钥/模式/填充）
const encryptData = (data) => {
    const secretKey = CryptoJS.enc.Utf8.parse('pannfmis2025'); // 替换为后端密钥
    const dataStr = JSON.stringify(data);
    const encrypted = CryptoJS.AES.encrypt(dataStr, secretKey, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return encrypted.toString();
}

// 解密函数
const decryptData = (data) => {
    const secretKey = CryptoJS.enc.Utf8.parse('pannfmis2025'); // 替换为后端密钥
    const decrypted = CryptoJS.AES.decrypt(data, secretKey, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    const decryptedStr = CryptoJS.enc.Utf8.stringify(decrypted);
    return JSON.parse(decryptedStr);
}

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        // 启用加密
        if (config.data) {
            // 关闭加密：注释下面这行 + 新增config.data = config.data（原样返回）
            // config.data = encryptData(config.data);
            config.data = config.data; // 原样传递请求数据，不加密
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        const res = response.data;
        if (res.res_code && res.res_code !== '0000') {
            ElMessage.error(res.res_msg || '请求失败');
            return Promise.reject(res);
        }
        // 启用解密
        if (res.data) {
            // 关闭解密：注释下面这行 + 新增res.data = res.data（原样返回）
            // res.data = decryptData(res.data);
            res.data = res.data; // 原样接收响应数据，不解密
        }
        return res;
    },
    (error) => {
        if (error.response?.status === 401) {
            ElMessage.error('登录已过期，请重新登录');
        }
        else if (error.response?.data?.res_msg) {
            ElMessage.error(`请求错误：${error.response.data.res_msg}`);
        }
        else if (error.response) {
            ElMessage.error(`请求错误：${error.response.status} - ${error.response.statusText}`)
        } else if (error.request) {
            ElMessage.error('网络异常，请求未发送成功，请检查网络')
        } else {
            ElMessage.error(`请求配置错误：${error.message}`)
        }
        return Promise.reject(error)
    }
)

export default request