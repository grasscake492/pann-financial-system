package org.dao;

import org.eneity.Feedback;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 问题反馈表数据访问接口
 * 对应数据库表: feedbacks
 * 提供问题反馈数据的CRUD操作
 */
@Repository
public interface FeedbackMapper {

    /**
     * 根据反馈ID查询
     * @param feedbackId 反馈ID
     * @return 反馈记录
     */
    Feedback selectByFeedbackId(Long feedbackId);

    /**
     * 根据用户ID查询反馈记录
     * @param userId 用户ID
     * @return 反馈记录列表
     */
    List<Feedback> selectByUserId(Long userId);

    /**
     * 查询所有反馈记录
     * @return 反馈记录列表
     */
    List<Feedback> selectAllFeedbacks();

    /**
     * 查询待回复的反馈记录
     * @return 反馈记录列表
     */
    List<Feedback> selectPendingFeedbacks();

    /**
     * 分页查询反馈记录
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 反馈记录列表
     */
    List<Feedback> selectByPage(int offset, int limit);

    /**
     * 统计反馈记录数量
     * @return 记录总数
     */
    int countFeedbacks();

    /**
     * 插入反馈记录
     * @param feedback 反馈对象
     * @return 影响的行数
     */
    int insertFeedback(Feedback feedback);

    /**
     * 更新反馈记录
     * @param feedback 反馈对象
     * @return 影响的行数
     */
    int updateFeedback(Feedback feedback);

    /**
     * 回复反馈
     * @param feedbackId 反馈ID
     * @param replyContent 回复内容
     * @return 影响的行数
     */
    int replyFeedback(Long feedbackId, String replyContent);

    /**
     * 删除反馈记录
     * @param feedbackId 反馈ID
     * @return 影响的行数
     */
    int deleteByFeedbackId(Long feedbackId);
}
