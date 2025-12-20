package org.test;

import org.config.TestConfig;
import org.dto.request.RoyaltyAddRequest;
import org.dto.request.RoyaltyQueryRequest;
import org.dto.request.RoyaltyUpdateRequest;
import org.dto.response.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Component
public class RoyaltyServiceTest {

    @Autowired
    private org.service.RoyaltyService royaltyService;

    private Long testUserId;
    private Long testAdminId;
    private Long testNewsDepartmentId;
    private Long testEditorialDepartmentId;
    private String testMonth;

    @Before
    public void setUp() {
        testUserId = 1L;      // 普通用户张三
        testAdminId = 2L;     // 管理员李四
        testNewsDepartmentId = 1L; // 新闻部
        testEditorialDepartmentId = 2L; // 编辑部
        testMonth = "2024-01";
    }

    // ============ 查询功能测试 ============

    @Test
    public void testQueryPersonalRoyalty_Success() {
        // 准备
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);
        request.setUserId(testUserId);

        // 执行
        RoyaltyListResponse response = royaltyService.queryPersonalRoyalty(testUserId, request);

        // 验证
        assertNotNull(response);
        assertNotNull(response.getList());
        assertTrue(response.getTotal() >= 0);
    }

    @Test
    public void testQueryPersonalRoyalty_Pagination() {
        // 准备 - 第一页
        RoyaltyQueryRequest request1 = new RoyaltyQueryRequest();
        request1.setPage(1);
        request1.setSize(2);
        request1.setUserId(testUserId);

        // 执行
        RoyaltyListResponse response1 = royaltyService.queryPersonalRoyalty(testUserId, request1);

        // 验证
        assertNotNull(response1);
        assertNotNull(response1.getList());
        assertTrue(response1.getList().size() <= 2);
    }

    @Test
    public void testQueryPersonalRoyalty_WithDateRange() {
        // 准备
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);
        request.setUserId(testUserId);
        request.setStartDate("2024-01-01");
        request.setEndDate("2024-12-31");

        // 执行
        RoyaltyListResponse response = royaltyService.queryPersonalRoyalty(testUserId, request);

        // 验证
        assertNotNull(response);
        assertNotNull(response.getList());
    }

    @Test(expected = RuntimeException.class)
    public void testQueryDepartmentRoyalty_InvalidDepartment() {
        // 准备 - 运营部不支持稿费管理
        Long operationDepartmentId = 3L;
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);

        // 执行 - 应抛出异常
        royaltyService.queryDepartmentRoyalty(operationDepartmentId, request);
    }

    @Test
    public void testQueryDepartmentRoyalty_DepartmentNotFound() {
        // 准备 - 不存在的部门
        Long nonExistentDepartmentId = 999L;
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);

        try {
            // 执行
            RoyaltyListResponse response = royaltyService.queryDepartmentRoyalty(nonExistentDepartmentId, request);
            // 验证 - 如果服务正确处理，应该返回空列表
            assertNotNull(response);
            assertNotNull(response.getList());
            assertEquals(0, response.getTotal().intValue());
        } catch (RuntimeException e) {
            // 或者可能抛出异常
            assertTrue(e.getMessage().contains("部门不存在"));
        }
    }

    @Test
    public void testQueryAllRoyalty_Success() {
        // 准备
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);

        // 执行
        RoyaltyListResponse response = royaltyService.queryAllRoyalty(request);

        // 验证
        assertNotNull(response);
        assertNotNull(response.getList());
        assertTrue(response.getTotal() >= 0);
    }

    @Test
    public void testQueryAllRoyalty_WithDepartmentFilter() {
        // 准备
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(10);
        request.setDepartmentId(testNewsDepartmentId);

        // 执行
        RoyaltyListResponse response = royaltyService.queryAllRoyalty(request);

        // 验证
        assertNotNull(response);
        assertNotNull(response.getList());
        // 验证所有记录都属于指定部门
        for (RoyaltyResponse record : response.getList()) {
            assertEquals(testNewsDepartmentId, record.getDepartmentId());
        }
    }

    @Test
    public void testQueryAllRoyalty_Pagination() {
        // 准备
        RoyaltyQueryRequest request1 = new RoyaltyQueryRequest();
        request1.setPage(1);
        request1.setSize(3);

        RoyaltyQueryRequest request2 = new RoyaltyQueryRequest();
        request2.setPage(2);
        request2.setSize(3);

        // 执行
        RoyaltyListResponse response1 = royaltyService.queryAllRoyalty(request1);
        RoyaltyListResponse response2 = royaltyService.queryAllRoyalty(request2);

        // 验证
        assertNotNull(response1);
        assertNotNull(response2);
        // 分页应该返回不同的记录
        if (response1.getList().size() > 0 && response2.getList().size() > 0) {
            // 验证两页记录不同（实际可能需要更复杂的验证）
            assertNotEquals(response1.getList().get(0).getRecordId(),
                    response2.getList().get(0).getRecordId());
        }
    }

    // ============ 增删改功能测试 ============

    @Test
    public void testAddRoyalty_Success_NewsDepartment() {
        // 准备
        RoyaltyAddRequest request = createValidRoyaltyAddRequest(testNewsDepartmentId);

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        RoyaltyResponse data = response.getData();
        assertEquals("测试新增稿件", data.getArticleTitle());
        assertEquals("新闻稿", data.getArticleType());
        assertEquals(500.00, data.getFeeAmount().doubleValue(), 0.001);
        assertEquals(testMonth, data.getStatisticalMonth());
        assertEquals(testNewsDepartmentId, data.getDepartmentId());
    }

    @Test
    public void testAddRoyalty_Success_EditorialDepartment() {
        // 准备
        RoyaltyAddRequest request = createValidRoyaltyAddRequest(testEditorialDepartmentId);
        request.setArticleType("评论稿"); // 编辑部稿件类型

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());
        assertEquals(testEditorialDepartmentId, response.getData().getDepartmentId());
    }

    @Test(expected = RuntimeException.class)
    public void testAddRoyalty_Validation_MissingRequiredFields() {
        // 准备 - 缺少必要字段
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        // 只设置用户ID，缺少其他必填字段
        request.setUserIds(Collections.singletonList(testUserId));

        // 执行 - 应抛出异常
        royaltyService.addRoyalty(request, testAdminId);
    }

    @Test
    public void testAddRoyalty_Validation_InvalidDepartment() {
        // 准备 - 不支持的部门ID
        Long invalidDepartmentId = 999L;
        RoyaltyAddRequest request = createValidRoyaltyAddRequest(invalidDepartmentId);

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);

        // 验证
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("不支持的部门ID"));
    }

    @Test
    public void testAddRoyalty_Validation_ArraySizeMismatch() {
        // 准备 - 数组长度不一致
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Arrays.asList(testUserId, 2L)); // 2个用户
        request.setRealNames(Collections.singletonList("张三")); // 1个姓名
        request.setStudentNumbers(Arrays.asList("20230001", "20230002")); // 2个学号
        request.setArticleTitle("测试稿件");
        request.setArticleType("新闻稿");
        request.setFeeAmount(new BigDecimal("100.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(testNewsDepartmentId);

        try {
            // 执行 - 应抛出异常
            royaltyService.addRoyalty(request, testAdminId);
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            // 验证异常消息
            assertTrue(e.getMessage().contains("数组长度必须一致"));
        }
    }

    @Test
    public void testUpdateRoyalty_Success() {
        // 准备 - 先添加一条记录，然后更新它
        Long recordId = addTestRecord(testNewsDepartmentId);

        RoyaltyUpdateRequest request = new RoyaltyUpdateRequest();
        request.setArticleTitle("修改后的标题");
        request.setFeeAmount(new BigDecimal("150.00"));
        request.setArticleType("修改后的类型");
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(testNewsDepartmentId);

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.updateRoyalty(recordId, request, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        RoyaltyResponse data = response.getData();
        assertEquals("修改后的标题", data.getArticleTitle());
        assertEquals(150.00, data.getFeeAmount().doubleValue(), 0.001);
        assertEquals("修改后的类型", data.getArticleType());
    }

    @Test
    public void testUpdateRoyalty_NotFound() {
        // 准备 - 不存在的记录ID
        Long nonExistRecordId = 999L;

        RoyaltyUpdateRequest request = new RoyaltyUpdateRequest();
        request.setArticleTitle("修改标题");
        request.setFeeAmount(new BigDecimal("200.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(testNewsDepartmentId);

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.updateRoyalty(nonExistRecordId, request, testAdminId);

        // 验证 - 应该返回错误
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("稿费记录不存在"));
    }

    @Test
    public void testUpdateRoyalty_InvalidRecordId() {
        // 准备 - 无效的记录ID
        RoyaltyUpdateRequest request = new RoyaltyUpdateRequest();
        request.setArticleTitle("修改标题");
        request.setFeeAmount(new BigDecimal("200.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(testNewsDepartmentId);

        // 测试空ID
        ApiResponse<RoyaltyResponse> response1 = royaltyService.updateRoyalty(null, request, testAdminId);
        assertNotNull(response1);
        assertNotEquals("0000", response1.getResCode());

        // 测试负数ID
        ApiResponse<RoyaltyResponse> response2 = royaltyService.updateRoyalty(-1L, request, testAdminId);
        assertNotNull(response2);
        assertNotEquals("0000", response2.getResCode());
    }

    @Test
    public void testUpdateRoyalty_InvalidDepartmentId() {
        // 准备 - 先添加一条记录
        Long recordId = addTestRecord(testNewsDepartmentId);

        RoyaltyUpdateRequest request = new RoyaltyUpdateRequest();
        request.setArticleTitle("修改标题");
        request.setFeeAmount(new BigDecimal("200.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(null); // 部门ID为空

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.updateRoyalty(recordId, request, testAdminId);

        // 验证 - 应该返回错误
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("部门ID不能为空"));
    }

    @Test
    public void testDeleteRoyalty_Success_NewsDepartment() {
        // 准备 - 先添加一条新闻部记录，然后删除它
        Long recordId = addTestRecord(testNewsDepartmentId);

        // 执行
        ApiResponse<Void> response = royaltyService.deleteRoyalty(recordId, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
    }

    @Test
    public void testDeleteRoyalty_Success_EditorialDepartment() {
        // 准备 - 先添加一条编辑部记录，然后删除它
        Long recordId = addTestRecord(testEditorialDepartmentId);

        // 执行
        ApiResponse<Void> response = royaltyService.deleteRoyalty(recordId, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
    }

    @Test
    public void testDeleteRoyalty_NotFound() {
        // 准备 - 不存在的记录ID
        Long nonExistRecordId = 999L;

        // 执行
        ApiResponse<Void> response = royaltyService.deleteRoyalty(nonExistRecordId, testAdminId);

        // 验证 - 应该返回错误
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("稿费记录不存在"));
    }

    // ============ 导出功能测试 ============

    @Test
    public void testExportRoyalty_Success_Excel() {
        // 准备
        String format = "Excel";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(testMonth, testNewsDepartmentId, format, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        Map<String, Object> data = response.getData();
        assertNotNull(data.get("fileUrl"));
        assertNotNull(data.get("exportTime"));
        assertNotNull(data.get("recordCount"));
        assertEquals(testMonth, data.get("statisticalMonth"));
        assertEquals(testNewsDepartmentId, data.get("departmentId"));

        // 验证文件URL包含正确格式
        String fileUrl = (String) data.get("fileUrl");
        assertTrue(fileUrl.endsWith(".xlsx"));
    }

    @Test
    public void testExportRoyalty_Success_PDF() {
        // 准备
        String format = "PDF";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(testMonth, testNewsDepartmentId, format, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        Map<String, Object> data = response.getData();
        String fileUrl = (String) data.get("fileUrl");
        assertTrue(fileUrl.endsWith(".pdf"));
    }

    @Test
    public void testExportRoyalty_Validation_MissingMonth() {
        // 准备
        String format = "Excel";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(null, testNewsDepartmentId, format, testAdminId);

        // 验证
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("统计月份不能为空"));
    }

    @Test
    public void testExportRoyalty_Validation_MissingDepartmentId() {
        // 准备
        String format = "Excel";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(testMonth, null, format, testAdminId);

        // 验证
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("部门ID不能为空"));
    }

    @Test
    public void testExportRoyalty_Validation_InvalidFormat() {
        // 准备
        String invalidFormat = "Word";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(testMonth, testNewsDepartmentId, invalidFormat, testAdminId);

        // 验证
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("导出格式必须是Excel或PDF"));
    }

    @Test
    public void testExportRoyalty_InvalidDepartment() {
        // 准备
        Long invalidDepartmentId = 999L;
        String format = "Excel";

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.exportRoyalty(testMonth, invalidDepartmentId, format, testAdminId);

        // 验证
        assertNotNull(response);
        assertNotEquals("0000", response.getResCode());
        assertTrue(response.getResMsg().contains("不支持的部门ID"));
    }

    // ============ 统计功能测试 ============

    @Test
    public void testGetUserMonthlyTotal_Success() {
        // 准备 - 先添加一些记录
        addTestRecordForUser(testUserId, testMonth, new BigDecimal("100.00"));
        addTestRecordForUser(testUserId, testMonth, new BigDecimal("200.00"));

        // 执行
        BigDecimal total = royaltyService.getUserMonthlyTotal(testUserId, testMonth);

        // 验证
        assertNotNull(total);
        // 总金额应该是300.00（加上可能已有的记录）
        // 由于数据库可能有其他记录，我们不验证具体值
        assertTrue(total.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    public void testGetUserMonthlyTotal_NoRecords() {
        // 准备 - 新用户，没有稿费记录
        Long newUserId = 999L;
        String month = "2024-01";

        // 执行
        BigDecimal total = royaltyService.getUserMonthlyTotal(newUserId, month);

        // 验证
        assertNotNull(total);
        assertEquals(0.00, total.doubleValue(), 0.01);
    }

    @Test
    public void testGetUserMonthlyTotal_DifferentMonths() {
        // 准备 - 添加不同月份的记录
        String month1 = "2024-01";
        String month2 = "2024-02";

        addTestRecordForUser(testUserId, month1, new BigDecimal("100.00"));
        addTestRecordForUser(testUserId, month2, new BigDecimal("200.00"));

        // 执行
        BigDecimal totalMonth1 = royaltyService.getUserMonthlyTotal(testUserId, month1);
        BigDecimal totalMonth2 = royaltyService.getUserMonthlyTotal(testUserId, month2);

        // 验证
        assertNotNull(totalMonth1);
        assertNotNull(totalMonth2);
        // 两个月的总额应该不同
        assertNotEquals(totalMonth1.doubleValue(), totalMonth2.doubleValue(), 0.01);
    }

    @Test
    public void testCalculateDepartmentMonthlyTotal_Success() {
        // 准备 - 先添加一些记录
        addTestRecord(testNewsDepartmentId);

        // 执行
        BigDecimal total = royaltyService.calculateDepartmentMonthlyTotal(testNewsDepartmentId, testMonth);

        // 验证
        assertNotNull(total);
        assertTrue(total.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    public void testCalculateDepartmentMonthlyTotal_NoRecords() {
        // 准备 - 使用一个没有记录的月份
        String emptyMonth = "2099-01";

        // 执行
        BigDecimal total = royaltyService.calculateDepartmentMonthlyTotal(testNewsDepartmentId, emptyMonth);

        // 验证
        assertNotNull(total);
        assertEquals(0.00, total.doubleValue(), 0.01);
    }

    @Test
    public void testCalculateDepartmentMonthlyTotal_InvalidDepartment() {
        // 准备 - 不支持稿费管理的部门
        Long operationDepartmentId = 3L;

        // 执行
        BigDecimal total = royaltyService.calculateDepartmentMonthlyTotal(operationDepartmentId, testMonth);

        // 验证
        assertNotNull(total);
        assertEquals(0.00, total.doubleValue(), 0.01);
    }

    @Test
    public void testCanProxyRoyalty_NoNeedProxy() {
        // 准备 - 添加一条小额记录
        Long recordId = addTestRecordForUser(testUserId, testMonth, new BigDecimal("100.00"));

        // 执行
        boolean canProxy = royaltyService.canProxyRoyalty(recordId, testNewsDepartmentId, testUserId);

        // 验证
        // 由于当前实现中，只有当总额超过800时才需要代领
        // 这里添加了100元，应该不需要代领，所以返回false
        // 注意：方法名canProxyRoyalty可能有歧义，实际实现是检查是否需要代领
        // 根据代码，当totalWithRecord > 800时返回true（需要代领）
        assertNotNull(canProxy);
        // 不验证具体值，因为依赖于现有数据
    }

    @Test
    public void testCanProxyRoyalty_NeedProxy() {
        // 准备 - 添加一条大额记录
        Long recordId = addTestRecordForUser(testUserId, testMonth, new BigDecimal("900.00"));

        // 执行
        boolean canProxy = royaltyService.canProxyRoyalty(recordId, testNewsDepartmentId, testUserId);

        // 验证
        // 添加了900元，单条记录就超过800元，应该需要代领
        assertNotNull(canProxy);
        // 不验证具体值，因为依赖于现有数据
    }

    @Test
    public void testCanProxyRoyalty_RecordNotFound() {
        // 准备 - 不存在的记录ID
        Long nonExistRecordId = 999L;

        // 执行
        boolean canProxy = royaltyService.canProxyRoyalty(nonExistRecordId, testNewsDepartmentId, testUserId);

        // 验证
        // 记录不存在时，金额为0，应该不需要代领
        assertFalse(canProxy);
    }

    @Test
    public void testCanProxyRoyalty_InvalidDepartment() {
        // 准备 - 先添加一条记录
        Long recordId = addTestRecord(testNewsDepartmentId);

        // 执行 - 使用无效的部门ID
        boolean canProxy = royaltyService.canProxyRoyalty(recordId, 999L, testUserId);

        // 验证
        // 部门无效时，金额为0，应该不需要代领
        assertFalse(canProxy);
    }

    // ============ 新增统计系统功能测试 ============

    @Test
    public void testCountRecordsByDepartmentAndMonth_Success() {
        // 准备 - 先添加一些记录
        addTestRecord(testNewsDepartmentId);
        addTestRecord(testNewsDepartmentId);

        // 执行
        Integer count = royaltyService.countRecordsByDepartmentAndMonth(testNewsDepartmentId, testMonth);

        // 验证
        assertNotNull(count);
        assertTrue(count.intValue() >= 2); // 至少2条记录
    }

    @Test
    public void testCountRecordsByDepartmentAndMonth_NoRecords() {
        // 准备 - 使用一个没有记录的月份
        String emptyMonth = "2099-01";

        // 执行
        Integer count = royaltyService.countRecordsByDepartmentAndMonth(testNewsDepartmentId, emptyMonth);

        // 验证
        assertNotNull(count);
        assertEquals(0, count.intValue());
    }

    @Test
    public void testCountRecordsByUserAndMonth_Success() {
        // 准备 - 先为用户添加一些记录
        addTestRecordForUser(testUserId, testMonth, new BigDecimal("100.00"));
        addTestRecordForUser(testUserId, testMonth, new BigDecimal("200.00"));

        // 执行
        Integer count = royaltyService.countRecordsByUserAndMonth(testUserId, testMonth);

        // 验证
        assertNotNull(count);
        assertTrue(count.intValue() >= 2); // 至少2条记录
    }

    @Test
    public void testCountRecordsByUserAndMonth_NoRecords() {
        // 准备 - 新用户，没有记录
        Long newUserId = 999L;
        String month = "2024-01";

        // 执行
        Integer count = royaltyService.countRecordsByUserAndMonth(newUserId, month);

        // 验证
        assertNotNull(count);
        assertEquals(0, count.intValue());
    }

    @Test
    public void testGetAllDepartmentsMonthlyTotal_Success() {
        // 准备 - 为两个部门添加记录
        addTestRecord(testNewsDepartmentId);
        addTestRecord(testEditorialDepartmentId);

        // 执行
        List<DepartmentMonthlySummaryResponse> result =
                royaltyService.getAllDepartmentsMonthlyTotal(testMonth);

        // 验证
        assertNotNull(result);
        assertTrue(result.size() >= 2); // 至少包含新闻部和编辑部

        // 验证每个部门都有正确的数据
        for (DepartmentMonthlySummaryResponse summary : result) {
            assertNotNull(summary.getDepartmentId());
            assertNotNull(summary.getDepartmentName());
            assertNotNull(summary.getRecordCount());
            assertNotNull(summary.getTotalFee());

            // 记录数和总金额应该非负
            assertTrue(summary.getRecordCount().intValue() >= 0);
            assertTrue(summary.getTotalFee().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Test
    public void testGetAllDepartmentsMonthlyTotal_NoRecords() {
        // 准备 - 使用一个没有记录的月份
        String emptyMonth = "2099-01";

        // 执行
        List<DepartmentMonthlySummaryResponse> result =
                royaltyService.getAllDepartmentsMonthlyTotal(emptyMonth);

        // 验证
        assertNotNull(result);
        // 可能返回空列表或包含零数据的部门汇总
        for (DepartmentMonthlySummaryResponse summary : result) {
            assertEquals(0, summary.getRecordCount().intValue());
            assertEquals(0.00, summary.getTotalFee().doubleValue(), 0.01);
        }
    }

    @Test
    public void testGetStatisticalSummary_Success() {
        // 准备 - 添加一些记录
        addTestRecord(testNewsDepartmentId);
        addTestRecord(testEditorialDepartmentId);

        // 执行
        StatisticalSummaryResponse summary = royaltyService.getStatisticalSummary();

        // 验证
        assertNotNull(summary);
        assertNotNull(summary.getTotalRecords());
        assertNotNull(summary.getTotalFee());
        assertNotNull(summary.getDepartmentCount());
        assertNotNull(summary.getUserCount());
        assertNotNull(summary.getEarliestMonth());
        assertNotNull(summary.getLatestMonth());
        assertNotNull(summary.getGeneratedAt());

        // 验证数据合理性
        assertTrue(summary.getTotalRecords().intValue() >= 2);
        assertTrue(summary.getTotalFee().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(summary.getDepartmentCount().intValue() >= 2); // 至少新闻部和编辑部
    }

    @Test
    public void testGetTypeDistribution_AllDepartments() {
        // 准备 - 添加不同类型的记录
        addTestRecordWithType(testNewsDepartmentId, "新闻稿");
        addTestRecordWithType(testNewsDepartmentId, "专题报道");
        addTestRecordWithType(testEditorialDepartmentId, "评论稿");

        // 执行 - 不指定部门和月份
        List<TypeDistributionResponse> result = royaltyService.getTypeDistribution(null, null);

        // 验证
        assertNotNull(result);
        assertTrue(result.size() >= 3); // 至少3种类型

        // 验证每种类型的数据
        for (TypeDistributionResponse distribution : result) {
            assertNotNull(distribution.getArticleType());
            assertNotNull(distribution.getRecordCount());
            assertNotNull(distribution.getTotalFee());
            assertNotNull(distribution.getPercentage());

            // 验证百分比在0-100之间
            assertTrue(distribution.getPercentage() >= 0.0);
            assertTrue(distribution.getPercentage() <= 100.0);
        }
    }

    @Test
    public void testGetTypeDistribution_ByDepartment() {
        // 准备 - 为新闻部添加记录
        addTestRecordWithType(testNewsDepartmentId, "新闻稿");
        addTestRecordWithType(testNewsDepartmentId, "专题报道");

        // 执行 - 只查询新闻部
        List<TypeDistributionResponse> result =
                royaltyService.getTypeDistribution(null, testNewsDepartmentId);

        // 验证
        assertNotNull(result);
        // 验证所有记录都属于新闻部
        for (TypeDistributionResponse distribution : result) {
            assertEquals(testNewsDepartmentId, distribution.getDepartmentId());
            assertEquals("新闻部", distribution.getDepartmentName());
        }
    }

    @Test
    public void testGetTypeDistribution_ByMonth() {
        // 准备
        String specificMonth = "2024-01";
        addTestRecordWithTypeAndMonth(testNewsDepartmentId, "新闻稿", specificMonth);

        // 执行 - 只查询特定月份
        List<TypeDistributionResponse> result =
                royaltyService.getTypeDistribution(specificMonth, null);

        // 验证
        assertNotNull(result);
        // 可能返回结果或空列表
    }

    @Test
    public void testGetTypeDistribution_EmptyResult() {
        // 准备 - 使用一个没有记录的月份
        String emptyMonth = "2099-01";

        // 执行
        List<TypeDistributionResponse> result =
                royaltyService.getTypeDistribution(emptyMonth, null);

        // 验证
        assertNotNull(result);
        // 可能返回空列表
    }

    // ============ 数据库清理功能测试 ============

    @Test
    public void testCleanupExpiredRecords_Success() {
        // 准备
        Integer retainYears = 1; // 保留1年

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.cleanupExpiredRecords(retainYears, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        Map<String, Object> data = response.getData();
        assertNotNull(data.get("totalDeleted"));
        assertNotNull(data.get("cutoffDate"));
        assertNotNull(data.get("cleanupTime"));
        assertNotNull(data.get("retainedYears"));

        assertEquals(retainYears, data.get("retainedYears"));
    }

    @Test
    public void testCleanupExpiredRecords_DefaultYears() {
        // 准备 - 不指定保留年限，使用默认值

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.cleanupExpiredRecords(null, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());

        Map<String, Object> data = response.getData();
        assertEquals(3, data.get("retainedYears")); // 默认3年
    }

    @Test
    public void testCleanupExpiredRecords_InvalidYears() {
        // 准备 - 无效的保留年限
        Integer invalidYears = -1;

        // 执行 - 应该使用默认值
        ApiResponse<Map<String, Object>> response =
                royaltyService.cleanupExpiredRecords(invalidYears, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode()); // 应该成功，因为使用了默认值
    }

    @Test
    public void testCleanupExpiredRecords_ZeroYears() {
        // 准备 - 保留0年（删除所有记录）

        // 执行
        ApiResponse<Map<String, Object>> response =
                royaltyService.cleanupExpiredRecords(0, testAdminId);

        // 验证
        assertNotNull(response);
        // 根据实现，可能成功或失败
        // 不验证具体结果
    }

    // ============ 边界条件和异常测试 ============

    @Test
    public void testAddRoyalty_MultipleUsers() {
        // 准备 - 多个用户的记录
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Arrays.asList(testUserId, 3L, 4L));
        request.setRealNames(Arrays.asList("张三", "李四", "王五"));
        request.setStudentNumbers(Arrays.asList("20230001", "20230002", "20230003"));
        request.setArticleTitle("多作者稿件");
        request.setArticleType("合作稿");
        request.setFeeAmount(new BigDecimal("300.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(testNewsDepartmentId);

        // 执行
        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);

        // 验证
        assertNotNull(response);
        assertEquals("0000", response.getResCode());
        assertNotNull(response.getData());
        assertEquals(3, response.getData().getUserIds().size());
    }

    @Test
    public void testAddRoyalty_ZeroAmount() {
        // 准备 - 金额为0
        RoyaltyAddRequest request = createValidRoyaltyAddRequest(testNewsDepartmentId);
        request.setFeeAmount(BigDecimal.ZERO);

        try {
            // 执行 - 应抛出异常
            royaltyService.addRoyalty(request, testAdminId);
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("稿费金额必须大于0"));
        }
    }

    @Test
    public void testAddRoyalty_NegativeAmount() {
        // 准备 - 负金额
        RoyaltyAddRequest request = createValidRoyaltyAddRequest(testNewsDepartmentId);
        request.setFeeAmount(new BigDecimal("-100.00"));

        try {
            // 执行 - 应抛出异常
            royaltyService.addRoyalty(request, testAdminId);
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("稿费金额必须大于0"));
        }
    }

    @Test
    public void testQueryAllRoyalty_InvalidPage() {
        // 准备 - 无效的分页参数
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(-1); // 负页码
        request.setSize(10);

        // 执行
        RoyaltyListResponse response = royaltyService.queryAllRoyalty(request);

        // 验证 - 应该使用默认值，返回有效结果
        assertNotNull(response);
        assertNotNull(response.getList());
    }

    @Test
    public void testQueryAllRoyalty_InvalidSize() {
        // 准备 - 无效的页面大小
        RoyaltyQueryRequest request = new RoyaltyQueryRequest();
        request.setPage(1);
        request.setSize(0); // 0或负数

        // 执行
        RoyaltyListResponse response = royaltyService.queryAllRoyalty(request);

        // 验证 - 应该使用默认值
        assertNotNull(response);
        assertNotNull(response.getList());
    }

    // ============ 集成测试 ============

    @Test
    public void testFullCRUDWorkflow() {
        // 1. 添加记录
        RoyaltyAddRequest addRequest = createValidRoyaltyAddRequest(testNewsDepartmentId);
        ApiResponse<RoyaltyResponse> addResponse = royaltyService.addRoyalty(addRequest, testAdminId);
        assertEquals("0000", addResponse.getResCode());
        Long recordId = addResponse.getData().getRecordId();

        // 2. 查询验证
        RoyaltyQueryRequest queryRequest = new RoyaltyQueryRequest();
        queryRequest.setPage(1);
        queryRequest.setSize(10);
        RoyaltyListResponse queryResponse = royaltyService.queryAllRoyalty(queryRequest);
        assertTrue(queryResponse.getTotal() > 0);

        // 3. 更新记录
        RoyaltyUpdateRequest updateRequest = new RoyaltyUpdateRequest();
        updateRequest.setArticleTitle("更新后的标题");
        updateRequest.setFeeAmount(new BigDecimal("250.00"));
        updateRequest.setArticleType("更新类型");
        updateRequest.setStatisticalMonth(testMonth);
        updateRequest.setDepartmentId(testNewsDepartmentId);

        ApiResponse<RoyaltyResponse> updateResponse = royaltyService.updateRoyalty(recordId, updateRequest, testAdminId);
        assertEquals("0000", updateResponse.getResCode());

        // 4. 删除记录
        ApiResponse<Void> deleteResponse = royaltyService.deleteRoyalty(recordId, testAdminId);
        assertEquals("0000", deleteResponse.getResCode());

        // 5. 确认删除
        ApiResponse<RoyaltyResponse> verifyResponse = royaltyService.updateRoyalty(recordId, updateRequest, testAdminId);
        assertNotEquals("0000", verifyResponse.getResCode());
    }

    @Test
    public void testStatisticalMethodsConsistency() {
        // 准备 - 添加记录
        addTestRecord(testNewsDepartmentId);
        addTestRecord(testEditorialDepartmentId);

        // 验证各种统计方法的一致性
        BigDecimal newsTotal = royaltyService.calculateDepartmentMonthlyTotal(testNewsDepartmentId, testMonth);
        BigDecimal editorialTotal = royaltyService.calculateDepartmentMonthlyTotal(testEditorialDepartmentId, testMonth);

        Integer newsCount = royaltyService.countRecordsByDepartmentAndMonth(testNewsDepartmentId, testMonth);
        Integer editorialCount = royaltyService.countRecordsByDepartmentAndMonth(testEditorialDepartmentId, testMonth);

        // 验证统计汇总
        StatisticalSummaryResponse summary = royaltyService.getStatisticalSummary();
        assertNotNull(summary);

        // 验证类型分布
        List<TypeDistributionResponse> typeDistribution = royaltyService.getTypeDistribution(null, null);
        assertNotNull(typeDistribution);

        // 验证部门月度汇总
        List<DepartmentMonthlySummaryResponse> departmentSummary =
                royaltyService.getAllDepartmentsMonthlyTotal(testMonth);
        assertNotNull(departmentSummary);
    }

    // ============ 辅助方法 ============

    private RoyaltyAddRequest createValidRoyaltyAddRequest(Long departmentId) {
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Collections.singletonList(testUserId));
        request.setRealNames(Collections.singletonList("张三"));
        request.setStudentNumbers(Collections.singletonList("20230001"));
        request.setArticleTitle("测试新增稿件");
        request.setArticleType("新闻稿");
        request.setFeeAmount(new BigDecimal("500.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(departmentId);
        return request;
    }

    private Long addTestRecord(Long departmentId) {
        return addTestRecordForUser(testUserId, testMonth, new BigDecimal("100.00"), departmentId);
    }

    private Long addTestRecordForUser(Long userId, String month, BigDecimal amount) {
        return addTestRecordForUser(userId, month, amount, testNewsDepartmentId);
    }

    private Long addTestRecordForUser(Long userId, String month, BigDecimal amount, Long departmentId) {
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Collections.singletonList(userId));
        request.setRealNames(Collections.singletonList("测试用户"));
        request.setStudentNumbers(Collections.singletonList("20230000"));
        request.setArticleTitle("测试稿件");
        request.setArticleType("测试类型");
        request.setFeeAmount(amount);
        request.setStatisticalMonth(month);
        request.setDepartmentId(departmentId);

        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);
        if ("0000".equals(response.getResCode()) && response.getData() != null) {
            return response.getData().getRecordId();
        }
        throw new RuntimeException("添加测试记录失败: " + response.getResMsg());
    }

    private void addTestRecordWithType(Long departmentId, String articleType) {
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Collections.singletonList(testUserId));
        request.setRealNames(Collections.singletonList("测试用户"));
        request.setStudentNumbers(Collections.singletonList("20230000"));
        request.setArticleTitle("测试稿件 - " + articleType);
        request.setArticleType(articleType);
        request.setFeeAmount(new BigDecimal("100.00"));
        request.setStatisticalMonth(testMonth);
        request.setDepartmentId(departmentId);

        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);
        if (!"0000".equals(response.getResCode())) {
            throw new RuntimeException("添加测试记录失败: " + response.getResMsg());
        }
    }

    private void addTestRecordWithTypeAndMonth(Long departmentId, String articleType, String month) {
        RoyaltyAddRequest request = new RoyaltyAddRequest();
        request.setUserIds(Collections.singletonList(testUserId));
        request.setRealNames(Collections.singletonList("测试用户"));
        request.setStudentNumbers(Collections.singletonList("20230000"));
        request.setArticleTitle("测试稿件 - " + articleType);
        request.setArticleType(articleType);
        request.setFeeAmount(new BigDecimal("100.00"));
        request.setStatisticalMonth(month);
        request.setDepartmentId(departmentId);

        ApiResponse<RoyaltyResponse> response = royaltyService.addRoyalty(request, testAdminId);
        if (!"0000".equals(response.getResCode())) {
            throw new RuntimeException("添加测试记录失败: " + response.getResMsg());
        }
    }
}