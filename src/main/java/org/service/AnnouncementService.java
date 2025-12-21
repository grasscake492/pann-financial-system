package org.service;

import org.dto.request.CreateAnnouncementRequest;
import org.dto.request.UpdateAnnouncementRequest;
import org.entity.Announcement;

import java.util.List;

public interface AnnouncementService {

    Announcement getById(Long announcementId);

    List<Announcement> listAll();

    List<Announcement> listLatest(int limit);

    List<Announcement> listByPage(int page, int size);

    boolean create(CreateAnnouncementRequest request);

    boolean update(UpdateAnnouncementRequest request);

    boolean delete(Long announcementId);
}
