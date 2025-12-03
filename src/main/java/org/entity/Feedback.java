package org.entity;

import java.util.Date;

/**
 * 问题反馈表实体类
 * 对应数据库表: feedbacks
 * 存储用户问题反馈信息
 */
public class Feedback {
    private Long feedbackId;       // 反馈ID，主键，自增
    private Long userId;           // 用户ID
    private String content;        // 反馈内容
    private String replyContent;   // 回复内容
    private Date repliedAt;        // 回复时间
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public Feedback() {}

    // Getter和Setter方法
    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Date getRepliedAt() { return repliedAt; }
    public void setRepliedAt(Date repliedAt) { this.repliedAt = repliedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
