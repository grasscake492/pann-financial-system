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
 * 批量获取公告列表（基于2.5.27接口扩展，前端聚合）
 * @param {Object} params - 请求参数
 * @param {string[]} params.announcementIds - 公告ID数组（必填，需提前获取）
 * @param {string} params.sign - 签名（必填）
 * @returns {Promise} - 聚合后的公告列表
 */
export const batchGetAnnouncements = async (params) => {
    const { announcementIds, sign, ...restParams } = params;
    // 校验ID数组
    if (!announcementIds || !Array.isArray(announcementIds) || announcementIds.length === 0) {
        return {
            res_code: '0002',
            res_msg: '参数错误：缺少公告ID数组',
            data: { list: [] }
        };
    }

    try {
        // 并行请求所有公告详情
        const requestPromises = announcementIds.map(id =>
            getAnnouncementDetail(id, { sign, ...restParams })
        );
        const results = await Promise.all(requestPromises);

        // 过滤有效数据，提取公告信息
        const validAnnouncements = results
            .filter(res => res.res_code === '0000' && res.data?.announcementInfo)
            .map(res => res.data.announcementInfo);

        return {
            res_code: '0000',
            res_msg: '批量获取公告成功',
            data: {
                list: validAnnouncements,
                total: validAnnouncements.length
            }
        };
    } catch (error) {
        console.error('批量获取公告失败：', error);
        return {
            res_code: '0008',
            res_msg: '系统内部错误',
            data: { list: [], total: 0 }
        };
    }
}

/**
 * 获取最新的N条公告（前端筛选，基于批量接口）
 * @param {Object} params - 请求参数
 * @param {string[]} params.announcementIds - 公告ID数组（必填）
 * @param {string} params.sign - 签名（必填）
 * @param {number} [params.count=3] - 最新公告条数
 * @returns {Promise} - 最新公告列表
 */
export const fetchLatestAnnouncements = async (params) => {
    const { count = 3, ...restParams } = params;
    // 先批量获取所有公告
    const batchRes = await batchGetAnnouncements(restParams);

    if (batchRes.res_code !== '0000') {
        return batchRes;
    }

    // 前端筛选最新的N条（按发布时间降序）
    const sortedList = batchRes.data.list
        .filter(item => item.published_at) // 过滤未发布公告
        .sort((a, b) => new Date(b.published_at) - new Date(a.published_at)) // 最新在前
        .slice(0, count); // 取前N条

    return {
        res_code: '0000',
        res_msg: '获取最新公告成功',
        data: { list: sortedList }
    };
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