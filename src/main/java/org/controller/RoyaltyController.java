package org.controller;

import org.constant.ResponseCode;
import org.dto.request.RoyaltyAddRequest;
import org.dto.request.RoyaltyQueryRequest;
import org.dto.request.RoyaltyUpdateRequest;
import org.dto.response.*;
import org.service.RoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 稿费管理控制器
 * 负责处理稿费相关的HTTP请求
 * @author PANN开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class RoyaltyController {

    @Autowired
    private RoyaltyService royaltyService;

    /**
     * 2.5.9 查询个人稿费
     * GET /api/v1/royalty/personal
     */
    @GetMapping("/royalty/personal")
    public ApiResponse<RoyaltyListResponse> queryPersonalRoyalty(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            // 从会话中获取当前用户ID（假设已经登录验证）
            Long userId = getCurrentUserId(request);
            if (userId == null) {
                return ApiResponse.error(ResponseCode.UNAUTHORIZED, "用户未登录");
            }

            // 构建查询请求
            RoyaltyQueryRequest queryRequest = new RoyaltyQueryRequest();
            queryRequest.setStartDate(startDate);
            queryRequest.setEndDate(endDate);
            queryRequest.setPage(page);
            queryRequest.setSize(size);

            // 调用服务
            RoyaltyListResponse result = royaltyService.queryPersonalRoyalty(userId, queryRequest);

            return ApiResponse.success(result, "查询个人稿费成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "查询个人稿费失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.10 查询部门稿费（部门管理员）
     * GET /api/v1/admin/royalty/department
     */
    @GetMapping("/admin/royalty/department")
    public ApiResponse<RoyaltyListResponse> queryDepartmentRoyalty(
            @RequestParam Long departmentId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是部门管理员
            if (!isDepartmentAdmin(request, departmentId)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "无权限访问部门稿费");
            }

            // 构建查询请求
            RoyaltyQueryRequest queryRequest = new RoyaltyQueryRequest();
            queryRequest.setStartDate(startDate);
            queryRequest.setEndDate(endDate);
            queryRequest.setPage(page);
            queryRequest.setSize(size);
            queryRequest.setUserId(userId);
            queryRequest.setDepartmentId(departmentId);

            // 调用服务
            RoyaltyListResponse result = royaltyService.queryDepartmentRoyalty(departmentId, queryRequest);

            return ApiResponse.success(result, "查询部门稿费成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "查询部门稿费失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.11 查询全部稿费（系统管理员）
     * GET /api/v1/admin/royalty/all
     */
    @GetMapping("/admin/royalty/all")
    public ApiResponse<RoyaltyListResponse> queryAllRoyalty(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long department,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是系统管理员
            if (!isSuperAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要系统管理员权限");
            }

            // 构建查询请求
            RoyaltyQueryRequest queryRequest = new RoyaltyQueryRequest();
            queryRequest.setStartDate(startDate);
            queryRequest.setEndDate(endDate);
            queryRequest.setPage(page);
            queryRequest.setSize(size);
            queryRequest.setDepartmentId(department);

            // 调用服务
            RoyaltyListResponse result = royaltyService.queryAllRoyalty(queryRequest);

            return ApiResponse.success(result, "查询全部稿费成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "查询全部稿费失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.12 添加稿费记录（管理员）
     * POST /api/v1/admin/royalty
     */
    @PostMapping("/admin/royalty")
    public ApiResponse<RoyaltyResponse> addRoyalty(@RequestBody RoyaltyAddRequest addRequest,
                                                   HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 获取管理员ID
            Long adminId = getCurrentUserId(request);

            // 调用服务
            ApiResponse<RoyaltyResponse> result = royaltyService.addRoyalty(addRequest, adminId);

            return result;

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "添加稿费记录失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.13 修改稿费记录（管理员）
     * PUT /api/v1/admin/royalty/{recordId}
     */
    @PutMapping("/admin/royalty/{recordId}")
    public ApiResponse<RoyaltyResponse> updateRoyalty(
            @PathVariable Long recordId,
            @RequestBody RoyaltyUpdateRequest updateRequest,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 获取管理员ID
            Long adminId = getCurrentUserId(request);

            // 调用服务
            ApiResponse<RoyaltyResponse> result = royaltyService.updateRoyalty(recordId, updateRequest, adminId);

            return result;

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "修改稿费记录失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.14 删除稿费记录（管理员）
     * DELETE /api/v1/admin/royalty/{recordId}
     */
    @DeleteMapping("/admin/royalty/{recordId}")
    public ApiResponse<Void> deleteRoyalty(
            @PathVariable Long recordId,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 获取管理员ID
            Long adminId = getCurrentUserId(request);

            // 调用服务（修正：服务层只需要recordId和adminId）
            ApiResponse<Void> result = royaltyService.deleteRoyalty(recordId, adminId);

            return result;

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "删除稿费记录失败: " + e.getMessage());
        }
    }

    /**
     * 2.5.15 导出稿费记录（管理员）
     * GET /api/v1/admin/royalty/export
     */
    @GetMapping("/admin/royalty/export")
    public ApiResponse<Map<String, Object>> exportRoyalty(
            @RequestParam String statisticalMonth,
            @RequestParam Long departmentId,
            @RequestParam String format,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 获取管理员ID
            Long adminId = getCurrentUserId(request);

            // 调用服务
            ApiResponse<Map<String, Object>> result = royaltyService.exportRoyalty(
                    statisticalMonth, departmentId, format, adminId);

            return result;

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "导出稿费记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户月度总稿费
     * GET /api/v1/royalty/user-monthly-total
     */
    @GetMapping("/royalty/user-monthly-total")
    public ApiResponse<BigDecimal> getUserMonthlyTotal(
            @RequestParam Long userId,
            @RequestParam String month,
            HttpServletRequest request) {

        try {
            // 权限验证：用户只能查看自己的总稿费
            Long currentUserId = getCurrentUserId(request);
            if (currentUserId == null || !currentUserId.equals(userId)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "只能查看自己的稿费总额");
            }

            // 调用服务
            BigDecimal total = royaltyService.getUserMonthlyTotal(userId, month);

            return ApiResponse.success(total, "获取月度总稿费成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "获取月度总稿费失败: " + e.getMessage());
        }
    }

    /**
     * 计算部门月度稿费总额
     * GET /api/v1/royalty/department-monthly-total
     */
    @GetMapping("/royalty/department-monthly-total")
    public ApiResponse<BigDecimal> calculateDepartmentMonthlyTotal(
            @RequestParam Long departmentId,
            @RequestParam String month,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员或该部门管理员
            if (!isAdmin(request) && !isDepartmentAdmin(request, departmentId)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "无权限访问部门稿费总额");
            }

            // 调用服务
            BigDecimal total = royaltyService.calculateDepartmentMonthlyTotal(departmentId, month);

            return ApiResponse.success(total, "获取部门月度稿费总额成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "获取部门月度稿费总额失败: " + e.getMessage());
        }
    }

    /**
     * 验证稿费记录是否可以被代领
     * GET /api/v1/royalty/can-proxy
     */
    @GetMapping("/royalty/can-proxy")
    public ApiResponse<Boolean> canProxyRoyalty(
            @RequestParam Long recordId,
            @RequestParam Long departmentId,
            @RequestParam Long originalUserId,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            boolean canProxy = royaltyService.canProxyRoyalty(recordId, departmentId, originalUserId);

            return ApiResponse.success(canProxy, "验证代领资格成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "验证代领资格失败: " + e.getMessage());
        }
    }

    /**
     * 统计系统功能 - 统计某个部门在某个月的稿费记录数
     * GET /api/v1/admin/royalty/statistics/count-by-department
     */
    @GetMapping("/admin/royalty/statistics/count-by-department")
    public ApiResponse<Integer> countRecordsByDepartmentAndMonth(
            @RequestParam Long departmentId,
            @RequestParam String month,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            Integer count = royaltyService.countRecordsByDepartmentAndMonth(departmentId, month);

            return ApiResponse.success(count, "统计部门月度记录数成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "统计部门月度记录数失败: " + e.getMessage());
        }
    }

    /**
     * 统计系统功能 - 统计某个用户在某个月的稿费记录数
     * GET /api/v1/admin/royalty/statistics/count-by-user
     */
    @GetMapping("/admin/royalty/statistics/count-by-user")
    public ApiResponse<Integer> countRecordsByUserAndMonth(
            @RequestParam Long userId,
            @RequestParam String month,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            Integer count = royaltyService.countRecordsByUserAndMonth(userId, month);

            return ApiResponse.success(count, "统计用户月度记录数成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "统计用户月度记录数失败: " + e.getMessage());
        }
    }

    /**
     * 统计系统功能 - 统计所有部门在某个月的总稿费
     * GET /api/v1/admin/royalty/statistics/departments-monthly-total
     */
    @GetMapping("/admin/royalty/statistics/departments-monthly-total")
    public ApiResponse<List<DepartmentMonthlySummaryResponse>> getAllDepartmentsMonthlyTotal(
            @RequestParam String month,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            List<DepartmentMonthlySummaryResponse> result = royaltyService.getAllDepartmentsMonthlyTotal(month);

            return ApiResponse.success(result, "获取所有部门月度总稿费成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "获取所有部门月度总稿费失败: " + e.getMessage());
        }
    }

    /**
     * 统计系统功能 - 统计所有部门在所有月份的总稿费
     * GET /api/v1/admin/royalty/statistics/summary
     */
    @GetMapping("/admin/royalty/statistics/summary")
    public ApiResponse<StatisticalSummaryResponse> getStatisticalSummary(HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            StatisticalSummaryResponse result = royaltyService.getStatisticalSummary();

            return ApiResponse.success(result, "获取统计汇总成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "获取统计汇总失败: " + e.getMessage());
        }
    }

    /**
     * 统计系统功能 - 统计稿费类型分布（按稿件类型统计）
     * GET /api/v1/admin/royalty/statistics/type-distribution
     */
    @GetMapping("/admin/royalty/statistics/type-distribution")
    public ApiResponse<List<TypeDistributionResponse>> getTypeDistribution(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long departmentId,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 调用服务
            List<TypeDistributionResponse> result = royaltyService.getTypeDistribution(month, departmentId);

            return ApiResponse.success(result, "获取类型分布成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "获取类型分布失败: " + e.getMessage());
        }
    }

    /**
     * 数据库清理功能 - 清理过期稿费记录
     * POST /api/v1/admin/royalty/cleanup
     */
    @PostMapping("/admin/royalty/cleanup")
    public ApiResponse<Map<String, Object>> cleanupExpiredRecords(
            @RequestParam(required = false) Integer years,
            HttpServletRequest request) {

        try {
            // 权限验证：必须是管理员
            if (!isAdmin(request)) {
                return ApiResponse.error(ResponseCode.PERMISSION_DENIED, "需要管理员权限");
            }

            // 获取管理员ID
            Long adminId = getCurrentUserId(request);

            // 调用服务
            ApiResponse<Map<String, Object>> result = royaltyService.cleanupExpiredRecords(years, adminId);

            return result;

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "清理过期记录失败: " + e.getMessage());
        }
    }

    // ============ 私有辅助方法 ============

    private Long getCurrentUserId(HttpServletRequest request) {
        // 从请求中获取当前用户ID（实际项目中从JWT token或session中获取）
        // 这里简化处理
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isAdmin(HttpServletRequest request) {
        // 验证是否是管理员（简化实现）
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role) || "super_admin".equals(role);
    }

    private boolean isSuperAdmin(HttpServletRequest request) {
        // 验证是否是系统管理员
        String role = request.getHeader("X-User-Role");
        return "super_admin".equals(role);
    }

    private boolean isDepartmentAdmin(HttpServletRequest request, Long departmentId) {
        // 验证是否是部门管理员
        String role = request.getHeader("X-User-Role");
        String userDepartmentId = request.getHeader("X-User-DepartmentId");

        if ("admin".equals(role) || "super_admin".equals(role)) {
            // 如果是系统管理员，可以访问任何部门
            if ("super_admin".equals(role)) {
                return true;
            }

            // 部门管理员只能访问自己部门
            if (userDepartmentId != null && !userDepartmentId.isEmpty()) {
                try {
                    Long userDeptId = Long.parseLong(userDepartmentId);
                    return userDeptId.equals(departmentId);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        return false;
    }
}