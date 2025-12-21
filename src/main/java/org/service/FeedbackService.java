package org.service;

import org.dto.request.CreateFeedbackRequest;
import org.entity.Feedback;

import java.util.List;

public interface FeedbackService {

    boolean submit(CreateFeedbackRequest request);

    List<Feedback> listByUser(Long userId);

    List<Feedback> listAll();

    boolean reply(Long feedbackId, Long adminId, String replyContent);
}
