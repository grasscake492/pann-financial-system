//导入util工具
import { request, storage, format, permission } from '@/utils/index.js'
/**
 * 添加代领记录（运营部管理员专属，文档编号2.5.16）
 * @param {Object} params - 请求参数
 * @param {bigint} params.fee_record_id - 稿费记录ID（必填）
 * @param {bigint} params.department_id - 部门ID（必填）
 * @param {bigint} params.original_user_id - 原用户ID（必填）
 * @param {bigint} params.proxy_user_id - 代领用户ID（必填）
 * @param {string} params.proxy_month - 代领月份（必填）
 * @param {decimal} params.fee_amount - 金额（必填）
 * @returns {Promise} - 请求结果（含代领记录ID）
 */
export const addProxyRecord = (params) => {
    return request({
        method: 'POST',
        url: '/api/v1/admin/proxy',
        data: params
    })
}

/**
 * 查询代领记录（运营部管理员专属，文档编号2.5.17）
 * @param {Object} params - 请求参数
 * @param {bigint} [params.department_id] - 部门ID（可选）
 * @param {bigint} [params.original_user_id] - 原用户ID（可选）
 * @param {bigint} [params.proxy_user_id] - 代领用户ID（可选）
 * @param {string} [params.proxy_month] - 代领月份（可选）
 * @param {number} [params.page=1] - 页码（可选，默认1）
 * @param {number} [params.size=10] - 每页数量（可选，默认10）
 * @returns {Promise} - 请求结果（含代领记录列表、总记录数）
 */
export const getProxyList = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/proxy/list',
        params: params
    })
}

/**
 * 修改代领稿费记录（运营部管理员专属，文档编号2.5.18）
 * @param {bigint} proxyId - 代领记录ID（路径参数）
 * @param {Object} params - 请求参数
 * @param {bigint} [params.department_id] - 部门ID（可选）
 * @param {bigint} [params.original_user_id] - 原用户ID（可选）
 * @param {bigint} [params.proxy_user_id] - 代领用户ID（可选）
 * @param {string} [params.proxy_month] - 代领月份（可选）
 * @param {bigint} [params.fee_record_id] - 稿费记录ID（可选）
 * @param {string} [params.article_title] - 稿件标题（可选）
 * @param {decimal} [params.fee_amount] - 金额（可选）
 * @param {string} [params.description] - 备注（可选）
 * @returns {Promise} - 请求结果（含代领记录ID、更新时间）
 */
export const updateProxyRecord = (proxyId, params) => {
    return request({
        method: 'PUT',
        url: `/api/v1/admin/proxy/${proxyId}`,
        data: params
    })
}

/**
 * 撤销代领稿费记录（运营部管理员专属，文档编号2.5.19）
 * @param {bigint} proxyId - 代领记录ID（路径参数，必填）
 * @returns {Promise} - 请求结果（含代领记录ID、删除时间）
 */
export const cancelProxyRecord = (proxyId) => {
    return request({
        method: 'DELETE',
        url: `/api/v1/admin/proxy/${proxyId}`
    })
}