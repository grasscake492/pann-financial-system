package org.eneity;

import java.util.Date;

/**
 * 公告表实体类
 * 对应数据库表: announcements
 * 存储系统公告信息
 */
public class Announcement {
    private Long announcementId;   // 公告ID，主键，自增
    private String title;          // 公告标题
    private String content;        // 公告内容
    private Long publisherId;      // 发布者ID
    private Date publishedAt;      // 发布时间
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间

    public Announcement() {}

    // Getter和Setter方法
    public Long getAnnouncementId() { return announcementId; }
    public void setAnnouncementId(Long announcementId) { this.announcementId = announcementId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }

    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
