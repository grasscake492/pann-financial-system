package org.dao;

import org.entity.FeeMonthlySummary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 稿费月汇总表数据访问接口
 * 对应数据库表: fee_monthly_summary
 * 提供稿费汇总数据的CRUD操作
 */
@Repository
public interface FeeMonthlySummaryMapper {

    /**
     * 根据汇总ID查询
     * @param summaryId 汇总ID
     * @return 汇总记录
     */
    FeeMonthlySummary selectBySummaryId(Long summaryId);

    /**
     * 根据用户ID和月份查询汇总
     * @param userId 用户ID
     * @param month 月份
     * @return 汇总记录
     */
    FeeMonthlySummary selectByUserAndMonth(Long userId, String month);

    /**
     * 根据月份查询所有汇总记录
     * @param month 月份
     * @return 汇总记录列表
     */
    List<FeeMonthlySummary> selectByMonth(String month);

    /**
     * 根据部门ID和月份查询汇总记录
     * @param departmentId 部门ID
     * @param month 月份
     * @return 汇总记录列表
     */
    List<FeeMonthlySummary> selectByDepartmentAndMonth(Long departmentId, String month);

    /**
     * 查询用户所有月份的汇总记录
     * @param userId 用户ID
     * @return 汇总记录列表
     */
    List<FeeMonthlySummary> selectByUser(Long userId);

    /**
     * 插入汇总记录
     * @param summary 汇总记录对象
     * @return 影响的行数
     */
    int insertSummary(FeeMonthlySummary summary);

    /**
     * 更新汇总记录
     * @param summary 汇总记录对象
     * @return 影响的行数
     */
    int updateSummary(FeeMonthlySummary summary);

    /**
     * 删除汇总记录
     * @param summaryId 汇总ID
     * @return 影响的行数
     */
    int deleteBySummaryId(Long summaryId);

    /**
     * 根据用户ID和月份删除汇总记录
     * @param userId 用户ID
     * @param month 月份
     * @return 影响的行数
     */
    int deleteByUserAndMonth(Long userId, String month);

    /**
     * 统计某个月份的汇总记录数量
     * @param month 月份
     * @return 记录数量
     */
    int countByMonth(String month);
}