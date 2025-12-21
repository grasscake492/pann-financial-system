package org.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 管理员回复反馈请求对象
 * 用于 AdminFeedbackController -> FeedbackService
 */
public class ReplyFeedbackRequest {

    @NotNull(message = "反馈ID不能为空")
    private Long feedbackId;

    @NotBlank(message = "回复内容不能为空")
    private String replyContent;

    @NotNull(message = "回复管理员ID不能为空")
    private Long adminId;

    // getter / setter

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
