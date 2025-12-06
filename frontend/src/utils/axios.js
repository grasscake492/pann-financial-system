// src/utils/axios.js
import axios from 'axios';

// 创建Axios实例
const instance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL, // 从环境变量获取接口基础路径
    timeout: 5000 // 请求超时时间
});

// 请求拦截器（可选）
instance.interceptors.request.use(
    (config) => {
        // 可添加token等请求头
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器（可选）
instance.interceptors.response.use(
    (response) => response.data, // 直接返回响应数据
    (error) => Promise.reject(error)
);

export default instance;