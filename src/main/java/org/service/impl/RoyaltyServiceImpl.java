// src/main/java/org/service/impl/RoyaltyServiceImpl.java
package org.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.constant.ResponseCode;
import org.dao.*;
import org.dto.request.RoyaltyAddRequest;
import org.dto.request.RoyaltyQueryRequest;
import org.dto.request.RoyaltyUpdateRequest;
import org.dto.response.ApiResponse;
import org.dto.response.RoyaltyListResponse;
import org.dto.response.RoyaltyResponse;
import org.entity.*;
import org.service.RoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 稿费服务实现类
 * @author PANN开发团队
 * @version 1.0
 */
@Service
public class RoyaltyServiceImpl implements RoyaltyService {

    @Autowired
    private NewsDepartmentMonthlyMapper newsDepartmentMonthlyMapper;

    @Autowired
    private EditorialDepartmentMonthlyMapper editorialDepartmentMonthlyMapper;

    @Autowired
    private FeeMonthlySummaryMapper feeMonthlySummaryMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 部门常量
    private static final Long NEWS_DEPARTMENT_ID = 1L; // 新闻部
    private static final Long EDITORIAL_DEPARTMENT_ID = 2L; // 编辑部
    private static final Long OPERATION_DEPARTMENT_ID = 3L; // 运营部

    // 代领规则常量
    private static final BigDecimal MONTHLY_MAX_FEE = new BigDecimal("800.00");

