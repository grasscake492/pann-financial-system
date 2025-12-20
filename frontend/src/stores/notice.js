import { defineStore } from 'pinia'
// 导入util工具（统一入口）
import { storage, format, permission } from '@/utils/index.js'
// 导入公告接口（仅保留实际用到的）
import {
    batchGetAnnouncements,
    fetchLatestAnnouncements,
    getAnnouncementDetail,
    publishAnnouncement,
    updateAnnouncement,
    deleteAnnouncement
} from '@/api/announcement'
import {submitFeedback} from "@/api/feedback.js";

export const useNoticeStore = defineStore('notice', {
    state: () => ({
        // 公告相关（清空默认占位，接口返回后赋值）
        announcementList: [], // 所有公告列表
        currentAnnouncement: {}, // 当前查看的公告详情
        announcementTotal: 0, // 公告总条数
        announcementCurrentPage: 1, // 公告当前页码（分页用）

        // 反馈相关（与feedbacks表、反馈接口返回字段一致）
        feedbackList: [],
        pendingFeedbackList: [], // 待处理反馈列表（管理员专用）
        currentFeedback: {}, // 当前查看的反馈详情
        feedbackTotal: 0, // 反馈总条数（分页用）
        feedbackCurrentPage: 1 // 反馈当前页码（分页用）
    }),
    getters: {
        /**
         * 获取最新的N条公告（响应式，数据变化自动更新）
         * @param {number} count - 要获取的公告条数，默认3
         * @returns {Array} 最新的公告列表
         */
        latestAnnouncements: (state) => (count = 3) => {
            return state.announcementList
                .filter(item => item.published_at) // 过滤无发布时间的公告
                .sort((a, b) => new Date(b.published_at) - new Date(a.published_at)) // 最新在前
                .slice(0, count); // 取前count条
        },
        /**
         * 获取最新的1条公告（简化调用）
         * @returns {Object} 最新公告详情
         */
        newestAnnouncement: (state) => {
            return state.latestAnnouncements(1)[0] || {};
        }
    },
    actions: {
        /**
         * 1. 查所有公告（核心修改：去掉ID数组必填，适配“查全部”逻辑）
         * @param {Object} params - 请求参数（仅传分页参数，如page/size，无需ID）
         */
        async fetchAllAnnouncements(params = {}) { // 加params兜底，避免传空
            try {
                // 生成签名（只拼分页参数，不用ID）
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...params
                });

                // 关键修改：调用「查全部公告」的接口（替换成你接口文档里的“查全部”接口名，比如getAllAnnouncements）
                const res = await getAllAnnouncements({ // 原先是batchGetAnnouncements（批量查ID），现在换成查全部的接口
                    ...params,
                    sign
                });

                // 接口成功：更新公告列表
                if (res.res_code === '0000') {
                    this.announcementList = res.data.list || [];
                    this.announcementTotal = res.data.total || 0;
                } else {
                    console.error('获取所有公告失败：', res.res_msg);
                }
            } catch (error) {
                console.error('请求所有公告异常：', error);
            }
        },

        /**
         * 2. 查最新N条公告（核心修改：去掉ID必填+改掉自调用+适配“查最新”逻辑）
         * @param {Object} params - 请求参数
         * @param {number} [params.count=3] - 最新公告条数（默认查3条）
         */
        async fetchLatestAnnouncements(params = {}) { // 加params兜底
            try {
                // 兜底默认条数：没传count就查3条
                const queryParams = { count: 3, ...params };
                // 生成签名（只拼条数，不用ID）
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...queryParams
                });

                // 关键修改1：改掉“自己调用自己”的错误，调用「查最新公告」的专属接口（比如getLatestAnnouncements）
                const res = await getLatestAnnouncements({
                    ...queryParams,
                    sign
                });

                // 接口成功返回最新公告列表
                if (res.res_code === '0000') {
                    return res.data.list || [];
                }
                return [];
            } catch (error) {
                console.error('获取最新公告失败：', error);
                return [];
            }
        },

        /**
         * 3. 查单个公告详情（保留不变，本来就需要ID）
         * @param {string} announcementId - 公告ID（必填）
         */
        async fetchAnnouncementDetail(announcementId) {
            try {
                const sign = format.generateSign({
                    timestamp: Date.now()
                });

                const res = await getAnnouncementDetail(announcementId, { sign });
                if (res.res_code === '0000') {
                    this.currentAnnouncement = res.data.announcementInfo || {};
                } else {
                    console.error('获取公告详情失败：', res.res_msg);
                }
            } catch (error) {
                console.error('获取公告详情异常：', error);
            }
        },

