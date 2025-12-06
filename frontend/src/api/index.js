import axios from 'axios'
import md5 from 'js-md5' // 引入MD5加密库（需先安装）
// 导入权限工具：获取/删除token
import { getToken, removeToken } from '@/utils/auth'

// 签名生成函数（对齐接口文档2.2节规则）
const SECRET_KEY = import.meta.env.VITE_SIGN_SECRET || '默认秘钥'; // 建议配置到环境变量
const generateSign = (data) => {
    if (!data) return '';
    // 步骤1：按字段名字母序排序（文档要求）
    const sortedKeys = Object.keys(data).sort();
    // 步骤2：拼接字段值
    let signStr = '';
    sortedKeys.forEach(key => {
        signStr += data[key];
    });
    // 步骤3：拼接秘钥后MD5加密
    signStr += SECRET_KEY;
    return md5(signStr);
};

// 创建Axios实例，统一配置请求参数
const service = axios.create({
    // 基础请求路径：优先读取环境变量，无则用/api（和vite.config.js的proxy对应）
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    // 请求超时时间：5秒
    timeout: 5000,
    // 请求头默认类型
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 请求拦截器：发送请求前的统一处理（加token + 自动生成sign）
service.interceptors.request.use(
    (config) => {
        // 1. 登录后，给所有请求头添加token（后端校验登录状态用）
        const token = getToken()
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        // 2. 自动生成sign（GET用params，POST/PUT用data）
        const signData = config.method === 'get' ? config.params : config.data;
        if (signData) {
            config.headers.sign = generateSign(signData);
        }

        return config
    },
    (error) => {
        // 请求出错时的统一处理（打印日志）
        console.error('请求拦截器错误：', error)
        return Promise.reject(error)
    }
)

// 响应拦截器：接收响应后的统一处理（比如错误码处理）
service.interceptors.response.use(
    (response) => {
        // 只返回后端的data部分，简化页面调用
        return response.data
    },
    (error) => {
        // 统一处理错误
        console.error('响应拦截器错误：', error)

        // 401状态码：token过期/未授权，清除token并跳登录页
        if (error.response?.status === 401) {
            removeToken()
            window.location.href = '/login'
        }

        return Promise.reject(error)
    }
)

// ========== 业务接口封装 ==========
// 1. 注册接口（对齐文档2.5.1节）
export const userRegister = (data) => {
    return service({
        url: '/auth/register',
        method: 'post',
        data // 需传：student_number/real_name/password/email + 自动生成sign
    });
};

// 2. 登录接口（对齐文档2.5.2节）
export const loginApi = (data) => {
    return service({
        url: '/auth/login',
        method: 'post',
        data // 需传：student_number/password + 自动生成sign
    });
};

// 3. 获取用户信息接口（对齐文档2.5.5节）
export const getUserInfo = () => {
    return service({
        url: '/user/profile',
        method: 'get' // 无参数，token鉴权，自动生成空sign（不影响）
    });
};

// 4. 公告列表接口（对齐文档2.5.27节）
export const getAnnouncementList = (params) => {
    return service({
        url: '/announcement/list',
        method: 'get',
        params // 需传：pageNum/pageSize/sortType（如sortType=desc按时间倒序）
    });
};

// 5. 修改密码接口（对齐文档2.5.3节）
export const changePassword = (data) => {
    return service({
        url: '/user/change-password',
        method: 'put',
        data // 需传：old_password/new_password + 自动生成sign
    });
};

// 6. 修改用户信息接口（对齐文档2.5.6节）
export const editUserInfo = (data) => {
    return service({
        url: '/user/profile',
        method: 'put',
        data // 需传：real_name/email + 自动生成sign
    });
};

// 导出Axios实例，供其他api文件调用
export default service