package org.service.impl;

import org.dao.AnnouncementMapper;
import org.dto.request.CreateAnnouncementRequest;
import org.dto.request.UpdateAnnouncementRequest;
import org.entity.Announcement;
import org.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Announcement getById(Long announcementId) {
        return announcementMapper.selectByAnnouncementId(announcementId);
    }

    @Override
    public List<Announcement> listAll() {
        return announcementMapper.selectAllAnnouncements();
    }

    @Override
    public List<Announcement> listLatest(int limit) {
        return announcementMapper.selectLatestAnnouncements(limit);
    }

    @Override
    public List<Announcement> listByPage(int page, int size) {
        int offset = (page - 1) * size;
        return announcementMapper.selectByPage(offset, size);
    }

    @Override
    public boolean create(CreateAnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setPublisherId(request.getPublisherId());
        announcement.setCreatedAt(new Date());
        announcement.setUpdatedAt(new Date());
        return announcementMapper.insertAnnouncement(announcement) > 0;
    }

    @Override
    public boolean update(UpdateAnnouncementRequest request) {
        Announcement announcement = announcementMapper
                .selectByAnnouncementId(request.getAnnouncementId());

        if (announcement == null) {
            return false;
        }

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setUpdatedAt(new Date());
        return announcementMapper.updateAnnouncement(announcement) > 0;
    }

    @Override
    public boolean delete(Long announcementId) {
        return announcementMapper.deleteByAnnouncementId(announcementId) > 0;
    }
}
