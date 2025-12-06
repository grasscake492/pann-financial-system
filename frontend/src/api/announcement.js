import request from '@/utils/request';

/**
 * 获取单条公告详情（接口文档2.5.27节）
 * @param {Number} announcementId - 公告ID
 * @returns {Promise}
 */
export const getAnnouncementDetail = (announcementId) => {
    return request({
        // 注意：若baseURL已配http://pannfmis，这里写/api/v1/announcements/${announcementId}
        // 若baseURL是/api（前端代理），需确保代理转发到http://pannfmis
        url: `/api/v1/announcements/${announcementId}`,
        method: 'GET',
    });
};

/**
 * 公告列表接口（补充，需和后端确认）
 * @returns {Promise}
 */
export const getAnnouncementList = () => {
    return request({
        url: '/api/v1/announcements',
        method: 'GET',
    });
};