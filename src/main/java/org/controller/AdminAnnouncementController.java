package org.controller;

import org.dto.request.CreateAnnouncementRequest;
import org.dto.request.UpdateAnnouncementRequest;
import org.result.result;
import org.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    public result<?> create(@RequestBody @Valid CreateAnnouncementRequest request) {
        return announcementService.create(request)
                ? result.success("发布成功")
                : result.error("发布失败");
    }

    @PutMapping
    public result<?> update(@RequestBody @Valid UpdateAnnouncementRequest request) {
        return announcementService.update(request)
                ? result.success("更新成功")
                : result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public result<?> delete(@PathVariable Long id) {
        return announcementService.delete(id)
                ? result.success("删除成功")
                : result.error("删除失败");
    }
}
