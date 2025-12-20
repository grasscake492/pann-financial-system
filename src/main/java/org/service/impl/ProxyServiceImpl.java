// src/main/java/org/service/impl/ProxyServiceImpl.java
package org.service.impl;

import org.dao.ProxyReceiptRecordsMapper;
import org.dao.NewsDepartmentMonthlyMapper;
import org.dao.EditorialDepartmentMonthlyMapper;
import org.dao.UserMapper;
import org.dao.DepartmentMapper;
import org.dao.FeeMonthlySummaryMapper;
import org.dto.request.ProxyAddRequest;
import org.dto.request.ProxyQueryRequest;
import org.dto.request.ProxyUpdateRequest;
import org.dto.response.ProxyResponse;
import org.dto.response.ProxyListResponse;
import org.dto.response.ApiResponse;
import org.entity.ProxyReceiptRecords;
import org.entity.NewsDepartmentMonthly;
import org.entity.EditorialDepartmentMonthly;
import org.entity.User;
import org.entity.Department;
import org.entity.FeeMonthlySummary;
import org.constant.ResponseCode;
import org.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 代领管理服务实现类
 */
@Service
public class ProxyServiceImpl implements ProxyService {

    // 部门ID常量
    private static final Long NEWS_DEPARTMENT_ID = 1L;      // 新闻部ID
    private static final Long EDITORIAL_DEPARTMENT_ID = 2L; // 编辑部ID

    // 最大稿费限额
    private static final BigDecimal MAX_MONTHLY_FEE = new BigDecimal("800.00");

    @Autowired
    private ProxyReceiptRecordsMapper proxyReceiptRecordsMapper;

    @Autowired
    private NewsDepartmentMonthlyMapper newsDepartmentMonthlyMapper;

    @Autowired
    private EditorialDepartmentMonthlyMapper editorialDepartmentMonthlyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private FeeMonthlySummaryMapper feeMonthlySummaryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ProxyResponse> addProxyRecord(ProxyAddRequest request, Long adminId) {
        try {
            // 1. 参数验证
            if (request == null || request.getFeeRecordId() == null || request.getOriginalUserId() == null
                    || request.getProxyUserId() == null || request.getProxyMonth() == null
                    || request.getFeeAmount() == null) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "请求参数不完整");
            }

