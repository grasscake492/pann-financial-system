// 统一导入配置好拦截器的 request 实例（而非原始 axios）
import request from '@/utils/request';

// 注册接口函数（对齐接口文档2.5.1节）
export function userRegister(data) {
    return request({
        url: '/auth/register', // 文档地址：http://pannfmis/auth/register/xxx → 简化为 /auth/register
        method: 'post',
        data: data // 需包含 sign/student_number/real_name/password/email（文档必填字段）
    });
}

// 登录接口函数（对齐接口文档2.5.2节）
export const loginApi = (params) => {
    return request({
        url: '/auth/login', // 文档地址：http://pannfmis/auth/login/xxx → 简化为 /auth/login
        method: 'post',
        data: params // 需包含 sign/student_number/password（文档必填字段）
    });
};

// 获取当前登录用户信息接口（对齐接口文档2.5.5节）
export const getUserInfo = () => {
    return request({
        url: '/user/profile', // 文档地址：http://pannfmis/user/profile/xxx → 简化为 /user/profile
        method: 'GET', // 文档要求的GET方式
        // 无需传参，后端通过token识别用户
    });
};

// 修改密码接口（对齐接口文档2.5.3节）
export const changePassword = (data) => {
    return request({
        url: '/auth/change-password', // 文档地址：http://pannfmis/auth/change-password/xxx → 简化为 /auth/change-password
        method: 'put', // 文档要求的PUT方式
        data: data // 需包含 sign/old_password/new_password（文档必填字段）
    });
};

// 修改用户信息接口（对齐接口文档2.5.6节）
export const editUserInfo = (data) => {
    return request({
        url: '/user/profile', // 文档地址：http://pannfmis/user/profile/xxx → 复用个人信息地址（文档统一路径）
        method: 'put', // 文档要求的PUT方式（原post错误）
        data: data // 需包含 sign/real_name/email（文档必填/选填字段）
    });
};