package org.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户提交反馈请求
 */
public class CreateFeedbackRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "反馈内容不能为空")
    private String content;

    // getter / setter
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