// 额外补充：保留“批量查指定ID公告”（如果需要），明确标注需要ID
        async fetchBatchAnnouncements(params = {}) {
            // 加参数校验：没传ID数组就提示，不调接口
            if (!Array.isArray(params.announcementIds) || params.announcementIds.length === 0) {
                console.error('批量查公告失败：缺少公告ID数组');
                return;
            }
            try {
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...params
                });
                const res = await batchGetAnnouncements({...params, sign});
                if (res.res_code === '0000') {
                    this.batchAnnouncementList = res.data.list || [];
                } else {
                    console.error('批量查公告失败：', res.res_msg);
                }
            } catch (error) {
                console.error('批量查公告异常：', error);
            }
        },
        /**
         * 管理员发布公告
         * @param {Object} params - 发布参数（title/content/publisher_id）
         * @returns {Promise<boolean>} 是否发布成功
         */
        async publishAnnouncement(params) {
            try {
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...params
                });

                const res = await publishAnnouncement({ ...params, sign });
                if (res.res_code === '0000') {
                    // 发布成功后刷新公告列表
                    return true;
                } else {
                    console.error('发布公告失败：', res.res_msg);
                    return false;
                }
            } catch (error) {
                console.error('发布公告异常：', error);
                return false;
            }
        },

        /**
         * 管理员修改公告
         * @param {string} announcementId - 公告ID
         * @param {Object} params - 修改参数（title/content/publisher_id）
         * @returns {Promise<boolean>} 是否修改成功
         */
        async updateAnnouncement(announcementId, params) {
            try {
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...params
                });

                const res = await updateAnnouncement(announcementId, { ...params, sign });
                if (res.res_code === '0000') {
                    // 修改成功后刷新当前公告详情
                    await this.fetchAnnouncementDetail(announcementId);
                    return true;
                } else {
                    console.error('修改公告失败：', res.res_msg);
                    return false;
                }
            } catch (error) {
                console.error('修改公告异常：', error);
                return false;
            }
        },

        /**
         * 管理员删除公告
         * @param {string} announcementId - 公告ID
         * @param {string[]} announcementIds - 所有公告ID数组（删除后刷新列表用）
         * @returns {Promise<boolean>} 是否删除成功
         */
        async deleteAnnouncement(announcementId, announcementIds) {
            try {
                const sign = format.generateSign({
                    timestamp: Date.now()
                });

                const res = await deleteAnnouncement(announcementId, { sign });
                if (res.res_code === '0000') {
                    // 删除成功后刷新公告列表
                    const newIds = announcementIds.filter(id => id !== announcementId);
                    await this.fetchAllAnnouncements({ announcementIds: newIds });
                    return true;
                } else {
                    console.error('删除公告失败：', res.res_msg);
                    return false;
                }
            } catch (error) {
                console.error('删除公告异常：', error);
                return false;
            }
        },

        // 公告相关基础操作（补全空方法）
        setAnnouncementList(list) {
            this.announcementList = list;
        },
        setCurrentAnnouncement(announcement) {
            this.currentAnnouncement = announcement;
        },
        setAnnouncementCurrentPage(page) {
            this.announcementCurrentPage = page;
        },

        // 反馈相关操作（后续按需完善）
        setFeedbackList(list) {
            this.feedbackList = list || []; // 设置用户反馈列表
        },
        setPendingFeedbackList(list) {
            this.pendingFeedbackList = list || []; // 管理员待处理反馈（暂不改动，保留）
        },
        setCurrentFeedback(feedback) {
            this.currentFeedback = feedback || {}; // 设置当前查看的反馈详情
        },
        addFeedback(feedback) {
            // 手动添加反馈（备用：比如接口调用前临时加）
            if (feedback) this.feedbackList.unshift(feedback);
        },
        replyFeedback() {}, // 管理员回复反馈（暂不改动，保留）
        updateFeedbackStatus() {}, // 管理员更新反馈状态（暂不改动，保留）
        setFeedbackCurrentPage(page) {
            this.feedbackCurrentPage = page || 1; // 设置反馈分页页码
        },
        /**
         * 提交用户反馈（核心：用户稿费反馈功能）
         * @param {Object} params - 反馈参数（user_id + content 为必填）
         * @returns {Promise<boolean>} 是否提交成功
         */
        async submitUserFeedback(params) {
            try {
                // 1. 参数校验：必填项兜底
                if (!params.user_id || !params.content) {
                    console.error('提交反馈失败：缺少用户ID或反馈内容');
                    return false;
                }
                // 2. 生成签名（复用原有format工具，和公告接口签名规则一致）
                const sign = format.generateSign({
                    timestamp: Date.now(),
                    ...params
                });
                // 3. 调用提交反馈接口
                const res = await submitFeedback({ ...params, sign });
                // 4. 接口成功：更新反馈列表（把新反馈加到列表首位）
                if (res.res_code === '0000') {
                    const newFeedback = {
                        ...res.data, // 接口返回的反馈ID、提交时间等
                        ...params,   // 反馈内容、用户ID
                        create_time: new Date().toString() // 兜底时间
                    };
                    this.feedbackList.unshift(newFeedback); // 新增反馈放列表顶部
                    this.feedbackTotal = this.feedbackList.length; // 更新总条数
                    return true;
                } else {
                    console.error('提交反馈失败：', res.res_msg);
                    return false;
                }
            } catch (error) {
                console.error('提交反馈异常：', error);
                return false;
            }
        },
        // 清空公告/反馈缓存
        clearNoticeInfo() {
            this.announcementList = [];
            this.currentAnnouncement = {};
            this.announcementTotal = 0;
            this.feedbackList = [];
            this.currentFeedback = {};
            this.feedbackTotal = 0;
        }
    }
});