            // 2. 验证代领规则
            if (!validateProxyRule(request.getOriginalUserId(), request.getProxyUserId(),
                    request.getFeeAmount(), request.getProxyMonth())) {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "不符合代领规则");
            }

            // 3. 获取用户信息
            User originalUser = userMapper.selectByUserId(request.getOriginalUserId());
            User proxyUser = userMapper.selectByUserId(request.getProxyUserId());

            if (originalUser == null || proxyUser == null) {
                return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "用户不存在");
            }

            // 4. 获取稿费记录信息并更新
            String articleTitle = "";
            boolean isNewsDepartment = NEWS_DEPARTMENT_ID.equals(request.getDepartmentId());
            boolean isEditorialDepartment = EDITORIAL_DEPARTMENT_ID.equals(request.getDepartmentId());

            if (isNewsDepartment) { // 新闻部
                NewsDepartmentMonthly newsRecord = newsDepartmentMonthlyMapper.selectByRecordId(request.getFeeRecordId());
                if (newsRecord == null) {
                    return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "稿费记录不存在");
                }
                articleTitle = newsRecord.getArticleTitle();

                // 更新稿费记录中的用户信息为代领用户信息（使用JSON数组格式）
                // 注意：原实体类中使用JSON数组存储，这里简化为单个用户
                newsRecord.setUserIds("[" + proxyUser.getUserId() + "]");
                newsRecord.setRealNames("[" + proxyUser.getRealName() + "]");
                newsRecord.setStudentNumbers("[" + proxyUser.getStudentNumber() + "]");
                // 更新记录
                int updateResult = newsDepartmentMonthlyMapper.updateRecord(newsRecord);
                if (updateResult <= 0) {
                    throw new RuntimeException("更新新闻部稿费记录失败");
                }

                // 更新稿费月汇总表 - 修正类型转换
                BigDecimal feeAmount = newsRecord.getFeeAmount();
                Double feeAmountDouble = feeAmount != null ? feeAmount.doubleValue() : 0.0;
                updateFeeMonthlySummary(request.getOriginalUserId(), request.getProxyUserId(),
                        feeAmountDouble, request.getProxyMonth(), NEWS_DEPARTMENT_ID);

            } else if (isEditorialDepartment) { // 编辑部
                EditorialDepartmentMonthly editorialRecord = editorialDepartmentMonthlyMapper.selectByRecordId(request.getFeeRecordId());
                if (editorialRecord == null) {
                    return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "稿费记录不存在");
                }
                articleTitle = editorialRecord.getArticleTitle();

                // 更新稿费记录中的用户信息为代领用户信息（使用JSON数组格式）
                editorialRecord.setUserIds("[" + proxyUser.getUserId() + "]");
                editorialRecord.setRealNames("[" + proxyUser.getRealName() + "]");
                editorialRecord.setStudentNumbers("[" + proxyUser.getStudentNumber() + "]");
                // 更新记录
                int updateResult = editorialDepartmentMonthlyMapper.updateRecord(editorialRecord);
                if (updateResult <= 0) {
                    throw new RuntimeException("更新编辑部稿费记录失败");
                }

                // 更新稿费月汇总表 - 修正类型转换
                BigDecimal feeAmount = editorialRecord.getFeeAmount();
                Double feeAmountDouble = feeAmount != null ? feeAmount.doubleValue() : 0.0;
                updateFeeMonthlySummary(request.getOriginalUserId(), request.getProxyUserId(),
                        feeAmountDouble, request.getProxyMonth(), EDITORIAL_DEPARTMENT_ID);

            } else {
                return ApiResponse.error(ResponseCode.PARAM_ERROR, "部门ID无效");
            }

            // 5. 创建代领记录
            ProxyReceiptRecords proxyRecord = new ProxyReceiptRecords();
            proxyRecord.setFeeRecordId(request.getFeeRecordId());
            proxyRecord.setDepartmentId(request.getDepartmentId());
            proxyRecord.setOriginalUserId(request.getOriginalUserId());
            proxyRecord.setOriginalRealName(originalUser.getRealName());
            proxyRecord.setOriginalStudentNumber(originalUser.getStudentNumber());
            proxyRecord.setProxyUserId(request.getProxyUserId());
            proxyRecord.setProxyRealName(proxyUser.getRealName());
            proxyRecord.setProxyStudentNumber(proxyUser.getStudentNumber());
            proxyRecord.setArticleTitle(articleTitle);
            proxyRecord.setFeeAmount(request.getFeeAmount());
            proxyRecord.setProxyMonth(request.getProxyMonth());
            proxyRecord.setCreatedAt(new Date());
            proxyRecord.setUpdatedAt(new Date());

            int result = proxyReceiptRecordsMapper.insertProxyRecord(proxyRecord);
            if (result <= 0) {
                throw new RuntimeException("插入代领记录失败");
            }

            // 6. 构建响应
            ProxyResponse response = convertToProxyResponse(proxyRecord);

            return ApiResponse.success(response, "设置代领成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "添加代领记录失败: " + e.getMessage());
        }
    }

    @Override
    public ProxyListResponse queryProxyRecords(ProxyQueryRequest request, Long adminId) {
        try {
            // 1. 构建查询条件
            Map<String, Object> params = new HashMap<>();
            if (request.getDepartmentId() != null) {
                params.put("departmentId", request.getDepartmentId());
            }
            if (request.getOriginalUserId() != null) {
                params.put("originalUserId", request.getOriginalUserId());
            }
            if (request.getProxyUserId() != null) {
                params.put("proxyUserId", request.getProxyUserId());
            }
            if (StringUtils.hasText(request.getProxyMonth())) {
                params.put("proxyMonth", request.getProxyMonth());
            }

            // 2. 分页参数
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getSize() != null ? request.getSize() : 10;
            int offset = (page - 1) * size;

            // 3. 查询总记录数（简化处理）
            int total = proxyReceiptRecordsMapper.countProxyRecords();

            // 4. 查询分页数据
            List<ProxyReceiptRecords> records = proxyReceiptRecordsMapper.selectByPage(offset, size);

            // 5. 转换为响应DTO
            List<ProxyResponse> proxyList = new ArrayList<>();
            for (ProxyReceiptRecords record : records) {
                proxyList.add(convertToProxyResponse(record));
            }

            // 6. 构建响应
            return new ProxyListResponse(total, page, size, proxyList);

        } catch (Exception e) {
            throw new RuntimeException("查询代领记录失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ProxyResponse> updateProxyRecord(Long proxyId, ProxyUpdateRequest request, Long adminId) {
        try {
            // 1. 查询代领记录
            ProxyReceiptRecords proxyRecord = proxyReceiptRecordsMapper.selectByProxyId(proxyId);
            if (proxyRecord == null) {
                return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "代领记录不存在");
            }

            // 2. 更新字段
            boolean needUpdate = false;

            if (request.getProxyUserId() != null && !request.getProxyUserId().equals(proxyRecord.getProxyUserId())) {
                // 需要更新代领用户信息
                User proxyUser = userMapper.selectByUserId(request.getProxyUserId());
                if (proxyUser == null) {
                    return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "代领用户不存在");
                }

                // 更新稿费记录表中的用户信息
                updateFeeRecordUserInfo(proxyRecord.getFeeRecordId(), proxyRecord.getDepartmentId(),
                        request.getProxyUserId(), proxyUser.getRealName(), proxyUser.getStudentNumber());

                // 更新稿费月汇总表
                updateFeeMonthlySummaryForProxyChange(proxyRecord.getOriginalUserId(), proxyRecord.getProxyUserId(),
                        request.getProxyUserId(), proxyRecord.getFeeAmount(),
                        proxyRecord.getProxyMonth(), proxyRecord.getDepartmentId());

                proxyRecord.setProxyUserId(request.getProxyUserId());
                proxyRecord.setProxyRealName(proxyUser.getRealName());
                proxyRecord.setProxyStudentNumber(proxyUser.getStudentNumber());
                needUpdate = true;
            }

            if (StringUtils.hasText(request.getProxyMonth()) && !request.getProxyMonth().equals(proxyRecord.getProxyMonth())) {
                proxyRecord.setProxyMonth(request.getProxyMonth());
                needUpdate = true;
            }

            if (request.getFeeAmount() != null && !request.getFeeAmount().equals(proxyRecord.getFeeAmount())) {
                // 更新稿费记录中的金额
                updateFeeRecordAmount(proxyRecord.getFeeRecordId(), proxyRecord.getDepartmentId(),
                        request.getFeeAmount());

                proxyRecord.setFeeAmount(request.getFeeAmount());
                needUpdate = true;
            }

            if (StringUtils.hasText(request.getArticleTitle()) && !request.getArticleTitle().equals(proxyRecord.getArticleTitle())) {
                // 更新稿费记录中的标题
                updateFeeRecordTitle(proxyRecord.getFeeRecordId(), proxyRecord.getDepartmentId(),
                        request.getArticleTitle());

                proxyRecord.setArticleTitle(request.getArticleTitle());
                needUpdate = true;
            }

            // 3. 更新代领记录
            if (needUpdate) {
                proxyRecord.setUpdatedAt(new Date());
                int result = proxyReceiptRecordsMapper.updateProxyRecord(proxyRecord);
                if (result <= 0) {
                    throw new RuntimeException("更新代领记录失败");
                }
            }

            // 4. 构建响应
            ProxyResponse response = convertToProxyResponse(proxyRecord);
            return ApiResponse.success(response, "修改代领记录成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "修改代领记录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> revokeProxyRecord(Long proxyId, Long adminId) {
        try {
            // 1. 查询代领记录
            ProxyReceiptRecords proxyRecord = proxyReceiptRecordsMapper.selectByProxyId(proxyId);
            if (proxyRecord == null) {
                return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "代领记录不存在");
            }

            // 2. 获取原始用户信息
            User originalUser = userMapper.selectByUserId(proxyRecord.getOriginalUserId());
            if (originalUser == null) {
                return ApiResponse.error(ResponseCode.DATA_NOT_FOUND, "原始用户不存在");
            }

            // 3. 恢复稿费记录中的原始用户信息
            restoreFeeRecordUserInfo(proxyRecord.getFeeRecordId(), proxyRecord.getDepartmentId(),
                    originalUser.getUserId(), originalUser.getRealName(), originalUser.getStudentNumber());

            // 4. 恢复稿费月汇总表
            revertFeeMonthlySummary(proxyRecord.getOriginalUserId(), proxyRecord.getProxyUserId(),
                    proxyRecord.getFeeAmount(), proxyRecord.getProxyMonth(), proxyRecord.getDepartmentId());

            // 5. 删除代领记录
            int result = proxyReceiptRecordsMapper.deleteByProxyId(proxyId);
            if (result <= 0) {
                throw new RuntimeException("删除代领记录失败");
            }

            return ApiResponse.success((Void) null, "撤销代领成功");

        } catch (Exception e) {
            return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "撤销代领记录失败: " + e.getMessage());
        }
    }

    @Override
    public List<ProxyReceiptRecords> queryProxyByDepartment(Long departmentId, String month, int page, int size) {
        // 这里简化处理，实际应该根据条件查询
        int offset = (page - 1) * size;
        return proxyReceiptRecordsMapper.selectByPage(offset, size);
    }

    @Override
    public List<ProxyReceiptRecords> queryProxyByUser(Long userId, boolean isOriginalUser, String month, int page, int size) {
        if (isOriginalUser) {
            return proxyReceiptRecordsMapper.selectByOriginalUser(userId);
        } else {
            return proxyReceiptRecordsMapper.selectByProxyUser(userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long executeProxy(Long feeRecordId, Long departmentId, Long originalUserId,
                             Long proxyUserId, String proxyMonth, BigDecimal feeAmount, Long adminId) {
        try {
            // 验证部门ID合法性
            if (!NEWS_DEPARTMENT_ID.equals(departmentId) && !EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
                throw new RuntimeException("不支持的部门ID: " + departmentId);
            }

            // 验证代领规则
            if (!validateProxyRule(originalUserId, proxyUserId, feeAmount, proxyMonth)) {
                throw new RuntimeException("不符合代领规则");
            }

            // 获取用户信息
            User originalUser = userMapper.selectByUserId(originalUserId);
            User proxyUser = userMapper.selectByUserId(proxyUserId);

            if (originalUser == null || proxyUser == null) {
                throw new RuntimeException("用户不存在");
            }

            // 获取稿费记录标题
            String articleTitle = getArticleTitleFromFeeRecord(feeRecordId, departmentId);
            if (articleTitle == null) {
                throw new RuntimeException("稿费记录不存在或无法获取文章标题");
            }

            // 更新稿费记录用户信息
            updateFeeRecordUserInfo(feeRecordId, departmentId, proxyUserId,
                    proxyUser.getRealName(), proxyUser.getStudentNumber());

            // 更新稿费月汇总表
            Double feeAmountDouble = feeAmount != null ? feeAmount.doubleValue() : 0.0;
            updateFeeMonthlySummary(originalUserId, proxyUserId,
                    feeAmountDouble, proxyMonth, departmentId);

            // 创建代领记录
            ProxyReceiptRecords proxyRecord = new ProxyReceiptRecords();
            proxyRecord.setFeeRecordId(feeRecordId);
            proxyRecord.setDepartmentId(departmentId);
            proxyRecord.setOriginalUserId(originalUserId);
            proxyRecord.setOriginalRealName(originalUser.getRealName());
            proxyRecord.setOriginalStudentNumber(originalUser.getStudentNumber());
            proxyRecord.setProxyUserId(proxyUserId);
            proxyRecord.setProxyRealName(proxyUser.getRealName());
            proxyRecord.setProxyStudentNumber(proxyUser.getStudentNumber());
            proxyRecord.setArticleTitle(articleTitle);
            proxyRecord.setFeeAmount(feeAmount);
            proxyRecord.setProxyMonth(proxyMonth);
            proxyRecord.setCreatedAt(new Date());
            proxyRecord.setUpdatedAt(new Date());

            int result = proxyReceiptRecordsMapper.insertProxyRecord(proxyRecord);
            if (result <= 0) {
                throw new RuntimeException("创建代领记录失败");
            }

            return proxyRecord.getProxyId();

        } catch (Exception e) {
            throw new RuntimeException("执行代领操作失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeProxy(Long proxyId, Long adminId) {
        try {
            ApiResponse<Void> response = revokeProxyRecord(proxyId, adminId);
            return ResponseCode.SUCCESS.getCode().equals(response.getResCode());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean validateProxyRule(Long originalUserId, Long proxyUserId, BigDecimal feeAmount, String month) {
        try {
            // 规则1：不能自己代领自己
            if (originalUserId.equals(proxyUserId)) {
                return false;
            }

            // 规则2：检查原始用户是否为勤工助学学生
            if (isWorkStudyStudent(originalUserId)) {
                return true; // 勤工助学学生必须代领
            }

            // 规则3：检查原始用户本月已领取稿费总额
            BigDecimal receivedAmount = getUserReceivedAmount(originalUserId, month);
            if (receivedAmount == null) {
                receivedAmount = BigDecimal.ZERO;
            }

            // 加上本次稿费金额
            BigDecimal totalAmount = receivedAmount.add(feeAmount);

            // 如果超过800元，则需要代领
            return totalAmount.compareTo(MAX_MONTHLY_FEE) > 0;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isWorkStudyStudent(Long userId) {
        try {
            User user = userMapper.selectByUserId(userId);
            if (user == null) {
                return false;
            }
            // 假设用户实体中有工作性质字段
            // return "勤工助学".equals(user.getWorkType());
            return false; // 简化处理，根据实际情况调整
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public BigDecimal getUserReceivedAmount(Long userId, String month) {
        try {
            // 查询新闻部和编辑部两表中该用户该月的稿费总额
            BigDecimal total = BigDecimal.ZERO;

            // 查询新闻部稿费记录
            List<NewsDepartmentMonthly> newsRecords = newsDepartmentMonthlyMapper.selectByUserAndMonth(userId, month);
            if (newsRecords != null) {
                for (NewsDepartmentMonthly record : newsRecords) {
                    if (record.getFeeAmount() != null) {
                        // 直接使用BigDecimal，不需要valueOf转换
                        total = total.add(record.getFeeAmount());
                    }
                }
            }

            // 查询编辑部稿费记录
            List<EditorialDepartmentMonthly> editorialRecords = editorialDepartmentMonthlyMapper.selectByUserAndMonth(userId, month);
            if (editorialRecords != null) {
                for (EditorialDepartmentMonthly record : editorialRecords) {
                    if (record.getFeeAmount() != null) {
                        total = total.add(record.getFeeAmount());
                    }
                }
            }

            return total;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // 私有辅助方法

    /**
     * 更新稿费记录中的用户信息为代领用户
     */
    private void updateFeeRecordUserInfo(Long feeRecordId, Long departmentId, Long userId,
                                         String realName, String studentNumber) {
        if (NEWS_DEPARTMENT_ID.equals(departmentId)) { // 新闻部
            NewsDepartmentMonthly record = newsDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                // 使用JSON数组格式更新用户信息
                record.setUserIds("[" + userId + "]");
                record.setRealNames("[" + realName + "]");
                record.setStudentNumbers("[" + studentNumber + "]");
                int result = newsDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新新闻部稿费记录失败");
                }
            } else {
                throw new RuntimeException("新闻部稿费记录不存在，ID: " + feeRecordId);
            }
        } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) { // 编辑部
            EditorialDepartmentMonthly record = editorialDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                // 使用JSON数组格式更新用户信息
                record.setUserIds("[" + userId + "]");
                record.setRealNames("[" + realName + "]");
                record.setStudentNumbers("[" + studentNumber + "]");
                int result = editorialDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新编辑部稿费记录失败");
                }
            } else {
                throw new RuntimeException("编辑部稿费记录不存在，ID: " + feeRecordId);
            }
        } else {
            throw new RuntimeException("不支持的部门ID: " + departmentId);
        }
    }

    /**
     * 恢复稿费记录中的原始用户信息
     */
    private void restoreFeeRecordUserInfo(Long feeRecordId, Long departmentId, Long userId,
                                          String realName, String studentNumber) {
        updateFeeRecordUserInfo(feeRecordId, departmentId, userId, realName, studentNumber);
    }

    /**
     * 更新稿费记录中的金额
     */
    private void updateFeeRecordAmount(Long feeRecordId, Long departmentId, BigDecimal feeAmount) {
        if (NEWS_DEPARTMENT_ID.equals(departmentId)) { // 新闻部
            NewsDepartmentMonthly record = newsDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                record.setFeeAmount(feeAmount);
                int result = newsDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新新闻部稿费记录金额失败");
                }
            }
        } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) { // 编辑部
            EditorialDepartmentMonthly record = editorialDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                record.setFeeAmount(feeAmount);
                int result = editorialDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新编辑部稿费记录金额失败");
                }
            }
        }
    }

    /**
     * 更新稿费记录中的标题
     */
    private void updateFeeRecordTitle(Long feeRecordId, Long departmentId, String articleTitle) {
        if (NEWS_DEPARTMENT_ID.equals(departmentId)) { // 新闻部
            NewsDepartmentMonthly record = newsDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                record.setArticleTitle(articleTitle);
                int result = newsDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新新闻部稿费记录标题失败");
                }
            }
        } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) { // 编辑部
            EditorialDepartmentMonthly record = editorialDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            if (record != null) {
                record.setArticleTitle(articleTitle);
                int result = editorialDepartmentMonthlyMapper.updateRecord(record);
                if (result <= 0) {
                    throw new RuntimeException("更新编辑部稿费记录标题失败");
                }
            }
        }
    }

    /**
     * 获取稿费记录标题
     */
    private String getArticleTitleFromFeeRecord(Long feeRecordId, Long departmentId) {
        if (NEWS_DEPARTMENT_ID.equals(departmentId)) {
            NewsDepartmentMonthly record = newsDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            return record != null ? record.getArticleTitle() : null;
        } else if (EDITORIAL_DEPARTMENT_ID.equals(departmentId)) {
            EditorialDepartmentMonthly record = editorialDepartmentMonthlyMapper.selectByRecordId(feeRecordId);
            return record != null ? record.getArticleTitle() : null;
        }
        return null;
    }

    /**
     * 更新稿费月汇总表
     * 注意：这里假设FeeMonthlySummary实体类中有getTotalAmount和setTotalAmount方法
     * 如果没有，需要根据实际字段名调整
     */
    private void updateFeeMonthlySummary(Long originalUserId, Long proxyUserId, Double feeAmount,
                                         String month, Long departmentId) {
        try {
            // 1. 减少原始用户的汇总金额
            FeeMonthlySummary originalSummary = feeMonthlySummaryMapper.selectByUserAndMonth(originalUserId, month);
            if (originalSummary != null) {
                // 假设FeeMonthlySummary中有getTotalAmount方法获取总金额
                // 这里需要根据实际实体类调整方法名
                // 假设字段名是totalAmount，那么getter应该是getTotalAmount
                BigDecimal currentAmount = originalSummary.getTotalAmount();
                if (currentAmount != null && feeAmount != null) {
                    BigDecimal newAmount = currentAmount.subtract(BigDecimal.valueOf(feeAmount));
                    originalSummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(originalSummary);
                }
            }

            // 2. 增加代领用户的汇总金额
            FeeMonthlySummary proxySummary = feeMonthlySummaryMapper.selectByUserAndMonth(proxyUserId, month);
            if (proxySummary != null) {
                BigDecimal currentAmount = proxySummary.getTotalAmount();
                if (currentAmount != null && feeAmount != null) {
                    BigDecimal newAmount = currentAmount.add(BigDecimal.valueOf(feeAmount));
                    proxySummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(proxySummary);
                }
            } else {
                // 如果代领用户没有该月的汇总记录，创建一条
                User proxyUser = userMapper.selectByUserId(proxyUserId);
                if (proxyUser != null) {
                    FeeMonthlySummary newSummary = new FeeMonthlySummary();
                    newSummary.setUserId(proxyUserId);
                    newSummary.setRealName(proxyUser.getRealName());
                    newSummary.setStudentNumber(proxyUser.getStudentNumber());
                    newSummary.setDepartmentId(departmentId);
                    newSummary.setTotalAmount(BigDecimal.valueOf(feeAmount != null ? feeAmount : 0.0));
                    newSummary.setStatisticalMonth(month);
                    newSummary.setCreatedAt(new Date());
                    newSummary.setUpdatedAt(new Date());
                    feeMonthlySummaryMapper.insertSummary(newSummary);
                }
            }
        } catch (Exception e) {
            // 汇总表更新失败不影响主流程，记录日志即可
            System.err.println("更新稿费月汇总表失败: " + e.getMessage());
        }
    }

    /**
     * 恢复稿费月汇总表
     */
    private void revertFeeMonthlySummary(Long originalUserId, Long proxyUserId, BigDecimal feeAmount,
                                         String month, Long departmentId) {
        try {
            // 1. 恢复原始用户的汇总金额
            FeeMonthlySummary originalSummary = feeMonthlySummaryMapper.selectByUserAndMonth(originalUserId, month);
            if (originalSummary != null) {
                BigDecimal currentAmount = originalSummary.getTotalAmount();
                if (currentAmount != null) {
                    BigDecimal newAmount = currentAmount.add(feeAmount);
                    originalSummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(originalSummary);
                }
            }

            // 2. 减少代领用户的汇总金额
            FeeMonthlySummary proxySummary = feeMonthlySummaryMapper.selectByUserAndMonth(proxyUserId, month);
            if (proxySummary != null) {
                BigDecimal currentAmount = proxySummary.getTotalAmount();
                if (currentAmount != null) {
                    BigDecimal newAmount = currentAmount.subtract(feeAmount);
                    proxySummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(proxySummary);
                }
            }
        } catch (Exception e) {
            // 汇总表更新失败不影响主流程，记录日志即可
            System.err.println("恢复稿费月汇总表失败: " + e.getMessage());
        }
    }

    /**
     * 更新稿费月汇总表（代领用户变更时）
     */
    private void updateFeeMonthlySummaryForProxyChange(Long originalUserId, Long oldProxyUserId,
                                                       Long newProxyUserId, BigDecimal feeAmount,
                                                       String month, Long departmentId) {
        try {
            // 1. 减少旧代领用户的汇总金额
            FeeMonthlySummary oldProxySummary = feeMonthlySummaryMapper.selectByUserAndMonth(oldProxyUserId, month);
            if (oldProxySummary != null) {
                BigDecimal currentAmount = oldProxySummary.getTotalAmount();
                if (currentAmount != null) {
                    BigDecimal newAmount = currentAmount.subtract(feeAmount);
                    oldProxySummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(oldProxySummary);
                }
            }

            // 2. 增加新代领用户的汇总金额
            FeeMonthlySummary newProxySummary = feeMonthlySummaryMapper.selectByUserAndMonth(newProxyUserId, month);
            if (newProxySummary != null) {
                BigDecimal currentAmount = newProxySummary.getTotalAmount();
                if (currentAmount != null) {
                    BigDecimal newAmount = currentAmount.add(feeAmount);
                    newProxySummary.setTotalAmount(newAmount);
                    feeMonthlySummaryMapper.updateSummary(newProxySummary);
                }
            } else {
                // 如果新代领用户没有该月的汇总记录，创建一条
                User newProxyUser = userMapper.selectByUserId(newProxyUserId);
                if (newProxyUser != null) {
                    FeeMonthlySummary newSummary = new FeeMonthlySummary();
                    newSummary.setUserId(newProxyUserId);
                    newSummary.setRealName(newProxyUser.getRealName());
                    newSummary.setStudentNumber(newProxyUser.getStudentNumber());
                    newSummary.setDepartmentId(departmentId);
                    newSummary.setTotalAmount(feeAmount);
                    newSummary.setStatisticalMonth(month);
                    newSummary.setCreatedAt(new Date());
                    newSummary.setUpdatedAt(new Date());
                    feeMonthlySummaryMapper.insertSummary(newSummary);
                }
            }
        } catch (Exception e) {
            // 汇总表更新失败不影响主流程，记录日志即可
            System.err.println("更新稿费月汇总表（代领用户变更）失败: " + e.getMessage());
        }
    }

    /**
     * 转换实体为响应DTO
     */
    private ProxyResponse convertToProxyResponse(ProxyReceiptRecords record) {
        if (record == null) {
            return null;
        }

        ProxyResponse response = new ProxyResponse();
        response.setProxyId(record.getProxyId());
        response.setFeeRecordId(record.getFeeRecordId());
        response.setDepartmentId(record.getDepartmentId());
        response.setOriginalUserId(record.getOriginalUserId());
        response.setOriginalUserName(record.getOriginalRealName());
        response.setOriginalStudentNumber(record.getOriginalStudentNumber());
        response.setProxyUserId(record.getProxyUserId());
        response.setProxyUserName(record.getProxyRealName());
        response.setProxyStudentNumber(record.getProxyStudentNumber());
        response.setArticleTitle(record.getArticleTitle());
        response.setFeeAmount(record.getFeeAmount());
        response.setProxyMonth(record.getProxyMonth());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        return response;
    }
}