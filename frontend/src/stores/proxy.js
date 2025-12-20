import { defineStore } from 'pinia'
//导入接口
import api from '@/api/index.js'
// 导入util统一入口（工具都从这拿）
import { storage, format, permission } from '@/utils/index.js'


export const useProxyStore = defineStore('proxy', {
    state: () => ({
        // 代领记录列表（与proxy_receipt_records表、查询代领接口返回字段一致）
        proxyList: [
            {
                proxy_id: '', // 代领记录ID
                fee_record_id: '', // 关联稿费记录ID
                department_id: '', // 所属部门ID
                original_user_id: '', // 原领取人ID
                original_real_name: '', // 原领取人姓名
                original_student_number: '', // 原领取人学号
                proxy_user_id: '', // 代领人ID
                proxy_real_name: '', // 代领人姓名
                proxy_student_number: '', // 代领人学号
                article_title: '', // 文章标题
                fee_amount: 0, // 稿费金额
                proxy_month: '', // 代领月份
                created_at: '', // 创建时间
                updated_at: '' // 更新时间
            }
        ],
        total: 0, // 代领记录总条数（分页用，接口返回）
        currentPage: 1, // 当前页码（分页用）
        pageSize: 10 // 每页条数（分页用）
    }),
    getters: {
        // 后续按需补充权限/筛选相关getter，如：当前部门代领记录、待处理代领记录等
    },
    actions: {
        // 存储查询到的代领列表（含分页数据）
        setProxyList() {},
        // 设置当前页码
        setCurrentPage() {},
        // 添加代领记录
        addProxyRecord() {},
        // 修改代领记录
        updateProxyRecord() {},
        // 撤销代领记录（删除）
        cancelProxyRecord() {},
        // 清空代领相关缓存
        clearProxyInfo() {}
    }
})