import { defineStore } from 'pinia';
import { getToken, setToken, removeToken } from '@/utils/auth';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: getToken(), // 从本地缓存读token
        studentNumber: '',
        realName: '',
        role: localStorage.getItem('pann_user_role') || '' // 🌟 新增：初始化时从本地读角色
    }),

    actions: {
        /**
         * 登录：存储用户信息（含多角色）+ token + 同步到本地
         * @param {Object} userInfo - 包含token/role/studentNumber/realName
         */
        login(userInfo) {
            this.token = userInfo.token;
            this.studentNumber = userInfo.studentNumber;
            this.realName = userInfo.realName;
            this.role = userInfo.role; // 存储多角色（user/operation_admin等）

            // 🌟 新增：把角色同步到localStorage（和Pinia双存储，防止刷新丢失）
            localStorage.setItem('pann_user_role', userInfo.role);
            // 同步token到本地
            setToken(userInfo.token);
        },

        /**
         * 退出登录：清空所有状态 + 删除本地缓存
         */
        logout() {
            this.token = '';
            this.studentNumber = '';
            this.realName = '';
            this.role = '';
            removeToken();
            // 🌟 新增：清空本地存储的角色
            localStorage.removeItem('pann_user_role');
        },

        /**
         * 更新用户信息（含角色）
         * @param {Object} info - 如{role: 'super_admin'}
         */
        updateUserInfo(info) {
            Object.assign(this, info);
            // 若更新了角色，同步到本地
            if (info.role) {
                localStorage.setItem('pann_user_role', info.role);
            }
        }
    }
});