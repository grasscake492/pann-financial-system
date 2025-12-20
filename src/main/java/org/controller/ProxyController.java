// src/main/java/org/controller/ProxyController.java
package org.controller;

import org.dto.request.ProxyAddRequest;
import org.dto.request.ProxyQueryRequest;
import org.dto.request.ProxyUpdateRequest;
import org.dto.response.ApiResponse;
import org.dto.response.ProxyListResponse;
import org.dto.response.ProxyResponse;
import org.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/proxy")
@Validated
public class ProxyController {

    @Autowired
    private ProxyService proxyService;

    /**
     * 获取当前管理员ID（从安全上下文或token中获取）
     * 这里需要根据你的安全框架实现
     */
    private Long getCurrentAdminId() {
        // TODO: 根据你的安全框架实现
        // 例如从JWT token或SecurityContext中获取当前用户ID
        // 这里假设你能获取到当前登录的管理员ID
        return 1L; // 临时返回固定值，需要替换为实际实现
    }

    /**
     * 2.5.16 添加代领记录（运营部管理员）
     * POST /api/v1/admin/proxy
     */
    @PostMapping
    public ApiResponse<ProxyResponse> addProxyRecord(@Valid @RequestBody ProxyAddRequest request) {
        try {
            Long adminId = getCurrentAdminId();
            ApiResponse<ProxyResponse> response = proxyService.addProxyRecord(request, adminId);
            return response;
        } catch (RuntimeException e) {
            return ApiResponse.error("0002", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("0008", "系统内部错误");
        }
    }

    /**
     * 2.5.17 查询代领记录（运营部管理员）
     * GET /api/v1/admin/proxy/list
     */
    @GetMapping("/list")
    public ApiResponse<ProxyListResponse> queryProxyRecords(@Valid ProxyQueryRequest request) {
        try {
            Long adminId = getCurrentAdminId();
            ProxyListResponse response = proxyService.queryProxyRecords(request, adminId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("0008", "系统内部错误");
        }
    }

    /**
     * 2.5.18 修改代领记录（运营部管理员）
     * PUT /api/v1/admin/proxy/{proxyId}
     */
    @PutMapping("/{proxyId}")
    public ApiResponse<ProxyResponse> updateProxyRecord(
            @PathVariable Long proxyId,
            @Valid @RequestBody ProxyUpdateRequest request) {
        try {
            Long adminId = getCurrentAdminId();

            // 由于ProxyUpdateRequest没有proxyId字段，我们只能使用路径参数
            ApiResponse<ProxyResponse> response = proxyService.updateProxyRecord(proxyId, request, adminId);
            return response;
        } catch (RuntimeException e) {
            return ApiResponse.error("0002", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("0008", "系统内部错误");
        }
    }

    /**
     * 2.5.19 撤销代领记录（运营部管理员）
     * DELETE /api/v1/admin/proxy/{proxyId}
     */
    @DeleteMapping("/{proxyId}")
    public ApiResponse<Void> revokeProxyRecord(@PathVariable Long proxyId) {
        try {
            Long adminId = getCurrentAdminId();
            ApiResponse<Void> response = proxyService.revokeProxyRecord(proxyId, adminId);
            return response;
        } catch (RuntimeException e) {
            return ApiResponse.error("0002", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("0008", "系统内部错误");
        }
    }

    /**
     * 获取代领记录详情
     * GET /api/v1/admin/proxy/{proxyId}
     * 注意：由于ProxyService没有getProxyDetail方法，我们需要通过其他方式实现
     * 例如通过查询接口获取单个记录，但这需要修改查询条件
     * 这里暂时不实现，或者你可以通过下面的方式实现：
     */
    @GetMapping("/{proxyId}")
    public ApiResponse<ProxyResponse> getProxyDetail(@PathVariable Long proxyId) {
        try {
            // 由于ProxyService没有提供直接查询单条记录的方法，
            // 这里有两种处理方式：

            // 方式1：返回错误提示
            return ApiResponse.error("0009", "该功能暂未实现，请使用查询列表接口");

            // 方式2：如果需要实现，可以创建一个新的查询请求，然后从结果中过滤
            /*
            Long adminId = getCurrentAdminId();
            ProxyQueryRequest queryRequest = new ProxyQueryRequest();
            queryRequest.setProxyId(proxyId); // 假设ProxyQueryRequest有proxyId字段
            ProxyListResponse listResponse = proxyService.queryProxyRecords(queryRequest, adminId);

            if (listResponse != null && listResponse.getRecords() != null && !listResponse.getRecords().isEmpty()) {
                return ApiResponse.success(listResponse.getRecords().get(0));
            } else {
                return ApiResponse.error("0004", "未找到该代领记录");
            }
            */
        } catch (RuntimeException e) {
            return ApiResponse.error("0002", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("0008", "系统内部错误");
        }
    }
}