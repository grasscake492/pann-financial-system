// 稿费模块（fee.js）- Pinia 结构（仅存state，action/getter留空）
import { defineStore } from 'pinia'
//导入接口

import api from '@/api/index.js'
// 导入util统一入口（工具都从这拿）
import { storage, format, permission } from '@/utils/index.js'
export const useFeeStore = defineStore('fee', {
    state: () => ({
        // 1. 个人稿费列表（对应查询个人稿费接口返回结构）
        personal_fee_list: [
            {
                record_id: '', // 记录ID
                article_title: '', // 稿件标题
                article_type: '', // 稿件类型
                fee_amount: 0, // 稿费金额（DECIMAL(10,2)）
                statistical_month: '', // 统计月份（YYYY-MM）
                department_id: '', // 部门ID
                created_at: '', // 创建时间
                updated_at: '' // 更新时间
            }
        ],
        // 2. 部门/全部稿费列表（对应查询部门/全部稿费接口返回结构）
        fee_list: [
            {
                record_id: '', // 记录ID
                user_ids: [], // 用户ID数组（JSON）
                real_names: [], // 真实姓名数组（JSON）
                student_numbers: [], // 学号数组（JSON）
                article_title: '', // 稿件标题
                article_type: '', // 稿件类型
                fee_amount: 0, // 稿费金额（DECIMAL(10,2)）
                statistical_month: '', // 统计月份（YYYY-MM）
                department_id: '', // 部门ID
                created_at: '' // 创建时间
            }
        ],
        // 3. 月度汇总数据（对应稿费月汇总表结构）
        monthly_summary: [
            {
                summary_id: '', // 汇总ID
                user_id: '', // 用户ID
                real_name: '', // 真实姓名
                student_number: '', // 学号
                department_id: '', // 部门ID
                total_amount: 0, // 月度总金额（DECIMAL(10,2)）
                statistical_month: '', // 统计月份（YYYY-MM）
                created_at: '', // 创建时间
                updated_at: '' // 更新时间
            }
        ],
        // 4. 分页相关字段（接口返回的分页参数）
        total: 0, // 总记录数
        current_page: 1, // 当前页码
        page_size: 10 // 每页条数（默认10）
    }),

    getters: {
        // 1. 个人稿费总额
        personalTotalAmount: (state) => {
            return state.personal_fee_list.reduce((sum, item) => sum + Number(item.fee_amount || 0), 0)
        },

        // 2. 部门分类总额（返回对象：{ '新闻部': 1000, '编辑部': 800 }）
        departmentTotals: (state) => {
            const totals = {}
            state.fee_list.forEach(item => {
                const dept = item.department_name || '未知部门'
                totals[dept] = (totals[dept] || 0) + Number(item.fee_amount || 0)
            })
            return totals
        },

        // 3. 全部稿费总额
        allTotalAmount: (state) => {
            return state.fee_list.reduce((sum, item) => sum + Number(item.fee_amount || 0), 0)
        },

        // 4. 分页后的个人稿费列表（可选：用于展示）
        pagedPersonalFeeList: (state) => {
            const start = (state.current_page - 1) * state.page_size
            const end = start + state.page_size
            return state.personal_fee_list.slice(start, end)
        },

        // 5. 分页后的部门/全部稿费列表
        pagedFeeList: (state) => {
            const start = (state.current_page - 1) * state.page_size
            const end = start + state.page_size
            return state.fee_list.slice(start, end)
        }
    },

    actions: {
        // 查询个人稿费
        async fetchPersonalFeeList(page = 1, size = 10, startDate, endDate, userId) {
            try {
                const params = { page, size, startDate, endDate, user_id: userId };
                const res = await api.royalty.getPersonalRoyalty(params);
                if (res.res_code === "0000") {
                    this.personal_fee_list = res.data.list || [];
                    this.total = res.data.total || 0;
                    this.current_page = page;
                }
            } catch (error) {
                console.error("个人稿费获取失败:", error);
            }
        },

        // 查询部门稿费
        async fetchDepartmentFeeList(page = 1, size = 10, startDate, endDate, userId = null) {
            try {
                const params = { page, size, startDate, endDate };
                if (userId) params.user_id = userId; // 可选按成员筛选
                const res = await api.royalty.getDepartmentRoyalty(params);
                if (res.res_code === "0000") {
                    this.fee_list = res.data.list || [];
                    this.total = res.data.total || 0;
                    this.current_page = page;
                }
            } catch (error) {
                console.error("部门稿费获取失败:", error);
            }
        },

        // 查询全部稿费（汇总用）
        async fetchAllFeeList(page = 1, size = 10, startDate, endDate, departmentId = null) {
            try {
                const params = { page, size, startDate, endDate };
                if (departmentId) params.department = departmentId;
                const res = await api.royalty.getAllRoyalty(params);
                if (res.res_code === "0000") {
                    this.fee_list = res.data.list || [];
                    this.total = res.data.total || 0;
                    this.current_page = page;
                }
            } catch (error) {
                console.error("全部稿费获取失败:", error);
            }
        },

        // 导出稿费
        async exportFee(statisticalMonth, departmentId, format) {
            try {
                const params = { statistical_month: statisticalMonth, department_id: departmentId, format };
                const res = await api.royalty.exportRoyaltyRecord(params);
                return res;
            } catch (error) {
                console.error("稿费导出失败:", error);
            }
        }
    }

})