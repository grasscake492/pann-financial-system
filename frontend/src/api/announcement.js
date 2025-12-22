// src/api/announcement.js
//导入util工具
import { request } from '@/utils/index.js'

/**
 * 获取公告详情（文档编号2.5.27）
 * @param {string} announcementId - 公告ID（路径参数，必填）
 * @param {Object} params - 含sign签名等参数
 * @returns {Promise} - 请求结果（含公告完整信息）
 */
export const getAnnouncementDetail = (announcementId, params) => {
    return request({
        method: 'GET',
        url: `/api/v1/announcements/${announcementId}`,
        params: {
            sign: params.sign, // 签名（按文档2.2节规则生成）
            ...params
        }
    })
}

/**
 * 管理员发布公告（文档编号2.5.28）
 * @param {Object} params - 请求参数
 * @param {string} params.title - 公告标题（必填）
 * @param {string} params.content - 公告内容（必填）
 * @param {string} params.publisher_id - 发布者ID（必填）
 * @returns {Promise} - 请求结果（含公告ID、发布结果描述、发布时间）
 */
/** @type {Function} */ // 解决ESLint误报
export const publishAnnouncement = (params) => {
    return request({
        method: 'POST',
        url: '/api/v1/admin/announcements',
        data: params
    })
}

/**
 * 管理员修改公告（文档编号2.5.29）
 * @param {string} announcementId - 公告ID（路径参数，必填）
 * @param {Object} params - 请求参数
 * @param {string} params.title - 公告标题（必填）
 * @param {string} params.content - 公告内容（必填）
 * @param {string} params.publisher_id - 发布者ID（必填）
 * @returns {Promise} - 请求结果（含修改结果描述、更新时间）
 */
export const updateAnnouncement = (announcementId, params) => {
    return request({
        method: 'PUT',
        url: `/api/v1/admin/announcements/${announcementId}`,
        data: params
    })
}

/**
 * 管理员删除公告（文档编号2.5.30）
 * @param {string} announcementId - 公告ID（路径参数，必填）
 * @returns {Promise} - 请求结果（含删除结果描述）
 */
export const deleteAnnouncement = (announcementId) => {
    return request({
        method: 'DELETE',
        url: `/api/v1/admin/announcements/${announcementId}`
    })
}


/**
 * 获取所有公告（分页+筛选+排序）（文档编号2.5.31）
 * @param {Object} params - 请求参数（严格匹配接口约束）
 * @param {number} params.page - 页码（必选，≥1）
 * @param {number} params.size - 每页数量（必选，1-50）
 * @param {bigint|string|number} [params.publisher_id] - 发布者ID（可选）
 * @param {string} [params.keyword] - 标题关键词（可选，模糊匹配）
 * @param {string} [params.order_by] - 排序字段（可选，默认published_at）
 * @param {string} [params.sort] - 排序方式（可选，asc/desc，默认desc）
 * @param {string} [params.sign] - 签名（若接口需要签名则传，参考2.2节规则）
 * @returns {Promise} - 请求结果（含公告列表、分页信息）
 */
export const getAllAnnouncements = (params) => {
    // 参数前置校验（可选，提前拦截非法参数，减少接口请求错误）
    if (!params.page || params.page < 1) {
        console.error('获取公告失败：页码必须≥1');
        return Promise.reject(new Error('页码必须≥1'));
    }
    if (!params.size || params.size < 1 || params.size > 50) {
        console.error('获取公告失败：每页数量必须在1-50之间');
        return Promise.reject(new Error('每页数量必须在1-50之间'));
    }

    return request({
        method: 'GET',
        url: '/api/v1/announcements', // 匹配接口地址
        params: {
            // 若接口需要签名，按参考示例添加sign（根据项目实际规则生成）
            // sign: params.sign,
            page: params.page,
            size: params.size,
            publisher_id: params.publisher_id, // 可选参数，无则自动忽略
            keyword: params.keyword, // 可选参数，无则自动忽略
            order_by: params.order_by || 'published_at', // 默认排序字段
            sort: params.sort || 'desc', // 默认排序方式
            ...params // 扩展其他可能的参数（如签名、时间戳等）
        }
    });
};
