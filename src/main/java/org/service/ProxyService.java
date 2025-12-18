package org.service;

import org.entity.ProxyReceiptRecords;
import org.dto.request.ProxyAddRequest;
import org.dto.request.ProxyQueryRequest;
import org.dto.request.ProxyUpdateRequest;
import org.dto.response.ProxyListResponse;
import org.dto.response.ProxyResponse;
import org.dto.response.ApiResponse;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代领管理服务接口
 * 负责处理稿费代领相关的所有业务逻辑
 * @author PANN开发团队
 * @version 1.0
 */
public interface ProxyService {

    /**
     * 添加代领记录
     * 接口：2.5.16 代领稿费记录
     * @param request 代领添加请求
     * @param adminId 管理员ID
     * @return 添加结果响应
     */
    ApiResponse<ProxyResponse> addProxyRecord(ProxyAddRequest request, Long adminId);

    /**
     * 查询代领记录
     * 接口：2.5.17 查询代领稿费记录
     * @param request 代领查询请求
     * @param adminId 管理员ID
     * @return 代领列表响应
     */
    ProxyListResponse queryProxyRecords(ProxyQueryRequest request, Long adminId);

    /**
     * 修改代领记录
     * 接口：2.5.18 修改代领稿费记录
     * @param proxyId 代领记录ID
     * @param request 代领修改请求
     * @param adminId 管理员ID
     * @return 修改结果响应
     */
    ApiResponse<ProxyResponse> updateProxyRecord(Long proxyId, ProxyUpdateRequest request, Long adminId);

    /**
     * 撤销代领记录
     * 接口：2.5.19 撤销代领稿费记录
     * @param proxyId 代领记录ID
     * @param adminId 管理员ID
     * @return 撤销结果响应
     */
    ApiResponse<Void> revokeProxyRecord(Long proxyId, Long adminId);

    /**
     * 根据部门ID查询代领记录
     * @param departmentId 部门ID
     * @param month 代领月份（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> queryProxyByDepartment(Long departmentId, String month, int page, int size);

    /**
     * 查询用户的代领记录（作为原始用户或代领用户）
     * @param userId 用户ID
     * @param isOriginalUser 是否作为原始用户查询（true: 查询被代领记录，false: 查询代领记录）
     * @param month 代领月份（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 代领记录列表
     */
    List<ProxyReceiptRecords> queryProxyByUser(Long userId, boolean isOriginalUser, String month, int page, int size);

    /**
     * 执行代领操作
     * 包括：1. 更新稿费月表中的用户信息 2. 创建代领记录
     * @param feeRecordId 稿费记录ID
     * @param departmentId 部门ID
     * @param originalUserId 原始用户ID
     * @param proxyUserId 代领用户ID
     * @param proxyMonth 代领月份
     * @param feeAmount 稿费金额
     * @param adminId 操作管理员ID
     * @return 代领记录ID
     */
    @Transactional
    Long executeProxy(Long feeRecordId, Long departmentId, Long originalUserId,
                      Long proxyUserId, String proxyMonth, BigDecimal feeAmount, Long adminId);

    /**
     * 撤销代领操作
     * 包括：1. 恢复稿费月表中的原始用户信息 2. 删除代领记录
     * @param proxyId 代领记录ID
     * @param adminId 操作管理员ID
     * @return 是否撤销成功
     */
    boolean revokeProxy(Long proxyId, Long adminId);

    /**
     * 验证代领规则的合法性
     * 规则1：勤工助学学生不能领取任何稿费
     * 规则2：每月稿费超过800元需要代领
     * @param originalUserId 原始用户ID
     * @param proxyUserId 代领用户ID
     * @param feeAmount 稿费金额
     * @param month 统计月份
     * @return 验证结果（true: 合法，false: 不合法）
     */
    boolean validateProxyRule(Long originalUserId, Long proxyUserId, BigDecimal feeAmount, String month);

    /**
     * 检查用户是否为勤工助学学生
     * @param userId 用户ID
     * @return 是否为勤工助学学生
     */
    boolean isWorkStudyStudent(Long userId);

    /**
     * 获取用户本月已领取稿费总额
     * @param userId 用户ID
     * @param month 统计月份
     * @return 已领取稿费总额
     */
    BigDecimal getUserReceivedAmount(Long userId, String month);
}
