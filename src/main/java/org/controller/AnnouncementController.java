package org.controller;

import org.entity.Announcement;
import org.result.result;
import org.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public result<List<Announcement>> listAll() {
        return result.success(announcementService.listAll());
    }

    @GetMapping("/{id}")
    public result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return announcement == null
                ? result.error("公告不存在")
                : result.success(announcement);
    }

    @GetMapping("/latest")
    public result<List<Announcement>> listLatest(@RequestParam int limit) {
        return result.success(announcementService.listLatest(limit));
    }
}
