<template>
  <PageBackground2>
    <div class="title-line"></div>
    <template #page-title>我的稿费与反馈</template>
    <template #card-content>
      <!-- 新增标签页容器 -->
      <div class="tab-container">
        <!-- 标签页头部 -->
        <div class="tab-header">
          <div
              class="tab-item"
              :class="{ active: activeTab === 'fee' }"
              @click="activeTab = 'fee'"
          >
            我的稿费明细
          </div>
          <div
              class="tab-item"
              :class="{ active: activeTab === 'feedback' }"
              @click="activeTab = 'feedback'"
          >
            我的反馈记录
          </div>
        </div>

        <!-- 标签页1：原有稿费明细内容 -->
        <div class="tab-content" v-show="activeTab === 'fee'">
          <!-- 查询区域：保留原有代码 -->
          <div class="query-area">
            <label>开始日期:</label>
            <input
                type="date"
                v-model="queryParams.startDate"
                class="date-input"
                min="2026-01-01"
            >
            <label class="ml-20">结束日期:</label>
            <input
                type="date"
                v-model="queryParams.endDate"
                class="date-input"
                :min="queryParams.startDate || '2026-01-01'"
            >
            <label class="ml-20">任务名称:</label>
            <input
                type="text"
                v-model="queryParams.taskName"
                placeholder="输入任务名称查询"
                class="task-input"
            >
            <button class="query-btn" @click="fetchRoyaltyList">查询</button>
            <button class="reset-btn ml-20" @click="resetQuery">重置</button>
          </div>

          <!-- 稿费表格 -->
          <table class="fee-table">
            <thead>
            <tr>
              <th>日期</th>
              <th>任务名称</th>
              <th>部门</th>
              <th>金额</th>
              <!--              <th>状态</th>-->
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="feeList.length === 0">
              <td colspan="6" class="empty-tip">暂无稿费数据</td>
            </tr>
            <tr v-for="(item, index) in feeList" :key="item.feedback_id || index">
              <td>{{ item.statistical_month || '-' }}</td>
              <td>{{ item.article_title || '-' }}</td>
              <td>{{ formatDepartment(item.department_id) }}</td>
              <td>{{ formatMoney(item.fee_amount) }}元</td>
              <!--              <td>已结算</td>-->
              <td>
                <button class="feedback-btn" @click="handleFeedback(item)">反馈</button>
              </td>
            </tr>
            </tbody>

            <!-- 总计区域：保留原有代码 -->
            <template class="total-area">
              共计: <span class="total-amount">{{ formatMoney(totalAmount) }}元</span>
            </template>
          </table>

          <!-- 分页控件：保留原有代码 -->
          <div class="pagination-area">
            <button
                class="page-btn"
                @click="changePage(queryParams.page - 1)"
                :disabled="queryParams.page <= 1"
            >上一页</button>
            <span class="page-info">
              第{{ queryParams.page }}页 / 共{{ totalPage }}页
            </span>
            <button
                class="page-btn"
                @click="changePage(queryParams.page + 1)"
                :disabled="queryParams.page >= totalPage"
            >下一页</button>
          </div>
        </div>

        <!-- 标签页2：新增我的反馈记录 -->
        <div class="tab-content" v-show="activeTab === 'feedback'">
          <!-- 反馈列表表格 -->
          <table class="feedback-table">
            <thead>
            <tr>
              <th>稿费名称</th>
              <th>反馈内容</th>
              <th>提交时间</th>
              <th>反馈状态</th>
              <th>管理员回复</th>
              <!--              <th>回复时间</th>-->
            </tr>
            </thead>
            <tbody>
            <tr v-if="!feedbackList || feedbackList.length === 0">
              <td colspan="6" class="empty-tip">暂无反馈记录</td>
            </tr>
            <tr v-for="(item, index) in feedbackList" :key="item.feedback_id || index">
              <td>{{ item.feedback_id || '-' }}</td>
              <td class="content-cell">{{ item.content || '-' }}</td>
              <td>{{ formatDate(item.created_at) || '-' }}</td>
              <td>
                <span class="status-tag" :class="getStatusClass(item.status)">
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td class="reply-cell">{{ item.reply_content || '暂无回复' }}</td>
              <!--              <td>{{item.replied_at || '-' }}</td>-->
            </tr>
            </tbody>
          </table>

          <!-- 新增反馈列表分页控件 -->
          <div class="pagination-area" v-if="feedbackTotalCount > 0">
            <button
                class="page-btn"
                @click="changeFeedbackPage(feedbackQueryParams.page - 1)"
                :disabled="feedbackQueryParams.page <= 1"
            >上一页</button>
            <span class="page-info">
              第{{ feedbackQueryParams.page }}页 / 共{{ feedbackTotalPage }}页
            </span>
            <button
                class="page-btn"
                @click="changeFeedbackPage(feedbackQueryParams.page + 1)"
                :disabled="feedbackQueryParams.page >= feedbackTotalPage"
            >下一页</button>
          </div>
        </div>
      </div>

      <!-- 反馈弹窗：移出循环，放到全局 -->
      <el-dialog
          v-model="feedbackDialogVisible"
          title="提交稿费反馈"
          width="400px"
          :modal="false"
          class="no-shadow-dialog"
      >
        <el-form :model="feedbackForm" label-width="80px">

          <el-form-item label="反馈理由" required>
            <el-input
                v-model="feedbackForm.content"
                type="textarea"
                :rows="4"
                placeholder="请输入您的反馈问题（如稿费金额错误、漏算等）"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedbackForm">提交</el-button>
        </template>
      </el-dialog>
    </template>
  </PageBackground2>
