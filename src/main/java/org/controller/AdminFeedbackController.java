package org.controller;

import org.result.result;
import org.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public result<?> listAll() {
        return result.success(feedbackService.listAll());
    }

    @PostMapping("/reply")
    public result<?> reply(@RequestParam Long feedbackId,
                           @RequestParam Long adminId,
                           @RequestParam String replyContent) {
        return feedbackService.reply(feedbackId, adminId, replyContent)
                ? result.success("回复成功")
                : result.error("回复失败");
    }
}
