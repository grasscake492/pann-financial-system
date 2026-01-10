import { defineStore } from 'pinia'
import { storage } from '@/utils/index.js'
// 替换为真实的用户信息接口（需你根据项目调整路径）
import { fetchUserProfile } from "@/api/user.js";

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
            isLogin: false // 强制初始为false，等待验证
        },
        authChecked: false // 新增：标记是否已完成权限验证
    }),

    getters: {
        userRole() {
            const userInfo = this.userInfo || {};
            // 1. 优先判断超级管理员（最高权限）
            if (userInfo.is_super_admin) {
                return 'super_admin';
            }

            // 2. 按 department_name 区分具体部门管理员
            const deptName = userInfo.department_name || '';
            if (deptName === '新闻部') {
                return 'news_admin';
            } else if (deptName === '编辑部') {
                return 'editorial_admin';
            } else if (deptName === '运营部') {
                return 'operation_admin';
            }

            // 3. 其余情况（无部门/非上述部门）均为普通用户
            return 'normal_user';
        },

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
            return ['news_admin', 'editorial_admin', 'operation_admin'].includes(this.userRole);
        },

        isSuperAdmin() {
            return this.userRole === 'super_admin';
        },

        isNormalUser() {
            return this.userRole === 'normal_user';
        },

        // 新增：严格的登录验证getter
        isValidLogin() {
            return (
                this.userInfo.token &&
                this.userInfo.isLogin &&
                this.userInfo.user_id &&
                this.authChecked
            );
        }
    },

    actions: {
        // 新增：初始化时验证token有效性
        async initAndValidateUser() {
            const token = storage.getToken();

            // 如果没有token，直接设为未登录
            if (!token) {
                this.clearUserInfo();
                this.authChecked = true;
                return false;
            }

            // 如果有token，验证其有效性
            try {
                console.log('开始验证token有效性...');
                const res = await this.loadUserProfile();

                if (res && res.res_code === "0000") {
                    // token有效，设置登录状态
                    this.userInfo.isLogin = true;
                    this.authChecked = true;
                    console.log('Token验证成功，用户已登录');
                    return true;
                } else {
                    // token无效，清理
                    console.warn('Token验证失败，清理用户数据');
                    this.clearUserInfo();
                    this.authChecked = true;
                    return false;
                }
            } catch (error) {
                console.error('Token验证异常:', error);
                this.clearUserInfo();
                this.authChecked = true;
                return false;
            }
        },

        // 修复：真实调用用户信息接口
        async loadUserProfile() {
            const token = this.userInfo.token;
            if (!token) {
                console.warn('【fetchUserProfile】无Token，跳过用户信息拉取');
                this.userInfo.isLogin = false;
                return;
            }

            try {
                // 调用真实接口
                const res = await fetchUserProfile();

                if (res.res_code === "0000") {
                    // 兜底：如果接口返回的data为空，不覆盖原有数据
                    const newData = res.data || {};

                    // 合并接口返回的用户信息，标记登录状态
                    this.userInfo = {
                        ...newData,
                        token: this.userInfo.token, // 保持原有token
                        isLogin: true // 验证成功才设为true
                    };

                    // 更新本地存储的用户信息
                    if (newData.user_id) {
                        localStorage.setItem('pann_user_info', JSON.stringify(newData));
                    }

                    console.log('用户信息加载成功');
                    return res;
                } else {
                    console.error('【fetchUserProfile】接口返回错误：', res.res_msg || '未知错误');
                    this.userInfo.isLogin = false;
                    return res;
                }
            } catch (error) {
                console.error('【fetchUserProfile】请求失败：', error);
                // 请求失败，设为未登录状态
                this.userInfo.isLogin = false;
                throw error; // 抛出错误让调用者处理
            }
        },

        // 登录成功后更新用户信息（登录页调用）
        updateUserInfo(data) {
            if (data.token) {
                this.userInfo.token = data.token;
                storage.setToken(data.token);
            }

            // 合并用户信息，但isLogin暂时不设置
            this.userInfo = {
                ...this.userInfo,
                ...data.userInfo
            };

            // 保存用户信息到本地存储
            if (data.userInfo) {
                localStorage.setItem('pann_user_info', JSON.stringify(data.userInfo));
            }
        },

        // 登录验证成功后的最终确认
        confirmLogin() {
            this.userInfo.isLogin = true;
            this.authChecked = true;

            // 保存角色信息到本地存储（用于路由守卫）
            localStorage.setItem('pann_user_role', this.userRole);
        },

        setIsSuperAdmin(isSuperAdmin) {
            this.userInfo.is_super_admin = isSuperAdmin;
        },

        setDeptName(deptName) {
            this.userInfo.department_name = deptName.trim();
        },

        setIsAdmin(isAdmin) {
            if (typeof isAdmin === 'boolean') {
                this.userInfo.admin_id = isAdmin ? this.userInfo.admin_id : '';
            } else {
                this.userInfo.admin_id = isAdmin;
            }
        },

        setUserInfo(userData) {
            this.userInfo = {
                ...this.userInfo,
                ...userData
            };

            if (userData.token) {
                storage.setToken(userData.token);
            }
        },

        updateUserProfile(data) {
            this.userInfo.real_name = data.real_name || this.userInfo.real_name;
            this.userInfo.email = data.email || this.userInfo.email;
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
            };
            this.authChecked = false;

            // 清空本地存储的所有登录相关数据
            storage.removeToken();
            localStorage.removeItem('pann_user_info');
            localStorage.removeItem('pann_user_role');

            console.log('用户信息已清除');
        },

        updateUserRole(data) {
            if (this.userInfo.is_super_admin) {
                this.userInfo.is_super_admin = data.is_super_admin || this.userInfo.is_super_admin;
                this.userInfo.department_id = data.department_id || this.userInfo.department_id;
            }
        },

        // 新增：检查并更新本地存储的角色信息
        updateLocalStorageRole() {
            localStorage.setItem('pann_user_role', this.userRole);
        }
    }
});