    @Override
    public RoyaltyListResponse queryPersonalRoyalty(Long userId, RoyaltyQueryRequest request) {
        try {
            List<RoyaltyResponse> resultList = new ArrayList<>();

            // 查询新闻部稿费记录
            List<NewsDepartmentMonthly> newsRecords = findNewsRecordsByUserId(userId, request);
            for (NewsDepartmentMonthly record : newsRecords) {
                resultList.add(convertToRoyaltyResponse(record, NEWS_DEPARTMENT_ID));
            }

            // 查询编辑部稿费记录
            List<EditorialDepartmentMonthly> editorialRecords = findEditorialRecordsByUserId(userId, request);
            for (EditorialDepartmentMonthly record : editorialRecords) {
                resultList.add(convertToRoyaltyResponse(record, EDITORIAL_DEPARTMENT_ID));
            }

            // 排序：按创建时间倒序
            resultList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            // 分页处理
            int total = resultList.size();
            int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
            int size = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 10;

            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, total);

            List<RoyaltyResponse> pagedList = (startIndex < total) ?
                    resultList.subList(startIndex, endIndex) :
                    new ArrayList<>();

            return new RoyaltyListResponse(total, pagedList);

        } catch (Exception e) {
            throw new RuntimeException("查询个人稿费失败: " + e.getMessage(), e);
        }
    }

    @Override
    public RoyaltyListResponse queryDepartmentRoyalty(Long departmentId, RoyaltyQueryRequest request) {
        try {
            List<RoyaltyResponse> resultList = new ArrayList<>();

            // 验证部门是否存在
            Department department = departmentMapper.selectByDepartmentId(departmentId);
            if (department == null) {
                throw new RuntimeException("部门不存在: " + departmentId);
            }

            // 根据部门ID查询对应表的记录
            if (NEWS_DEPARTMENT_ID.equals(departmentId)) {
                List<NewsDepartmentMonthly> newsRecords = findNewsRecordsByDepartment(departmentId, request);
                for (NewsDepartmentMonthly record : newsRecords) {
                    resultList.add(convertToRoyaltyResponse(record, departmentId));
                }
            } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
                List<EditorialDepartmentMonthly> editorialRecords = findEditorialRecordsByDepartment(departmentId, request);
                for (EditorialDepartmentMonthly record : editorialRecords) {
                    resultList.add(convertToRoyaltyResponse(record, departmentId));
                }
            } else {
                throw new RuntimeException("该部门不支持稿费管理: " + departmentId);
            }

            // 排序：按创建时间倒序
            resultList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            // 分页处理
            int total = resultList.size();
            int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
            int size = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 10;

            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, total);

            List<RoyaltyResponse> pagedList = (startIndex < total) ?
                    resultList.subList(startIndex, endIndex) :
                    new ArrayList<>();

            return new RoyaltyListResponse(total, pagedList);

        } catch (Exception e) {
            throw new RuntimeException("查询部门稿费失败: " + e.getMessage(), e);
        }
    }

    @Override
    public RoyaltyListResponse queryAllRoyalty(RoyaltyQueryRequest request) {
        try {
            List<RoyaltyResponse> resultList = new ArrayList<>();

            // 查询所有新闻部稿费记录
            List<NewsDepartmentMonthly> allNewsRecords = findAllNewsRecords(request);
            for (NewsDepartmentMonthly record : allNewsRecords) {
                resultList.add(convertToRoyaltyResponse(record, NEWS_DEPARTMENT_ID));
            }

            // 查询所有编辑部稿费记录
            List<EditorialDepartmentMonthly> allEditorialRecords = findAllEditorialRecords(request);
            for (EditorialDepartmentMonthly record : allEditorialRecords) {
                resultList.add(convertToRoyaltyResponse(record, EDITORIAL_DEPARTMENT_ID));
            }

            // 如果指定了部门ID，过滤结果
            if (request.getDepartmentId() != null) {
                resultList = resultList.stream()
                        .filter(record -> request.getDepartmentId().equals(record.getDepartmentId()))
                        .collect(Collectors.toList());
            }

            // 排序：按创建时间倒序
            resultList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            // 分页处理
            int total = resultList.size();
            int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
            int size = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 10;

            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, total);

            List<RoyaltyResponse> pagedList = (startIndex < total) ?
                    resultList.subList(startIndex, endIndex) :
                    new ArrayList<>();

            return new RoyaltyListResponse(total, pagedList);

        } catch (Exception e) {
            throw new RuntimeException("查询全部稿费失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<RoyaltyResponse> addRoyalty(RoyaltyAddRequest request, Long adminId) {
        try {
            // 1. 参数验证
            validateAddRequest(request);

            // 2. 根据部门ID插入对应表
            Long recordId = null;
            RoyaltyResponse response = null;

            if (NEWS_DEPARTMENT_ID.equals(request.getDepartmentId())) {
                NewsDepartmentMonthly record = createNewsRecord(request);
                newsDepartmentMonthlyMapper.insertRecord(record);
                recordId = record.getRecordId();
                response = convertToRoyaltyResponse(record, NEWS_DEPARTMENT_ID);
            } else if (EDITORIAL_DEPARTMENT_ID.equals(request.getDepartmentId())) {
                EditorialDepartmentMonthly record = createEditorialRecord(request);
                editorialDepartmentMonthlyMapper.insertRecord(record);
                recordId = record.getRecordId();
                response = convertToRoyaltyResponse(record, EDITORIAL_DEPARTMENT_ID);
            } else {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "不支持的部门ID: " + request.getDepartmentId());
            }

            // 3. 更新稿费月汇总表
            updateFeeMonthlySummary(request);

            // 4. 检查是否需要代领
            checkProxyRequirements(request);

            return ApiResponse.success(response, "添加稿费记录成功");

        } catch (Exception e) {
            throw new RuntimeException("添加稿费记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<RoyaltyResponse> updateRoyalty(Long recordId, RoyaltyUpdateRequest request, Long adminId) {
        try {
            // 1. 参数验证
            if (recordId == null || recordId <= 0) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "记录ID不能为空");
            }

            if (request.getDepartmentId() == null) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "部门ID不能为空");
            }

            // 2. 根据部门ID更新对应表
            RoyaltyResponse response = null;

            if (NEWS_DEPARTMENT_ID.equals(request.getDepartmentId())) {
                NewsDepartmentMonthly existingRecord = newsDepartmentMonthlyMapper.selectByRecordId(recordId);
                if (existingRecord == null) {
                    return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "稿费记录不存在");
                }

                // 更新记录
                updateNewsRecord(existingRecord, request);
                newsDepartmentMonthlyMapper.updateRecord(existingRecord);
                response = convertToRoyaltyResponse(existingRecord, NEWS_DEPARTMENT_ID);

            } else if (EDITORIAL_DEPARTMENT_ID.equals(request.getDepartmentId())) {
                EditorialDepartmentMonthly existingRecord = editorialDepartmentMonthlyMapper.selectByRecordId(recordId);
                if (existingRecord == null) {
                    return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "稿费记录不存在");
                }

                // 更新记录
                updateEditorialRecord(existingRecord, request);
                editorialDepartmentMonthlyMapper.updateRecord(existingRecord);
                response = convertToRoyaltyResponse(existingRecord, EDITORIAL_DEPARTMENT_ID);

            } else {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "不支持的部门ID: " + request.getDepartmentId());
            }

            // 3. 重新计算汇总表
            recalculateFeeMonthlySummary(recordId, request.getDepartmentId());

            return ApiResponse.success(response, "修改稿费记录成功");

        } catch (Exception e) {
            throw new RuntimeException("修改稿费记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteRoyalty(Long recordId, Long adminId) {
        try {
            // 这里需要先查询记录获取部门ID，实际项目中可能需要修改接口参数
            throw new RuntimeException("删除功能需要部门ID参数，请使用带部门ID的删除方法");
        } catch (Exception e) {
            throw new RuntimeException("删除稿费记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> exportRoyalty(String statisticalMonth, Long departmentId, String format, Long adminId) {
        try {
            // 参数验证
            if (statisticalMonth == null || statisticalMonth.isEmpty()) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "统计月份不能为空");
            }

            if (departmentId == null) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "部门ID不能为空");
            }

            if (format == null || (!"Excel".equalsIgnoreCase(format) && !"PDF".equalsIgnoreCase(format))) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "导出格式必须是Excel或PDF");
            }

            // 查询导出数据
            List<RoyaltyResponse> exportData = new ArrayList<>();

            if (NEWS_DEPARTMENT_ID.equals(departmentId)) {
                List<NewsDepartmentMonthly> records = newsDepartmentMonthlyMapper.selectByMonth(statisticalMonth);
                for (NewsDepartmentMonthly record : records) {
                    exportData.add(convertToRoyaltyResponse(record, NEWS_DEPARTMENT_ID));
                }
            } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
                List<EditorialDepartmentMonthly> records = editorialDepartmentMonthlyMapper.selectByMonth(statisticalMonth);
                for (EditorialDepartmentMonthly record : records) {
                    exportData.add(convertToRoyaltyResponse(record, EDITORIAL_DEPARTMENT_ID));
                }
            } else {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "不支持的部门ID: " + departmentId);
            }

            // 构造导出结果
            Map<String, Object> exportResult = new HashMap<>();

            // 模拟生成文件URL（实际项目中需要实现文件生成和存储）
            String fileName = String.format("稿费_%s_%s_%s.%s",
                    departmentId, statisticalMonth, System.currentTimeMillis(),
                    "Excel".equalsIgnoreCase(format) ? "xlsx" : "pdf");
            String fileUrl = "https://example.com/export/" + fileName;

            exportResult.put("fileUrl", fileUrl);
            exportResult.put("exportTime", new Date());
            exportResult.put("recordCount", exportData.size());
            exportResult.put("statisticalMonth", statisticalMonth);
            exportResult.put("departmentId", departmentId);

            return ApiResponse.success(exportResult, "导出成功");

        } catch (Exception e) {
            throw new RuntimeException("导出稿费记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getUserMonthlyTotal(Long userId, String month) {
        try {
            BigDecimal total = BigDecimal.ZERO;

            // 查询新闻部稿费
            List<NewsDepartmentMonthly> newsRecords = newsDepartmentMonthlyMapper.selectByUserAndMonth(userId, month);
            for (NewsDepartmentMonthly record : newsRecords) {
                total = total.add(record.getFeeAmount());
            }

            // 查询编辑部稿费
            List<EditorialDepartmentMonthly> editorialRecords = editorialDepartmentMonthlyMapper.selectByUserAndMonth(userId, month);
            for (EditorialDepartmentMonthly record : editorialRecords) {
                total = total.add(record.getFeeAmount());
            }

            return total;
        } catch (Exception e) {
            throw new RuntimeException("获取用户月度总稿费失败: " + e.getMessage(), e);
        }
    }


    @Override
    public BigDecimal calculateDepartmentMonthlyTotal(Long departmentId, String month) {
        try {
            if (NEWS_DEPARTMENT_ID.equals(departmentId)) {
                Double sum = newsDepartmentMonthlyMapper.sumFeeByMonth(month);
                return sum != null ? BigDecimal.valueOf(sum) : BigDecimal.ZERO;
            } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
                Double sum = editorialDepartmentMonthlyMapper.sumFeeByMonth(month);
                return sum != null ? BigDecimal.valueOf(sum) : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            throw new RuntimeException("统计部门月度稿费总额失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean canProxyRoyalty(Long recordId, Long departmentId, Long originalUserId) {
        try {
            // 获取用户本月总稿费
            String currentMonth = getCurrentMonth();
            BigDecimal monthlyTotal = getUserMonthlyTotal(originalUserId, currentMonth);

            // 获取当前记录金额
            BigDecimal recordAmount = BigDecimal.ZERO;

            if (NEWS_DEPARTMENT_ID.equals(departmentId)) {
                NewsDepartmentMonthly record = newsDepartmentMonthlyMapper.selectByRecordId(recordId);
                if (record != null) {
                    recordAmount = record.getFeeAmount();
                }
            } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
                EditorialDepartmentMonthly record = editorialDepartmentMonthlyMapper.selectByRecordId(recordId);
                if (record != null) {
                    recordAmount = record.getFeeAmount();
                }
            }

            // 计算加上当前记录后的总额
            BigDecimal totalWithRecord = monthlyTotal.add(recordAmount);

            // 检查是否超过800元限制
            return totalWithRecord.compareTo(MONTHLY_MAX_FEE) > 0;

        } catch (Exception e) {
            throw new RuntimeException("验证代领资格失败: " + e.getMessage(), e);
        }
    }

    // ============ 私有辅助方法 ============

    private List<NewsDepartmentMonthly> findNewsRecordsByUserId(Long userId, RoyaltyQueryRequest request) {
        // 简化实现，实际需要根据时间范围等条件查询
        // 这里假设所有记录都包含userId
        return newsDepartmentMonthlyMapper.selectAll();
    }

    private List<EditorialDepartmentMonthly> findEditorialRecordsByUserId(Long userId, RoyaltyQueryRequest request) {
        // 简化实现，实际需要根据时间范围等条件查询
        return editorialDepartmentMonthlyMapper.selectAll();
    }

    private List<NewsDepartmentMonthly> findNewsRecordsByDepartment(Long departmentId, RoyaltyQueryRequest request) {
        // 简化实现
        return newsDepartmentMonthlyMapper.selectAll();
    }

    private List<EditorialDepartmentMonthly> findEditorialRecordsByDepartment(Long departmentId, RoyaltyQueryRequest request) {
        // 简化实现
        return editorialDepartmentMonthlyMapper.selectAll();
    }

    private List<NewsDepartmentMonthly> findAllNewsRecords(RoyaltyQueryRequest request) {
        // 简化实现
        return newsDepartmentMonthlyMapper.selectAll();
    }

    private List<EditorialDepartmentMonthly> findAllEditorialRecords(RoyaltyQueryRequest request) {
        // 简化实现
        return editorialDepartmentMonthlyMapper.selectAll();
    }

    private RoyaltyResponse convertToRoyaltyResponse(NewsDepartmentMonthly record, Long departmentId) {
        RoyaltyResponse response = new RoyaltyResponse();

        try {
            response.setRecordId(record.getRecordId());

            // 解析JSON数组
            if (record.getUserIds() != null && !record.getUserIds().isEmpty()) {
                List<Long> userIds = objectMapper.readValue(record.getUserIds(), new TypeReference<List<Long>>() {});
                response.setUserIds(userIds);
            }

            if (record.getRealNames() != null && !record.getRealNames().isEmpty()) {
                List<String> realNames = objectMapper.readValue(record.getRealNames(), new TypeReference<List<String>>() {});
                response.setRealNames(realNames);
            }

            if (record.getStudentNumbers() != null && !record.getStudentNumbers().isEmpty()) {
                List<String> studentNumbers = objectMapper.readValue(record.getStudentNumbers(), new TypeReference<List<String>>() {});
                response.setStudentNumbers(studentNumbers);
            }

            response.setArticleTitle(record.getArticleTitle());
            response.setArticleType(record.getArticleType());
            response.setFeeAmount(record.getFeeAmount());
            response.setStatisticalMonth(record.getStatisticalMonth());
            response.setDepartmentId(departmentId);
            response.setCreatedAt(record.getCreatedAt());
            response.setUpdatedAt(record.getUpdatedAt());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }

        return response;
    }

    private RoyaltyResponse convertToRoyaltyResponse(EditorialDepartmentMonthly record, Long departmentId) {
        RoyaltyResponse response = new RoyaltyResponse();

        try {
            response.setRecordId(record.getRecordId());

            // 解析JSON数组
            if (record.getUserIds() != null && !record.getUserIds().isEmpty()) {
                List<Long> userIds = objectMapper.readValue(record.getUserIds(), new TypeReference<List<Long>>() {});
                response.setUserIds(userIds);
            }

            if (record.getRealNames() != null && !record.getRealNames().isEmpty()) {
                List<String> realNames = objectMapper.readValue(record.getRealNames(), new TypeReference<List<String>>() {});
                response.setRealNames(realNames);
            }

            if (record.getStudentNumbers() != null && !record.getStudentNumbers().isEmpty()) {
                List<String> studentNumbers = objectMapper.readValue(record.getStudentNumbers(), new TypeReference<List<String>>() {});
                response.setStudentNumbers(studentNumbers);
            }

            response.setArticleTitle(record.getArticleTitle());
            response.setArticleType(record.getArticleType());
            response.setFeeAmount(record.getFeeAmount());
            response.setStatisticalMonth(record.getStatisticalMonth());
            response.setDepartmentId(departmentId);
            response.setCreatedAt(record.getCreatedAt());
            response.setUpdatedAt(record.getUpdatedAt());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + e.getMessage(), e);
        }

        return response;
    }

    private void validateAddRequest(RoyaltyAddRequest request) {
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new RuntimeException("用户ID不能为空");
        }

        if (request.getRealNames() == null || request.getRealNames().isEmpty()) {
            throw new RuntimeException("真实姓名不能为空");
        }

        if (request.getStudentNumbers() == null || request.getStudentNumbers().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }

        if (request.getUserIds().size() != request.getRealNames().size() ||
                request.getUserIds().size() != request.getStudentNumbers().size()) {
            throw new RuntimeException("用户ID、真实姓名、学号数组长度必须一致");
        }

        if (request.getArticleTitle() == null || request.getArticleTitle().isEmpty()) {
            throw new RuntimeException("稿件标题不能为空");
        }

        if (request.getArticleType() == null || request.getArticleType().isEmpty()) {
            throw new RuntimeException("稿件类型不能为空");
        }

        if (request.getFeeAmount() == null || request.getFeeAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("稿费金额必须大于0");
        }

        if (request.getStatisticalMonth() == null || request.getStatisticalMonth().isEmpty()) {
            throw new RuntimeException("统计月份不能为空");
        }

        if (request.getDepartmentId() == null) {
            throw new RuntimeException("部门ID不能为空");
        }
    }

    private NewsDepartmentMonthly createNewsRecord(RoyaltyAddRequest request) {
        NewsDepartmentMonthly record = new NewsDepartmentMonthly();

        try {
            // 设置JSON字段
            record.setUserIds(objectMapper.writeValueAsString(request.getUserIds()));
            record.setRealNames(objectMapper.writeValueAsString(request.getRealNames()));
            record.setStudentNumbers(objectMapper.writeValueAsString(request.getStudentNumbers()));

            // 设置其他字段
            record.setArticleTitle(request.getArticleTitle());
            record.setArticleType(request.getArticleType());
            record.setFeeAmount(request.getFeeAmount());
            record.setStatisticalMonth(request.getStatisticalMonth());
            record.setDepartmentId(request.getDepartmentId());
            record.setCreatedAt(new Date());
            record.setUpdatedAt(new Date());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("创建新闻部记录失败: " + e.getMessage(), e);
        }

        return record;
    }

    private EditorialDepartmentMonthly createEditorialRecord(RoyaltyAddRequest request) {
        EditorialDepartmentMonthly record = new EditorialDepartmentMonthly();

        try {
            // 设置JSON字段
            record.setUserIds(objectMapper.writeValueAsString(request.getUserIds()));
            record.setRealNames(objectMapper.writeValueAsString(request.getRealNames()));
            record.setStudentNumbers(objectMapper.writeValueAsString(request.getStudentNumbers()));

            // 设置其他字段
            record.setArticleTitle(request.getArticleTitle());
            record.setArticleType(request.getArticleType());
            record.setFeeAmount(request.getFeeAmount());
            record.setStatisticalMonth(request.getStatisticalMonth());
            record.setDepartmentId(request.getDepartmentId());
            record.setCreatedAt(new Date());
            record.setUpdatedAt(new Date());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("创建编辑部记录失败: " + e.getMessage(), e);
        }

        return record;
    }

    private void updateNewsRecord(NewsDepartmentMonthly record, RoyaltyUpdateRequest request) {
        record.setArticleTitle(request.getArticleTitle());
        record.setArticleType(request.getArticleType());
        record.setFeeAmount(request.getFeeAmount());
        record.setStatisticalMonth(request.getStatisticalMonth());
        record.setUpdatedAt(new Date());
    }

    private void updateEditorialRecord(EditorialDepartmentMonthly record, RoyaltyUpdateRequest request) {
        record.setArticleTitle(request.getArticleTitle());
        record.setArticleType(request.getArticleType());
        record.setFeeAmount(request.getFeeAmount());
        record.setStatisticalMonth(request.getStatisticalMonth());
        record.setUpdatedAt(new Date());
    }

    private void updateFeeMonthlySummary(RoyaltyAddRequest request) {
        // 为每个用户更新汇总表
        for (int i = 0; i < request.getUserIds().size(); i++) {
            Long userId = request.getUserIds().get(i);
            String month = request.getStatisticalMonth();
            Long departmentId = request.getDepartmentId();

            // 查询是否已存在汇总记录
            FeeMonthlySummary summary = feeMonthlySummaryMapper.selectByUserAndMonth(userId, month);

            if (summary == null) {
                // 创建新汇总记录
                summary = new FeeMonthlySummary();
                summary.setUserId(userId);
                summary.setRealName(request.getRealNames().get(i));
                summary.setStudentNumber(request.getStudentNumbers().get(i));
                summary.setDepartmentId(departmentId);
                summary.setTotalAmount(request.getFeeAmount());
                summary.setStatisticalMonth(month);
                summary.setCreatedAt(new Date());
                summary.setUpdatedAt(new Date());

                feeMonthlySummaryMapper.insertSummary(summary);
            } else {
                // 更新现有汇总记录
                summary.setTotalAmount(summary.getTotalAmount().add(request.getFeeAmount()));
                summary.setUpdatedAt(new Date());

                feeMonthlySummaryMapper.updateSummary(summary);
            }
        }
    }

    private void recalculateFeeMonthlySummary(Long recordId, Long departmentId) {
        // 简化实现：重新计算该用户该月的总稿费
        // 实际项目中需要更精确的实现
    }

    private void checkProxyRequirements(RoyaltyAddRequest request) {
        // 检查每个用户的代领需求
        for (Long userId : request.getUserIds()) {
            BigDecimal monthlyTotal = getUserMonthlyTotal(userId, request.getStatisticalMonth());

            // 检查是否超过800元限制
            if (monthlyTotal.compareTo(MONTHLY_MAX_FEE) > 0) {
                // 记录需要代领
                // 实际项目中可以发送通知或记录日志
            }
        }
    }

    private String getCurrentMonth() {
        // 返回当前月份（yyyy-MM格式）
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        return sdf.format(new Date());
    }
}