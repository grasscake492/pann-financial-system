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
        // 后续需添加的 getter 留空
    },
    actions: {
        // 后续需添加的 action 留空
    }
})