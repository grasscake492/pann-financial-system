package org.dao;

import org.entity.Announcement;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告表数据访问接口
 * 对应数据库表: announcements
 * 提供公告数据的CRUD操作
 */
@Repository
public interface AnnouncementMapper {

    /**
     * 根据公告ID查询
     * @param announcementId 公告ID
     * @return 公告对象
     */
    Announcement selectByAnnouncementId(Long announcementId);

    /**
     * 查询所有公告
     * @return 公告列表
     */
    List<Announcement> selectAllAnnouncements();

    /**
     * 查询最新公告
     * @param limit 数量限制
     * @return 公告列表
     */
    List<Announcement> selectLatestAnnouncements(int limit);

    /**
     * 根据发布者ID查询公告
     * @param publisherId 发布者ID
     * @return 公告列表
     */
    List<Announcement> selectByPublisherId(Long publisherId);

    /**
     * 分页查询公告
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 公告列表
     */
    List<Announcement> selectByPage(int offset, int limit);

    /**
     * 统计公告数量
     * @return 公告总数
     */
    int countAnnouncements();

    /**
     * 插入公告
     * @param announcement 公告对象
     * @return 影响的行数
     */
    int insertAnnouncement(Announcement announcement);

    /**
     * 更新公告
     * @param announcement 公告对象
     * @return 影响的行数
     */
    int updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     * @param announcementId 公告ID
     * @return 影响的行数
     */
    int deleteByAnnouncementId(Long announcementId);
}
