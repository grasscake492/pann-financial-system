package org.service;

import org.dto.request.RoyaltyQueryRequest;
import org.dto.request.RoyaltyAddRequest;
import org.dto.request.RoyaltyUpdateRequest;
import org.dto.response.RoyaltyListResponse;
import org.dto.response.RoyaltyResponse;
import org.dto.response.ApiResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 稿费管理服务接口
 * 负责处理稿费相关的所有业务逻辑
 * @author PANN开发团队
 * @version 1.0
 */
public interface RoyaltyService {

    /**
     * 查询个人稿费
     * 接口：2.5.9 查询个人稿费
     * @param userId 用户ID
     * @param request 查询请求参数（开始日期、结束日期、分页）
     * @return 稿费列表响应
     */
    RoyaltyListResponse queryPersonalRoyalty(Long userId, RoyaltyQueryRequest request);

    /**
     * 查询部门稿费（部门管理员使用）
     * 接口：2.5.10 查询部门稿费
     * @param departmentId 部门ID
     * @param request 查询请求参数
     * @return 稿费列表响应
     */
    RoyaltyListResponse queryDepartmentRoyalty(Long departmentId, RoyaltyQueryRequest request);

    /**
     * 查询全部稿费（系统管理员使用）
     * 接口：2.5.11 查询全部稿费
     * @param request 查询请求参数
     * @return 稿费列表响应
     */
    RoyaltyListResponse queryAllRoyalty(RoyaltyQueryRequest request);

    /**
     * 添加稿费记录
     * 接口：2.5.12 添加稿费记录
     * @param request 添加稿费请求
     * @param adminId 管理员ID
     * @return 添加结果响应
     */
    ApiResponse<RoyaltyResponse> addRoyalty(RoyaltyAddRequest request, Long adminId);

    /**
     * 修改稿费记录
     * 接口：2.5.13 修改稿费记录
     * @param recordId 记录ID
     * @param request 修改稿费请求
     * @param adminId 管理员ID
     * @return 修改结果响应
     */
    ApiResponse<RoyaltyResponse> updateRoyalty(Long recordId, RoyaltyUpdateRequest request, Long adminId);

    /**
     * 删除稿费记录
     * 接口：2.5.14 删除稿费记录
     * @param recordId 记录ID
     * @param adminId 管理员ID
     * @return 删除结果响应
     */
    ApiResponse<Void> deleteRoyalty(Long recordId, Long adminId);

    /**
     * 导出稿费记录
     * 接口：2.5.15 导出稿费记录
     * @param statisticalMonth 统计月份 (格式：YYYY-MM)
     * @param departmentId 部门ID
     * @param format 导出格式 (Excel/PDF)
     * @param adminId 管理员ID
     * @return 导出结果响应
     */
    ApiResponse<Map<String, Object>> exportRoyalty(String statisticalMonth, Long departmentId, String format, Long adminId);

    /**
     * 获取用户月度总稿费
     * 用于代领规则校验
     * @param userId 用户ID
     * @param month 统计月份 (格式：YYYY-MM)
     * @return 月度总金额
     */
    BigDecimal getUserMonthlyTotal(Long userId, String month);

    /**
     * 统计部门月度稿费总额
     * @param departmentId 部门ID
     * @param month 统计月份
     * @return 部门月度总额
     */
    BigDecimal calculateDepartmentMonthlyTotal(Long departmentId, String month);

    /**
     * 验证稿费记录是否可以被代领
     * 根据代领规则：勤工助学学生不能领取，每月超过800元需要代领
     * @param recordId 稿费记录ID
     * @param departmentId 部门ID
     * @param originalUserId 原始用户ID
     * @return 是否可以代领
     */
    boolean canProxyRoyalty(Long recordId, Long departmentId, Long originalUserId);
}