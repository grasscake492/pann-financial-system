package org.dao;

import org.eneity.ProxyReceiptRecords;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代领记录表数据访问接口
 * 对应数据库表: proxy_receipt_records
 * 提供代领记录数据的CRUD操作
 */
@Repository
public interface ProxyReceiptRecordsMapper {

    /**
     * 根据代领ID查询
     * @param proxyId 代领ID
     * @return 代领记录
     */
    ProxyReceiptRecords selectByProxyId(Long proxyId);

    /**
     * 根据原始用户ID查询代领记录
     * @param originalUserId 原始用户ID
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectByOriginalUser(Long originalUserId);

    /**
     * 根据代领用户ID查询代领记录
     * @param proxyUserId 代领用户ID
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectByProxyUser(Long proxyUserId);

    /**
     * 根据代领月份查询代领记录
     * @param proxyMonth 代领月份
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectByProxyMonth(String proxyMonth);

    /**
     * 根据部门ID查询代领记录
     * @param departmentId 部门ID
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectByDepartmentId(Long departmentId);

    /**
     * 查询所有代领记录
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectAll();

    /**
     * 分页查询代领记录
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> selectByPage(int offset, int limit);

    /**
     * 统计代领记录数量
     * @return 记录总数
     */
    int countProxyRecords();

    /**
     * 插入代领记录
     * @param record 代领记录对象
     * @return 影响的行数
     */
    int insertProxyRecord(ProxyReceiptRecords record);

    /**
     * 更新代领记录
     * @param record 代领记录对象
     * @return 影响的行数
     */
    int updateProxyRecord(ProxyReceiptRecords record);

    /**
     * 删除代领记录
     * @param proxyId 代领ID
     * @return 影响的行数
     */
    int deleteByProxyId(Long proxyId);

    /**
     * 根据稿费记录ID删除代领记录
     * @param feeRecordId 稿费记录ID
     * @return 影响的行数
     */
    int deleteByFeeRecordId(Long feeRecordId);
}
