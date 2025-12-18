//导入util工具
import { request, storage, format, permission } from '@/utils/index.js'
/**
 * 提交问题反馈（文档编号2.5.20）
 * @param {Object} params - 请求参数
 * @param {string} params.user_id - 用户ID（必填）
 * @param {string} params.content - 反馈内容（必填）
 * @returns {Promise} - 请求结果（含反馈ID、提交时间）
 */
export const submitFeedback = (params) => {
    return request({
        method: 'POST',
        url: '/api/v1/feedback',
        data: params
    })
}

/**
 * 用户查询问题反馈（文档编号2.5.21）
 * @param {Object} params - 请求参数
 * @param {number} [params.page=1] - 页码（可选，默认1）
 * @param {number} [params.size=10] - 每页数量（可选，默认10）
 * @returns {Promise} - 请求结果（含反馈列表、总记录数、页码信息）
 */
export const getUserFeedbackList = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/feedback/my',
        params: params
    })
}

/**
 * 查询反馈详情（文档编号2.5.22）
 * @param {string} feedbackId - 反馈ID（路径参数，必填）
 * @returns {Promise} - 请求结果（含反馈完整信息）
 */
export const getFeedbackDetail = (feedbackId) => {
    return request({
        method: 'GET',
        url: `/api/v1/feedback/${feedbackId}`
    })
}

/**
 * 查询待处理反馈（管理员专属，文档编号2.5.23）
 * @param {Object} params - 请求参数
 * @param {number} params.page - 页码（必填，≥1）
 * @param {number} params.size - 每页数量（必填，1-50）
 * @returns {Promise} - 请求结果（含待处理反馈列表、总记录数）
 */
export const getPendingFeedback = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/feedback/pending',
        params: params
    })
}

/**
 * 查询所有反馈（管理员专属，文档编号2.5.24）
 * @param {Object} params - 请求参数
 * @param {number} params.page - 页码（必填，≥1）
 * @param {number} params.size - 每页数量（必填，1-50）
 * @param {string} [params.status] - 反馈状态（可选）
 * @returns {Promise} - 请求结果（含所有反馈列表、总记录数）
 */
export const getAllFeedback = (params) => {
    return request({
        method: 'GET',
        url: '/api/v1/admin/feedback/all',
        params: params
    })
}

/**
 * 回复用户反馈（管理员专属，文档编号2.5.25）
 * @param {string} feedbackId - 反馈ID（路径参数，必填）
 * @param {Object} params - 请求参数
 * @param {string} params.reply_content - 回复内容（必填）
 * @returns {Promise} - 请求结果（含回复结果描述、回复时间）
 */
export const replyFeedback = (feedbackId, params) => {
    return request({
        method: 'POST',
        url: `/api/v1/admin/feedback/${feedbackId}/reply`,
        data: params
    })
}

/**
 * 更新反馈状态（管理员专属，文档编号2.5.26）
 * @param {string} feedbackId - 反馈ID（路径参数，必填）
 * @param {Object} params - 请求参数
 * @param {string} params.status - 反馈状态（必填）
 * @returns {Promise} - 请求结果（含状态更新描述、更新时间）
 */
export const updateFeedbackStatus = (feedbackId, params) => {
    return request({
        method: 'PUT',
        url: `/api/v1/admin/feedback/${feedbackId}/status`,
        data: params
    })
}