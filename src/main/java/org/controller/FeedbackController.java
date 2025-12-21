package org.controller;

import org.dto.request.CreateFeedbackRequest;
import org.entity.Feedback;
import org.result.result;
import org.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public result<?> submit(@RequestBody @Valid CreateFeedbackRequest request) {
        return feedbackService.submit(request)
                ? result.success("反馈已提交")
                : result.error("提交失败");
    }

    @GetMapping("/my")
    public result<List<Feedback>> myFeedback(@RequestParam Long userId) {
        return result.success(feedbackService.listByUser(userId));
    }
}
