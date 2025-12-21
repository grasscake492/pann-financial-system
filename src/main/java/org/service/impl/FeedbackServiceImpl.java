package org.service.impl;

import org.dao.FeedbackMapper;
import org.dto.request.CreateFeedbackRequest;
import org.entity.Feedback;
import org.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public boolean submit(CreateFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(request.getUserId());
        feedback.setContent(request.getContent());
        feedback.setCreatedAt(new Date());
        feedback.setUpdatedAt(new Date());
        return feedbackMapper.insertFeedback(feedback) > 0;
    }

    @Override
    public List<Feedback> listByUser(Long userId) {
        return feedbackMapper.selectByUserId(userId);
    }

    @Override
    public List<Feedback> listAll() {
        return feedbackMapper.selectAllFeedbacks();
    }

    @Override
    public boolean reply(Long feedbackId, Long adminId, String replyContent) {
        Feedback feedback = feedbackMapper.selectByFeedbackId(feedbackId);
        if (feedback == null) {
            return false;
        }

        // 当前实体不保存 adminId，只保存回复内容和时间
        feedback.setReplyContent(replyContent);
        feedback.setRepliedAt(new Date());
        feedback.setUpdatedAt(new Date());

        return feedbackMapper.updateFeedback(feedback) > 0;
    }
}