</template>
<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { useRouter } from 'vue-router';
import { ref, reactive, computed, onMounted } from 'vue';
// 导入原有稿费接口
import { getPersonalRoyalty } from '@/api/royalty';
// 严格按你提供的feedback.js导入接口
import { submitFeedback, getUserFeedbackList } from '@/api/feedback.js';

// 导入原有工具方法
import {formatDateTime, formatDepartment, formatMoney} from "@/utils/format.js";
// 导入Store
import {useNoticeStore, useUserStore} from "@/stores/index.js";
// 导入Element Plus组件
import {ElMessage} from "element-plus";

const router = useRouter();
// 初始化Store
const userStore = useUserStore();
const noticeStore = useNoticeStore();

// ===================== 新增变量（解决报错核心） =====================
// 1. 标签页激活状态（默认选中稿费明细）
const activeTab = ref('fee');
// 2. 反馈列表数据（初始化为空数组，避免length读取报错）
const feedbackList = ref([]);

// ===================== 新增反馈分页相关变量 =====================
// 反馈列表查询参数
const feedbackQueryParams = reactive({
  page: 1, // 当前页码
  size: 10 // 每页条数
});
// 反馈列表总条数
const feedbackTotalCount = ref(0);
// 反馈列表总页数（计算属性）
const feedbackTotalPage = computed(() => {
  return Math.ceil(feedbackTotalCount.value / feedbackQueryParams.size);
});

// ===================== 原有变量（保留） =====================
// 反馈弹窗+表单状态
const feedbackDialogVisible = ref(false); // 弹窗显隐
const currentFeedbackItem = ref({}); // 存储当前点击的稿费项
const feedbackForm = ref({
  content: '', // 反馈内容
  feedback_id: '' // 关联的稿费记录ID
});

// 查询参数：匹配接口+扩展任务名称（前端过滤）
const queryParams = reactive({
  startDate: '2026-01-01', // 默认开始日期：2026.1.1
  endDate: '', // 默认结束日期：当天（在onMounted中初始化）
  taskName: '', // 任务名称（前端过滤）
  page: 1, // 页码
  size: 10 // 每页数量
});

// 响应数据
const feeList = ref([]); // 稿费列表
const totalCount = ref(0); // 总条数（接口返回）
const totalAmount = ref(0); // 总金额（前端累加）

// 计算总页数
const totalPage = computed(() => {
  return Math.ceil(totalCount.value / queryParams.size);
});

// ===================== 原有方法（保留） =====================
// 辅助函数：获取当天的YYYY-MM-DD格式日期
const getTodayDate = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};
// 日期格式化方法
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return formatDateTime(dateStr).split(' ')[0];
};

