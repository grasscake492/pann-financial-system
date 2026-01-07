//package org.test;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.dao.*;
//import org.dto.request.ProxyAddRequest;
//import org.dto.response.ApiResponse;
//import org.entity.*;
//import org.service.impl.ProxyServiceImpl;
//
//import java.lang.reflect.Method;
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * 代领系统中的部门关联逻辑测试
// * 主要测试代领操作中的部门区分和业务逻辑
// */
//@ExtendWith(MockitoExtension.class)
//class ProxyDepartmentTest {
//
//    @Mock
//    private ProxyReceiptRecordsMapper proxyMapper;
//
//    @Mock
//    private NewsDepartmentMonthlyMapper newsMapper;
//
//    @Mock
//    private EditorialDepartmentMonthlyMapper editorialMapper;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private DepartmentMapper departmentMapper;
//
//    @Mock
//    private FeeMonthlySummaryMapper feeSummaryMapper;
//
//    @InjectMocks
//    private ProxyServiceImpl proxyService;
//
//    private User originalUser;
//    private User proxyUser;
//    private Department newsDepartment;
//    private Department editorialDepartment;
//
//    @BeforeEach
//    void setUp() {
//        // 初始化用户数据
//        originalUser = new User();
//        originalUser.setUserId(1001L);
//        originalUser.setRealName("原始用户");
//        originalUser.setStudentNumber("20230001");
//
//        proxyUser = new User();
//        proxyUser.setUserId(1002L);
//        proxyUser.setRealName("代领用户");
//        proxyUser.setStudentNumber("20230002");
//
//        // 初始化部门数据
//        newsDepartment = new Department();
//        newsDepartment.setDepartmentId(1L);
//        newsDepartment.setDepartmentName("新闻部");
//
//        editorialDepartment = new Department();
//        editorialDepartment.setDepartmentId(2L);
//        editorialDepartment.setDepartmentName("编辑部");
//    }
//
//    @Test
//    void testExecuteProxy_NewsDepartment() throws Exception {
//        // 测试新闻部代领
//        testExecuteProxyForDepartment(1L, newsMapper, createNewsRecord());
//    }
//
//    @Test
//    void testExecuteProxy_EditorialDepartment() throws Exception {
//        // 测试编辑部代领
//        testExecuteProxyForDepartment(2L, editorialMapper, createEditorialRecord());
//    }
//
//    @Test
//    void testExecuteProxy_InvalidDepartment() {
//        // 测试无效部门ID
//        try {
//            Long proxyId = proxyService.executeProxy(
//                    1L,        // feeRecordId
//                    3L,        // 无效部门ID
//                    1001L,     // originalUserId
//                    1002L,     // proxyUserId
//                    "2023-10", // proxyMonth
//                    new BigDecimal("900.00"), // feeAmount
//                    1L         // adminId
//            );
//            fail("应该抛出异常");
//        } catch (RuntimeException e) {
//            assertTrue(e.getMessage().contains("不支持的部门ID") || e.getMessage().contains("部门"));
//        }
//    }
//
//    @Test
//    void testUpdateFeeRecord_NewsDepartment() throws Exception {
//        // 测试更新新闻部稿费记录（代领时）
//        Long feeRecordId = 1L;
//        Long departmentId = 1L;
//
//        NewsDepartmentMonthly newsRecord = createNewsRecord();
//        when(newsMapper.selectByRecordId(feeRecordId))
//                .thenReturn(newsRecord);
//        when(newsMapper.updateRecord(any(NewsDepartmentMonthly.class)))
//                .thenReturn(1);
//
//        // 由于updateFeeRecordUserInfo是私有方法，我们通过反射调用
//        Method method = ProxyServiceImpl.class.getDeclaredMethod(
//                "updateFeeRecordUserInfo",
//                Long.class, Long.class, Long.class, String.class, String.class
//        );
//        method.setAccessible(true);
//
//        method.invoke(proxyService, feeRecordId, departmentId,
//                proxyUser.getUserId(), proxyUser.getRealName(), proxyUser.getStudentNumber());
//
//        // 注意：updateFeeRecordUserInfo方法会调用selectByRecordId和updateRecord各一次
//        verify(newsMapper, times(1)).selectByRecordId(feeRecordId);
//        verify(newsMapper, times(1)).updateRecord(any(NewsDepartmentMonthly.class));
//    }
//
//    @Test
//    void testUpdateFeeRecord_EditorialDepartment() throws Exception {
//        // 测试更新编辑部稿费记录（代领时）
//        Long feeRecordId = 2L;
//        Long departmentId = 2L;
//
//        EditorialDepartmentMonthly editorialRecord = createEditorialRecord();
//        when(editorialMapper.selectByRecordId(feeRecordId))
//                .thenReturn(editorialRecord);
//        when(editorialMapper.updateRecord(any(EditorialDepartmentMonthly.class)))
//                .thenReturn(1);
//
//        // 由于updateFeeRecordUserInfo是私有方法，我们通过反射调用
//        Method method = ProxyServiceImpl.class.getDeclaredMethod(
//                "updateFeeRecordUserInfo",
//                Long.class, Long.class, Long.class, String.class, String.class
//        );
//        method.setAccessible(true);
//
//        method.invoke(proxyService, feeRecordId, departmentId,
//                proxyUser.getUserId(), proxyUser.getRealName(), proxyUser.getStudentNumber());
//
//        verify(editorialMapper, times(1)).selectByRecordId(feeRecordId);
//        verify(editorialMapper, times(1)).updateRecord(any(EditorialDepartmentMonthly.class));
//    }
//
//    @Test
//    void testRevokeProxy_NewsDepartment() {
//        // 测试撤销新闻部代领记录
//        testRevokeProxyForDepartment(1L);
//    }
//
//    @Test
//    void testRevokeProxy_EditorialDepartment() {
//        // 测试撤销编辑部代领记录
//        testRevokeProxyForDepartment(2L);
//    }
//
//    @Test
//    void testQueryProxyByDepartment() {
//        // 测试按部门查询代领记录
//        Long departmentId = 1L;
//
//        ProxyReceiptRecords proxyRecord1 = createProxyRecord();
//        proxyRecord1.setDepartmentId(departmentId);
//
//        ProxyReceiptRecords proxyRecord2 = createProxyRecord();
//        proxyRecord2.setProxyId(2L);
//        proxyRecord2.setDepartmentId(departmentId);
//
//        when(proxyMapper.selectByDepartmentId(departmentId))
//                .thenReturn(Arrays.asList(proxyRecord1, proxyRecord2));
//
//        // 执行查询
//        List<ProxyReceiptRecords> results = proxyMapper.selectByDepartmentId(departmentId);
//
//        // 验证
//        assertEquals(2, results.size());
//        for (ProxyReceiptRecords record : results) {
//            assertEquals(departmentId, record.getDepartmentId());
//        }
//    }
//
//    @Test
//    void testGetArticleTitle_ByDepartment() throws Exception {
//        // 测试根据部门ID获取文章标题
//        Long newsRecordId = 1L;
//        Long editorialRecordId = 2L;
//
//        NewsDepartmentMonthly newsRecord = createNewsRecord();
//        EditorialDepartmentMonthly editorialRecord = createEditorialRecord();
//
//        when(newsMapper.selectByRecordId(newsRecordId))
//                .thenReturn(newsRecord);
//        when(editorialMapper.selectByRecordId(editorialRecordId))
//                .thenReturn(editorialRecord);
//
//        // 由于getArticleTitleFromFeeRecord是私有方法，我们通过反射调用
//        Method method = ProxyServiceImpl.class.getDeclaredMethod(
//                "getArticleTitleFromFeeRecord",
//                Long.class, Long.class
//        );
//        method.setAccessible(true);
//
//        // 测试新闻部
//        String newsTitle = (String) method.invoke(proxyService, newsRecordId, 1L);
//        assertEquals("新闻部测试稿件", newsTitle);
//
//        // 测试编辑部
//        String editorialTitle = (String) method.invoke(proxyService, editorialRecordId, 2L);
//        assertEquals("编辑部测试稿件", editorialTitle);
//    }
//
//    @Test
//    void testProxyRuleValidation_ByDepartment() {
//        // 测试不同部门的代领规则验证
//        Long originalUserId = 1001L;
//        Long proxyUserId = 1002L;
//        BigDecimal feeAmount = new BigDecimal("900.00");
//        String month = "2023-10";
//
//        // 模拟稿费记录查询 - 用户已有一定稿费
//        when(newsMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.singletonList(createNewsRecord()));
//        when(editorialMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.singletonList(createEditorialRecord()));
//
//        // 验证代领规则（应该返回true，因为新闻部300 + 编辑部300 + 本次900 = 1500 > 800）
//        boolean isValid = proxyService.validateProxyRule(
//                originalUserId, proxyUserId, feeAmount, month);
//
//        assertTrue(isValid, "超过800元应该需要代领");
//    }
//
//    // 测试代领规则 - 勤工助学学生
//    @Test
//    void testProxyRuleValidation_WorkStudyStudent() {
//        Long originalUserId = 1001L;
//        Long proxyUserId = 1002L;
//        BigDecimal feeAmount = new BigDecimal("100.00"); // 金额很小
//        String month = "2023-10";
//
//        // 模拟稿费记录查询 - 用户已有一定稿费
//        when(newsMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.emptyList());
//        when(editorialMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.emptyList());
//
//        // 调用validateProxyRule方法
//        boolean isValid = proxyService.validateProxyRule(
//                originalUserId, proxyUserId, feeAmount, month);
//
//        // 由于金额100 < 800，且用户不是勤工助学，所以应该返回false
//        assertFalse(isValid, "金额小于800元且不是勤工助学学生不需要代领");
//    }
//
//    // 测试代领规则 - 金额小于800元
//    @Test
//    void testProxyRuleValidation_SmallAmount() {
//        Long originalUserId = 1001L;
//        Long proxyUserId = 1002L;
//        BigDecimal feeAmount = new BigDecimal("500.00"); // 金额小于800
//        String month = "2023-10";
//
//        // 模拟稿费记录查询 - 用户没有其他稿费
//        when(newsMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.emptyList());
//        when(editorialMapper.selectByUserAndMonth(originalUserId, month))
//                .thenReturn(Collections.emptyList());
//
//        // 调用validateProxyRule方法
//        boolean isValid = proxyService.validateProxyRule(
//                originalUserId, proxyUserId, feeAmount, month);
//
//        // 金额500 < 800，应该返回false
//        assertFalse(isValid, "金额小于800元不需要代领");
//    }
//
//    // 辅助方法
//    private void testExecuteProxyForDepartment(
//            Long departmentId,
//            Object mapper,
//            Object record) {
//
//        // 准备测试数据 - 使用大金额触发代领规则
//        Long feeRecordId = departmentId == 1L ? 1L : 2L;
//        BigDecimal feeAmount = new BigDecimal("900.00"); // 金额大于800，触发代领规则
//        String proxyMonth = "2023-10";
//        Long originalUserId = 1001L;
//        Long proxyUserId = 1002L;
//        Long adminId = 1L;
//
//        // 模拟用户查询
//        when(userMapper.selectByUserId(originalUserId))
//                .thenReturn(originalUser);
//        when(userMapper.selectByUserId(proxyUserId))
//                .thenReturn(proxyUser);
//
//        // 模拟稿费记录查询 - 注意：在executeProxy中selectByRecordId会被调用两次：
//        // 1. 在getArticleTitleFromFeeRecord中
//        // 2. 在updateFeeRecordUserInfo中
//        if (departmentId == 1L) {
//            NewsDepartmentMonthly newsRecord = (NewsDepartmentMonthly) record;
//            when(newsMapper.selectByRecordId(feeRecordId))
//                    .thenReturn(newsRecord);
//            when(newsMapper.updateRecord(any(NewsDepartmentMonthly.class)))
//                    .thenReturn(1);
//        } else if (departmentId == 2L) {
//            EditorialDepartmentMonthly editorialRecord = (EditorialDepartmentMonthly) record;
//            when(editorialMapper.selectByRecordId(feeRecordId))
//                    .thenReturn(editorialRecord);
//            when(editorialMapper.updateRecord(any(EditorialDepartmentMonthly.class)))
//                    .thenReturn(1);
//        }
//
//        // 模拟代领记录插入 - 关键修复：设置proxyId
//        when(proxyMapper.insertProxyRecord(any(ProxyReceiptRecords.class))).thenAnswer(invocation -> {
//            ProxyReceiptRecords proxyRecord = invocation.getArgument(0);
//            proxyRecord.setProxyId(100L); // 设置一个模拟的proxyId
//            return 1; // 返回插入成功
//        });
//
//        // 模拟稿费月汇总查询 - 假设没有汇总记录
//        when(feeSummaryMapper.selectByUserAndMonth(anyLong(), anyString()))
//                .thenReturn(null);
//
//        // 模拟汇总记录插入
//        when(feeSummaryMapper.insertSummary(any(FeeMonthlySummary.class)))
//                .thenReturn(1);
//
//        // 模拟validateProxyRule方法 - 用户没有其他稿费记录
//        when(newsMapper.selectByUserAndMonth(originalUserId, proxyMonth))
//                .thenReturn(Collections.emptyList());
//        when(editorialMapper.selectByUserAndMonth(originalUserId, proxyMonth))
//                .thenReturn(Collections.emptyList());
//
//        try {
//            // 执行代领
//            Long proxyId = proxyService.executeProxy(
//                    feeRecordId,
//                    departmentId,
//                    originalUserId,
//                    proxyUserId,
//                    proxyMonth,
//                    feeAmount,
//                    adminId
//            );
//
//            assertNotNull(proxyId, "代理ID不应该为空");
//            assertEquals(100L, proxyId, "代理ID应该为100");
//
//            // 验证正确的Mapper被调用 - 注意：selectByRecordId会被调用2次
//            if (departmentId == 1L) {
//                verify(newsMapper, times(2)).selectByRecordId(feeRecordId);
//                verify(newsMapper, times(1)).updateRecord(any(NewsDepartmentMonthly.class));
//            } else if (departmentId == 2L) {
//                verify(editorialMapper, times(2)).selectByRecordId(feeRecordId);
//                verify(editorialMapper, times(1)).updateRecord(any(EditorialDepartmentMonthly.class));
//            }
//
//            // 关键修复：userMapper.selectByUserId 被调用了4次
//            // 1. isWorkStudyStudent 中调用一次 (originalUserId)
//            // 2. executeProxy 中获取原始用户信息 (originalUserId)
//            // 3. executeProxy 中获取代领用户信息 (proxyUserId)
//            // 4. updateFeeMonthlySummary 中获取代领用户信息 (proxyUserId)
//            verify(userMapper, times(2)).selectByUserId(originalUserId);
//            verify(userMapper, times(2)).selectByUserId(proxyUserId);
//
//            verify(proxyMapper).insertProxyRecord(any(ProxyReceiptRecords.class));
//
//        } catch (Exception e) {
//            fail("代领操作应该成功: " + e.getMessage());
//        }
//    }
//
//    private void testRevokeProxyForDepartment(Long departmentId) {
//        Long proxyId = 1L;
//        Long adminId = 1L;
//
//        // 创建代领记录
//        ProxyReceiptRecords proxyRecord = createProxyRecord();
//        proxyRecord.setDepartmentId(departmentId);
//
//        // 模拟查询
//        when(proxyMapper.selectByProxyId(proxyId))
//                .thenReturn(proxyRecord);
//        when(userMapper.selectByUserId(proxyRecord.getOriginalUserId()))
//                .thenReturn(originalUser);
//
//        // 模拟稿费记录恢复
//        if (departmentId == 1L) {
//            NewsDepartmentMonthly newsRecord = createNewsRecord();
//            when(newsMapper.selectByRecordId(proxyRecord.getFeeRecordId()))
//                    .thenReturn(newsRecord);
//            when(newsMapper.updateRecord(any(NewsDepartmentMonthly.class)))
//                    .thenReturn(1);
//        } else if (departmentId == 2L) {
//            EditorialDepartmentMonthly editorialRecord = createEditorialRecord();
//            when(editorialMapper.selectByRecordId(proxyRecord.getFeeRecordId()))
//                    .thenReturn(editorialRecord);
//            when(editorialMapper.updateRecord(any(EditorialDepartmentMonthly.class)))
//                    .thenReturn(1);
//        }
//
//        // 模拟删除代领记录
//        when(proxyMapper.deleteByProxyId(proxyId))
//                .thenReturn(1);
//
//        // 模拟稿费月汇总查询
//        FeeMonthlySummary summary = new FeeMonthlySummary();
//        summary.setTotalAmount(new BigDecimal("300.00"));
//        when(feeSummaryMapper.selectByUserAndMonth(anyLong(), anyString()))
//                .thenReturn(summary);
//        when(feeSummaryMapper.updateSummary(any(FeeMonthlySummary.class)))
//                .thenReturn(1);
//
//        try {
//            // 执行撤销 - 使用revokeProxyRecord方法（公共方法）
//            ApiResponse<Void> response = proxyService.revokeProxyRecord(proxyId, adminId);
//
//            // 验证响应 - 使用isSuccess()方法
//            assertNotNull(response);
//            assertTrue(response.isSuccess(), "撤销操作应该成功");
//
//            // 验证正确的Mapper被调用
//            if (departmentId == 1L) {
//                verify(newsMapper).updateRecord(any(NewsDepartmentMonthly.class));
//            } else if (departmentId == 2L) {
//                verify(editorialMapper).updateRecord(any(EditorialDepartmentMonthly.class));
//            }
//
//            verify(proxyMapper).deleteByProxyId(proxyId);
//
//        } catch (Exception e) {
//            fail("撤销代领应该成功: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testQueryProxyByDepartment_ServiceMethod() {
//        // 测试ProxyServiceImpl中的queryProxyByDepartment方法
//        Long departmentId = 1L;
//        String month = "2023-10";
//        int page = 1;
//        int size = 10;
//
//        // 模拟分页查询
//        when(proxyMapper.selectByPage(anyInt(), anyInt()))
//                .thenReturn(Arrays.asList(createProxyRecord()));
//
//        // 调用服务方法
//        List<ProxyReceiptRecords> results = proxyService.queryProxyByDepartment(departmentId, month, page, size);
//
//        // 验证
//        assertNotNull(results);
//        verify(proxyMapper).selectByPage(anyInt(), anyInt());
//    }
//
//    @Test
//    void testQueryProxyByUser_ServiceMethod() {
//        // 测试ProxyServiceImpl中的queryProxyByUser方法
//        Long userId = 1001L;
//        String month = "2023-10";
//        int page = 1;
//        int size = 10;
//
//        // 模拟查询原始用户的代领记录
//        when(proxyMapper.selectByOriginalUser(userId))
//                .thenReturn(Arrays.asList(createProxyRecord()));
//
//        // 调用服务方法 - 作为原始用户查询
//        List<ProxyReceiptRecords> results = proxyService.queryProxyByUser(userId, true, month, page, size);
//
//        // 验证
//        assertNotNull(results);
//        verify(proxyMapper).selectByOriginalUser(userId);
//    }
//
//    @Test
//    void testQueryProxyByUser_AsProxyUser() {
//        // 测试ProxyServiceImpl中的queryProxyByUser方法 - 作为代领用户查询
//        Long userId = 1002L;
//        String month = "2023-10";
//        int page = 1;
//        int size = 10;
//
//        // 模拟查询代领用户的代领记录
//        when(proxyMapper.selectByProxyUser(userId))
//                .thenReturn(Arrays.asList(createProxyRecord()));
//
//        // 调用服务方法 - 作为代领用户查询
//        List<ProxyReceiptRecords> results = proxyService.queryProxyByUser(userId, false, month, page, size);
//
//        // 验证
//        assertNotNull(results);
//        verify(proxyMapper).selectByProxyUser(userId);
//    }
//
//    @Test
//    void testExecuteProxy_SmallAmountShouldFail() {
//        // 测试金额小于800时代领应该失败
//        Long feeRecordId = 1L;
//        Long departmentId = 1L;
//        BigDecimal feeAmount = new BigDecimal("100.00"); // 金额小于800
//        String proxyMonth = "2023-10";
//        Long originalUserId = 1001L;
//        Long proxyUserId = 1002L;
//        Long adminId = 1L;
//
//        // 模拟用户查询 - 使用lenient避免不必要的桩定义警告
//        lenient().when(userMapper.selectByUserId(originalUserId))
//                .thenReturn(originalUser);
//        lenient().when(userMapper.selectByUserId(proxyUserId))
//                .thenReturn(proxyUser);
//
//        // 模拟稿费记录查询 - 用户没有其他稿费
//        when(newsMapper.selectByUserAndMonth(originalUserId, proxyMonth))
//                .thenReturn(Collections.emptyList());
//        when(editorialMapper.selectByUserAndMonth(originalUserId, proxyMonth))
//                .thenReturn(Collections.emptyList());
//
//        try {
//            Long proxyId = proxyService.executeProxy(
//                    feeRecordId,
//                    departmentId,
//                    originalUserId,
//                    proxyUserId,
//                    proxyMonth,
//                    feeAmount,
//                    adminId
//            );
//            fail("金额小于800元应该抛出异常");
//        } catch (RuntimeException e) {
//            assertTrue(e.getMessage().contains("不符合代领规则") || e.getMessage().contains("代领规则"));
//        }
//    }
//
//    private ProxyAddRequest createProxyAddRequest() {
//        ProxyAddRequest request = new ProxyAddRequest();
//        request.setFeeRecordId(1L);
//        request.setDepartmentId(1L);
//        request.setOriginalUserId(1001L);
//        request.setProxyUserId(1002L);
//        request.setProxyMonth("2023-10");
//        request.setFeeAmount(new BigDecimal("900.00"));
//        return request;
//    }
//
//    private ProxyReceiptRecords createProxyRecord() {
//        ProxyReceiptRecords record = new ProxyReceiptRecords();
//        record.setProxyId(1L);
//        record.setFeeRecordId(1L);
//        record.setDepartmentId(1L);
//        record.setOriginalUserId(1001L);
//        record.setOriginalRealName("原始用户");
//        record.setOriginalStudentNumber("20230001");
//        record.setProxyUserId(1002L);
//        record.setProxyRealName("代领用户");
//        record.setProxyStudentNumber("20230002");
//        record.setArticleTitle("测试稿件");
//        record.setFeeAmount(new BigDecimal("900.00"));
//        record.setProxyMonth("2023-10");
//        record.setCreatedAt(new Date());
//        return record;
//    }
//
//    private NewsDepartmentMonthly createNewsRecord() {
//        NewsDepartmentMonthly record = new NewsDepartmentMonthly();
//        record.setRecordId(1L);
//        record.setDepartmentId(1L);
//        record.setUserIds("[1001]");
//        record.setRealNames("[\"原始用户\"]");
//        record.setStudentNumbers("[\"20230001\"]");
//        record.setArticleTitle("新闻部测试稿件");
//        record.setFeeAmount(new BigDecimal("300.00"));
//        record.setStatisticalMonth("2023-10");
//        return record;
//    }
//
//    private EditorialDepartmentMonthly createEditorialRecord() {
//        EditorialDepartmentMonthly record = new EditorialDepartmentMonthly();
//        record.setRecordId(2L);
//        record.setDepartmentId(2L);
//        record.setUserIds("[1001]");
//        record.setRealNames("[\"原始用户\"]");
//        record.setStudentNumbers("[\"20230001\"]");
//        record.setArticleTitle("编辑部测试稿件");
//        record.setFeeAmount(new BigDecimal("300.00"));
//        record.setStatisticalMonth("2023-10");
//        return record;
//    }
//}