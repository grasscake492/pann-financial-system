import { defineStore } from 'pinia'
import { storage } from '@/utils/index.js'
// 替换为真实的用户信息接口（需你根据项目调整路径）
import {fetchUserProfile} from "@/api/user.js";


/**
 * @typedef {Object} UserInfo
 * @property {string} user_id
 * @property {string} student_number
 * @property {string} real_name
 * @property {string} email
 * @property {string} token
 * @property {string[]} permissions
 * @property {boolean} is_super_admin
 * @property {string} department_id
 * @property {string} department_name
 * @property {string} admin_id
 * @property {boolean} isLogin
 */
export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: {
            user_id: '',
            student_number: '',
            real_name: '',
            email: '',
            token: storage.getToken() || '', // 初始化时读取Token
            permissions: [],
            is_super_admin: false,
            department_id: '',
            department_name: '',
            admin_id: '',
            isLogin: !!storage.getToken() // 用Token判断登录状态，更稳定
        }
    }),

    getters: {
        userRole() {
            const userInfo = this.userInfo || {}; // 兜底 userInfo 为空对象
            // 1. 优先判断超级管理员（最高权限）
            if (userInfo.is_super_admin) {
                return 'super_admin';
            }

            // 2. 按 department_name 区分具体部门管理员
            const deptName = userInfo.department_name || '';
            if (deptName === '新闻部') {
                return 'news_admin'; // 新闻部管理员
            } else if (deptName === '编辑部') {
                return 'editorial_admin'; // 编辑部管理员
            } else if (deptName === '运营部') {
                return 'operation_admin'; // 运营部管理员
            }

            // 3. 其余情况（无部门/非上述部门）均为普通用户
            return 'normal_user';
        },
        // 可选：新增快捷判断各部门管理员的 getter（方便后续使用）
        isNewsAdmin() {
            return this.userRole === 'news_admin';
        },
        isEditorAdmin() {
            return this.userRole === 'editorial_admin';
        },
        isOperationAdmin() {
            return this.userRole === 'operation_admin';
        },
        isDeptAdmin() {
            // 只要是任意部门管理员，都返回 true
            return ['news_admin', 'editorial_admin', 'operation_admin'].includes(this.userRole);
        },
        isSuperAdmin() {
            return this.userRole === 'super_admin';
        },
        isNormalUser() {
            return this.userRole === 'normal_user';
        }
    },

    actions: {
        // 修复：真实调用用户信息接口
        async loadUserProfile() {
            const token = this.userInfo.token; // 读的是登录页存入stores的token
            if (!token) {
                console.warn('【fetchUserProfile】无Token，跳过请求');
                return; // 直接返回，避免后续逻辑执行
            }

            try {
                // 调用真实接口（参数需根据后端要求调整）
                const res = await fetchUserProfile()
                if (res.res_code === "0000") {
                    // 合并接口返回的用户信息，标记登录状态
                    this.userInfo = {
                        ...this.userInfo,
                        ...res.data,
                        isLogin: true
                    }

                } else {
                    console.error('【fetchUserProfile】接口返回错误：', res.msg);
                }
            } catch (error) {
                console.error('【fetchUserProfile】请求失败：', error);

            }
        },

        // 登录成功后更新用户信息（登录页调用）
        updateUserInfo(data) {
            this.userInfo.token = data.token || this.userInfo.token
            this.userInfo = { ...this.userInfo, ...data.userInfo, isLogin: true }
            // 同步存储Token到本地（保证刷新页面不丢失）
            storage.setToken(this.userInfo.token)
        },

        setIsSuperAdmin(isSuperAdmin) {
            this.userInfo.is_super_admin = isSuperAdmin
        },

        setDeptName(deptName) {
            this.userInfo.department_name = deptName.trim()
        },

        setIsAdmin(isAdmin) {
            if (typeof isAdmin === 'boolean') {
                this.userInfo.admin_id = isAdmin ? this.userInfo.admin_id : ''
            } else {
                this.userInfo.admin_id = isAdmin
            }
        },

        setUserInfo(userData) {
            this.userInfo = { ...this.userInfo, ...userData, isLogin: true }
            storage.setToken(this.userInfo.token) // 同步Token
        },

        updateUserProfile(data) {
            this.userInfo.real_name = data.real_name || this.userInfo.real_name
            this.userInfo.email = data.email || this.userInfo.email
        },

        async changePassword(oldPwd, newPwd) {
            // 后续补充真实接口调用
            // const res = await changePwdApi({ oldPwd, newPwd })
            // if (res.code === 0) {
            //   console.log('密码修改成功');
            // }
        },

        clearUserInfo() {
            this.userInfo = {
                user_id: '',
                student_number: '',
                real_name: '',
                email: '',
                token: '',
                permissions: [],
                is_super_admin: false,
                department_id: '',
                department_name: '',
                admin_id: '',
                isLogin: false
            }
            // 清空本地Token
            storage.removeToken()
        },

        updateUserRole(data) {
            if (this.userInfo.is_super_admin) {
                this.userInfo.is_super_admin = data.is_super_admin || this.userInfo.is_super_admin
                this.userInfo.department_id = data.department_id || this.userInfo.department_id
            }
        }
    }
})