// 获取稿费列表（核心方法）
const fetchRoyaltyList = async () => {
  // 日期校验
  if (queryParams.startDate && queryParams.endDate) {
    if (new Date(queryParams.endDate) < new Date(queryParams.startDate)) {
      ElMessage.error('结束日期不能早于开始日期，请重新选择');
      return;
    }
  }
  try {
    const requestParams = {
      startDate: queryParams.startDate,
      endDate: queryParams.endDate,
      page: queryParams.page,
      size: queryParams.size
    };
    const res = await getPersonalRoyalty(requestParams);
    if (res.res_code === '0000') {
      const { total, list } = res.data;
      totalCount.value = total;

      // 前端过滤任务名称
      let finalList = list;
      if (queryParams.taskName) {
        finalList = list.filter(item => item.article_title?.toLowerCase().includes(queryParams.taskName.toLowerCase()));
      }

      feeList.value = finalList;

      // 只累加当前用户的稿费金额
      totalAmount.value = finalList.reduce((sum, item) => sum + (Number(item.fee_amount) || 0), 0);
    } else {
      ElMessage.error(`查询失败：${res.res_msg}`);
      feeList.value = [];
      totalCount.value = 0;
      totalAmount.value = 0;
    }
  } catch (error) {
    console.error(error);
    feeList.value = [];
    totalCount.value = 0;
    totalAmount.value = 0;
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.startDate = '2026-01-01'; // 重置为默认开始日期
  // 兜底：结束日期不能早于开始日期
  const today = getTodayDate();
  queryParams.endDate = today < '2026-01-01' ? '2026-01-01' : today;
  queryParams.taskName = '';
  queryParams.page = 1;
  fetchRoyaltyList();
};


// 分页切换
const changePage = (page) => {
  if (page < 1 || page > totalPage.value) return;
  queryParams.page = page;
  fetchRoyaltyList();
};

// 反馈操作：打开弹窗，初始化表单
const handleFeedback = (item) => {

  currentFeedbackItem.value = item;
  feedbackForm.value = {
    content: '',
    feedback_id: item.feedback_id // 关联稿费记录ID
  };
  feedbackDialogVisible.value = true;
};

// ===================== 修正：提交反馈表单（对齐你的接口） =====================
const submitFeedbackForm = async () => {
  if (!feedbackForm.value.content.trim()) {
    ElMessage.error('请填写反馈理由');
    return;
  }
  if (!userStore.userInfo?.user_id) {
    ElMessage.error('请先登录');
    feedbackDialogVisible.value = false;
    return;
  }

  // 严格对齐你接口文档2.5.20的参数（user_id、content为必填）
  const params = {
    user_id: userStore.userInfo.user_id,
    content: feedbackForm.value.content,
    feedback_id: feedbackForm.value.feedback_id // 自定义字段，用于关联稿费
  };

  try {
    const res = await submitFeedback(params); // 调用你提供的submitFeedback接口
    if (res.res_code === '0000') {
      ElMessage.success('反馈提交成功！');
      feedbackDialogVisible.value = false;
      feedbackForm.value.content = '';
      fetchMyFeedback(); // 提交后刷新反馈列表
    } else {
      ElMessage.error(`提交失败：${res.data?.res_msg || '未知错误'}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，提交失败');
    console.error('反馈提交失败:', err);
  }
};
const getStatusText = (status) => {
  const statusMap = {
    pending: '待处理',
    replied: '已回复',
    /*resolved: '已解决',
    rejected: '已驳回'*/
  };
  return statusMap[status] || '未知状态';
};

// 2. 反馈状态转样式类（模板里用到的getStatusClass）
const getStatusClass = (status) => {
  const classMap = {
    pending: 'status-pending',
    replied: 'status-replied',
    resolved: 'status-resolved',
    rejected: 'status-rejected'
  };
  return classMap[status] || '';
};
// ===================== 修正：获取我的反馈列表（对齐你的接口2.5.21） =====================
const fetchMyFeedback = async () => {
  if (!userStore.userInfo?.user_id) return;
  try {
    // 调用你提供的getUserFeedbackList接口，传分页参数（接口2.5.21要求）
    const res = await getUserFeedbackList({
      page: feedbackQueryParams.page, // 使用反馈分页参数
      size: feedbackQueryParams.size // 使用反馈分页参数
    });
    if (res.res_code === '0000') {
      // 接口返回结构：list为反馈列表，total为总条数（按你文档2.5.21）
      feedbackList.value = res.data.list || [];
      feedbackTotalCount.value = res.data.total || 0;
    } else {
      ElMessage.error(`获取反馈列表失败：${res.res_msg}`);
      feedbackList.value = [];
      feedbackTotalCount.value = 0;
    }
  } catch (err) {
    ElMessage.error('网络异常，获取反馈列表失败');
    console.error('获取反馈列表失败:', err);
    feedbackList.value = [];
    feedbackTotalCount.value = 0;
  }
};

// ===================== 新增：反馈列表分页切换方法 =====================
const changeFeedbackPage = (page) => {
  // 边界校验：页码不能小于1，不能大于总页数
  if (page < 1 || page > feedbackTotalPage.value) return;
  feedbackQueryParams.page = page;
  // 重新请求反馈列表
  fetchMyFeedback();
};

// ===================== 生命周期（保留） =====================
onMounted(() => {
  // 初始化结束日期为当天，且不早于开始日期
  const today = getTodayDate();
  queryParams.endDate = today < queryParams.startDate ? queryParams.startDate : today;
  fetchRoyaltyList(); // 加载稿费列表
  fetchMyFeedback();  // 加载反馈列表
});
</script>
<style scoped>
/* 原有样式保留 */
.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4;
  margin-right: 12px;
}
.query-area {
  margin: 20px 0;
  display: flex;
  align-items: center;
}
.month-select, .task-input, .date-input {
  padding: 4px 8px;
  margin: 0 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.query-btn {
  padding: 4px 16px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
}
.reset-btn {
  padding: 4px 12px;
  background: #f5f5f5;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}
.ml-20 {
  margin-left: 20px;
}
.fee-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  /* ========== 新增核心优化 ========== */
  table-layout: fixed; /* 锁定表格布局规则，只根据表头计算列宽，避免内容撑开列宽 */
  table-layout: fixed; /* 重复声明兼容部分浏览器 */
  min-width: 100%; /* 防止表格宽度收缩 */
  border: 1px solid #ddd; /* 给表格加外层边框，避免单元格边框拼接的视觉波动 */
}

.fee-table th, .fee-table td {
  border: 1px solid #ddd;
  padding: 12px 8px;
  /* ========== 新增核心优化 ========== */
  box-sizing: border-box; /* 确保边框和内边距不影响单元格宽度计算 */
  white-space: nowrap; /* 禁止文字换行，避免单元格高度波动 */
  overflow: hidden; /* 隐藏超出内容 */
  text-overflow: ellipsis; /* 超出部分显示省略号，保持单元格尺寸稳定 */
}

.fee-table th {
  background-color: #eee;
  font-weight: 500;
  /* ========== 新增优化 ========== */
  position: relative; /* 增强表头渲染稳定性 */
}

.feedback-btn {
  border: none;
  background: transparent;
  color: #9b8eb4;
  cursor: pointer;
  padding: 0;
}
.empty-tip {
  text-align: center;
  padding: 20px;
  color: #999;
}
.total-area {
  margin-top: 20px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
}
.total-amount {
  color: #333;
}
.pagination-area {
  margin-top: 20px;
  text-align: right;
}
.page-btn {
  padding: 4px 12px;
  margin: 0 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}
.page-btn:disabled {
  cursor: not-allowed;
  background: #f5f5f5;
  color: #999;
}
.page-info {
  margin: 0 8px;
}
:deep(.no-shadow-dialog){
  box-shadow: none !important;
  border: 1px #1a1a1a solid;
}

/* ===================== 新增标签页样式 ===================== */
.tab-container {
  width: 100%;
}
.tab-header {
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 20px;
}
.tab-item {
  padding: 10px 20px;
  cursor: pointer;
  position: relative;
  color: #666;
}
.tab-item.active {
  color: #9b8eb4;
  font-weight: 500;
}
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #9b8eb4;
}
.tab-content {
  width: 100%;
}

/* ===================== 新增反馈列表表格样式 ===================== */
.feedback-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}
.feedback-table th, .feedback-table td {
  border: 1px solid #ddd;
  padding: 12px 8px;
}
.feedback-table th {
  background-color: #eee;
  font-weight: 500;
}
.content-cell, .reply-cell {
  max-width: 200px;
  word-break: break-all;
  text-align: left;
}
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-pending {
  background-color: #fff7e6;
  color: #ff9a3c;
}
.status-replied {
  background-color: #e6f7ff;
  color: #40a9ff;
}
.status-resolved {
  background-color: #f0f9ff;
  color: #52c41a;
}
.status-rejected {
  background-color: #fff1f0;
  color: #f5222d;
}
</style>