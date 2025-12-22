//导入util工具
import { request, storage, format, permission } from '@/utils/index.js'

/**
 * 查询个人稿费（文档编号2.5.9）
 * @param {Object} params - 请求参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @param {number} [params.page=1] - 页码（可选，默认1）
 * @param {number} [params.size=10] - 每页数量（可选，默认10）
 * @returns {Promise} - 请求结果（含稿费列表、总记录数）
 */
export const getPersonalRoyalty = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/royalty/personal',
        params: params
    })
}

/**
 * 查询部门稿费（部门管理员专属，文档编号2.5.10）
 * @param {Object} params - 请求参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @param {number} [params.page=1] - 页码（可选，默认1）
 * @param {number} [params.size=10] - 每页数量（可选，默认10）
 * @param {bigint} [params.user_id] - 用户ID（可选，按成员筛选）
 * @returns {Promise} - 请求结果（含部门稿费列表、总记录数）
 */
export const getDepartmentRoyalty = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/royalty/department',
        params: params
    })
}

/**
 * 查询全部稿费（系统管理员专属，文档编号2.5.11）
 * @param {Object} params - 请求参数
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @param {bigint} [params.department] - 部门ID（可选）
 * @param {number} [params.page=1] - 页码（可选，默认1）
 * @param {number} [params.size=10] - 每页数量（可选，默认10）
 * @returns {Promise} - 请求结果（含全部稿费列表、总记录数）
 */
export const getAllRoyalty = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/royalty/all',
        params: params
    })
}

/**
 * 添加稿费记录（管理员专属，文档编号2.5.12）
 * @param {Object} params - 请求参数
 * @param {JSON} params.user_id - 用户ID（必填）
 * @param {JSON} params.real_names - 真实姓名数组（必填）
 * @param {JSON} params.student_numbers - 学号数组（必填）
 * @param {string} params.article_title - 稿件标题（必填）
 * @param {string} params.article_type - 稿件类型（必填）
 * @param {decimal} params.fee_amount - 稿费金额（必填）
 * @param {string} params.statistical_month - 统计月份（YYYY-MM，必填）
 * @param {bigint} params.department_id - 部门ID（必填）
 * @returns {Promise} - 请求结果（含新增稿费记录ID）
 */
export const addRoyaltyRecord = (params) => {
    return request({
        method: 'POST',
        url: '/api/v1/admin/royalty',
        data: params
    })
}

/**
 * 修改稿费记录（管理员专属，文档编号2.5.13）
 * @param {number} recordId - 稿费记录ID（路径参数）
 * @param {Object} params - 请求参数
 * @param {string} params.article_title - 稿件标题（必填）
 * @param {string} params.article_type - 稿件类型（必填）
 * @param {decimal} params.fee_amount - 稿费金额（必填）
 * @param {string} params.statistical_month - 统计月份（YYYY-MM，必填）
 * @param {string} params.description - 备注（必填）
 * @returns {Promise} - 请求结果（含更新时间）
 */
export const updateRoyaltyRecord = (recordId, params) => {
    return request({
        method: 'PUT',
        url: `/api/v1/admin/royalty/${recordId}`,
        data: params
    })
}

/**
 * 删除稿费记录（管理员专属，文档编号2.5.14）
 * @param {bigint} recordId - 稿费记录ID（必填）
 * @returns {Promise} - 请求结果（含删除时间）
 */
export const deleteRoyaltyRecord = (recordId) => {
    return request({
        method: 'DELETE',
        url: `/api/v1/admin/royalty/${recordId}`
    })
}

/**
 * 导出稿费记录（管理员专属，文档编号2.5.15）
 * @param {Object} params - 请求参数
 * @param {string} params.statistical_month - 统计月份（YYYY-MM，必填）
 * @param {bigint} params.department_id - 部门ID（必填）
 * @param {string} params.format - 导出格式（Excel/PDF，必填）
 * @returns {Promise} - 请求结果（含文件链接、导出时间、记录数）
 */
export const exportRoyaltyRecord = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/royalty/export',
        params: params,
        //responseType: 'blob' // 导出文件需配置响应类型为blob
    })
}