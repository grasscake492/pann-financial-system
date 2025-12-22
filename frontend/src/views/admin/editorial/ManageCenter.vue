<template>
  <PageBackground2>
    <template #card-content>
      <!-- 标签页按钮 -->
      <div class="tab-buttons">
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'query' }"
            @click="handleTabChange('query')"
        >
          任务查询
        </button>
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'feedback' }"
            @click="handleTabChange('feedback')"
        >
          反馈处理
        </button>
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'manage' }"
            @click="handleTabChange('manage')"
        >
          任务管理
        </button>
      </div>

      <!-- 任务查询区域 -->
      <div v-if="currentTab === 'query'">
        <div class="query-form">
          <label>选择月份:</label>
          <select class="form-select" v-model="queryForm.month">
            <option value="">请选择</option>
            <option value="01">1月</option>
            <option value="02">2月</option>
            <option value="03">3月</option>
            <option value="04">4月</option>
            <option value="05">5月</option>
            <option value="06">6月</option>
            <option value="07">7月</option>
            <option value="08">8月</option>
            <option value="09">9月</option>
            <option value="10">10月</option>
            <option value="11">11月</option>
            <option value="12">12月</option>
          </select>
          <label>任务名称:</label>
          <input
              type="text"
              placeholder="输入任务名称查询"
              class="form-input"
              v-model="queryForm.article_title"
          >
          <button class="query-btn" @click="fetchTaskList">查询</button>
        </div>

        <table class="task-table">
          <thead>
          <tr>
            <th>日期</th>
            <th>日历图张数</th>
            <th>参与人以及金额详情</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="taskList.length === 0">
            <td colspan="4" class="empty-tip">暂无任务记录</td>
          </tr>
          <tr v-for="item in taskList" :key="item.record_id">
            <td>{{ item.created_at }}</td>
            <td>{{ item.image_count || 0 }}</td>
            <td>
              {{ item.real_names?.join(', ') || '-' }}
              ({{ item.fee_amount || 0 }}元)
            </td>
            <td>
              <button class="op-btn" @click="openTaskEditDialog(item)">修改</button>
              <button class="op-btn danger" @click="confirmDeleteTask(item.record_id)">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 反馈处理区域 -->
      <div v-if="currentTab === 'feedback'">
        <!-- 反馈筛选栏 -->
        <div class="feedback-filter">
          <label>反馈状态:</label>
          <select class="form-select" v-model="feedbackQueryParams.status">
            <option value="">全部</option>
            <option value="pending">待处理</option>
            <option value="replied">已回复</option>
          </select>
          <label>月份:</label>
          <select class="form-select" v-model="filterMonth">
            <option value="">全部</option>
            <option value="1">1月</option>
            <option value="2">2月</option>
            <option value="3">3月</option>
            <option value="4">4月</option>
            <option value="5">5月</option>
            <option value="6">6月</option>
            <option value="7">7月</option>
            <option value="8">8月</option>
            <option value="9">9月</option>
            <option value="10">10月</option>
            <option value="11">11月</option>
            <option value="12">12月</option>
          </select>
          <label>用户姓名:</label>
          <input
              type="text"
              placeholder="输入姓名模糊查询"
              class="form-input"
              v-model="filterName"
          >
          <button class="query-btn" @click="handleFrontendFilter">查询</button>
        </div>

        <!-- 反馈条目列表 -->
        <div class="feedback-item-list">
          <div v-if="filteredFeedbackList.length === 0" class="empty-tip">暂无反馈数据</div>
          <div
              class="feedback-item"
              v-for="item in filteredFeedbackList"
              :key="item.feedback_id"
          >
            <div class="feedback-header">
              <span class="feedback-id">反馈ID：{{ item.feedback_id }}</span>
              <span class="status-tag" :class="FEEDBACK_STATUS_CLASS[item.status]">
                {{ FEEDBACK_STATUS_LABEL[item.status] }}
              </span>
            </div>
            <div class="feedback-content">
              <p><span class="label">反馈用户：</span>{{ item.real_name }}</p>
              <p><span class="label">反馈内容：</span>{{ item.content }}</p>
              <p><span class="label">反馈时间：</span>{{ item.created_at }}</p>
              <p><span class="label">回复时间：</span>{{ item.replied_at || '未回复' }}</p>
              <p><span class="label">回复内容：</span>{{ item.reply_content || '暂无回复' }}</p>
            </div>
            <div class="feedback-actions">
              <button class="op-btn reply-btn" @click="openReplyDialog(item.feedback_id)">回复</button>
              <button class="op-btn status-btn" @click="openStatusDialog(item.feedback_id)">改状态</button>
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination" v-if="filteredFeedbackList.length > 0">
          <button
              class="page-btn"
              @click="handlePageChange(currentPage - 1)"
              :disabled="currentPage <= 1"
          >
            上一页
          </button>
          <span>第{{ currentPage }}页 / 共{{ totalItemPages }}页</span>
          <button
              class="page-btn"
              @click="handlePageChange(currentPage + 1)"
              :disabled="currentPage >= totalItemPages"
          >
            下一页
          </button>
        </div>

        <!-- 回复弹窗 -->
        <div class="dialog-mask" v-if="replyDialogVisible">
          <div class="dialog-content">
            <h4>回复反馈（ID: {{ currentFeedbackId }}）</h4>
            <textarea
                placeholder="请输入回复内容"
                class="reply-textarea"
                v-model="replyForm.reply_content"
            ></textarea>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="closeReplyDialog">取消</button>
              <button class="confirm-btn" @click="submitReply">提交回复</button>
            </div>
          </div>
        </div>

        <!-- 改状态弹窗 -->
        <div class="dialog-mask" v-if="statusDialogVisible">
          <div class="dialog-content">
            <h4>更新反馈状态（ID: {{ currentFeedbackId }}）</h4>
            <select class="form-select" v-model="statusForm.status">
              <option value="pending">待处理</option>
              <option value="replied">已回复</option>
            </select>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="closeStatusDialog">取消</button>
              <button class="confirm-btn" @click="submitStatusUpdate">确认更新</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 任务管理区域 -->
      <div v-if="currentTab === 'manage'">
        <div class="manage-title">
          <div class="title-line"></div>
          <h3>新建任务</h3>
        </div>
        <div class="manage-form">
          <div class="form-item">
            <label>任务名称:</label>
            <input
                type="text"
                placeholder="输入"
                class="form-input"
                v-model="taskEditForm.article_title"
            >
          </div>
          <div class="form-item">
            <label>任务日期:</label>
            <input
                type="date"
                placeholder="弹出日期选择框"
                class="form-input"
                v-model="taskEditForm.created_at"
            >
          </div>
          <!-- 新增：部门选择单选框 -->
          <div class="form-item">
            <label>所属部门:</label>
            <div class="radio-group">
              <label class="radio-item">
                <input
                    type="radio"
                    name="department"
                    v-model="taskEditForm.departmentId"
                    value="1"
                >
                新闻部
              </label>
              <label class="radio-item">
                <input
                    type="radio"
                    name="department"
                    v-model="taskEditForm.departmentId"
                    value="2"
                >
                编辑部
              </label>
              <label class="radio-item">
                <input
                    type="radio"
                    name="department"
                    v-model="taskEditForm.departmentId"
                    value="3"
                >
                运营部
              </label>
            </div>
          </div>

          <!-- 原有表单项：稿件类型、执行人选择等 -->
          <div class="form-item">
            <label>稿件类型:</label>
            <select class="form-select" v-model="taskEditForm.articleType">
              <option value="">请选择稿件类型</option>
              <option value="日历图">日历图</option>
              <option value="文案">文案</option>
              <option value="设计">设计</option>
            </select>
          </div>
          <!-- 多执行人选择区域 -->
          <div class="form-item">
            <label>执行人</label>
            <select class="form-select" v-model="taskEditForm.selectUserId" @change="onUserSelect">
              <option value="">请选择执行人</option>
              <option v-for="user in userList" :key="user.user_id" :value="user.user_id">
                {{ user.real_name }}
              </option>
            </select>
            <!-- 自动显示选中执行人的学号，无需手动输入 -->
            <label>学号:</label>
            <input
                type="text"
                class="form-input small-input"
                v-model="taskEditForm.inputStudentNumber"
                readonly
            placeholder="选择执行人后自动填充"
            >
            <label>金额:</label>
            <input
                type="number"
                placeholder="输入"
                class="form-input small-input"
                v-model="taskEditForm.inputAmount"
                min="0"
                step="0.01"
            >
            <button class="add-btn" @click="addExecutor">添加执行人</button>
          </div>

          <!-- 已选执行人列表（优化多执行人展示） -->
          <div class="executor-list" v-if="taskEditForm.executors.length > 0">
            <div class="executor-header">
              <label>已选执行人 (共{{ taskEditForm.executors.length }}人)</label>
              <button class="clear-btn" @click="clearAllExecutors">清空</button>
            </div>
            <div class="executor-tags">
      <span v-for="(item, index) in taskEditForm.executors" :key="index" class="executor-tag">
        {{ item.real_name }}
        <span class="amount-text">({{ item.amount.toFixed(2) }}元)</span>
        <button class="tag-close" @click="removeExecutor(index)">×</button>
      </span>
            </div>
            <!-- 总金额展示 -->
            <div class="total-amount">
              执行人总金额: <span class="total-value">{{ totalAmount.toFixed(2) }}元</span>
            </div>
          </div>

          <button class="submit-btn" @click="submitTask">提交任务</button>
        </div>
      </div>
    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 引入API
import { getDepartmentRoyalty, addRoyaltyRecord, deleteRoyaltyRecord } from "@/api/royalty.js";
import { getAllFeedback, replyFeedback, updateFeedbackStatus } from "@/api/feedback.js";
import { getUserList } from "@/api/user.js";
import { FEEDBACK_STATUS_CLASS, FEEDBACK_STATUS_LABEL } from "@/utils/feedbackStatus.js";

//=================== 基础变量 ===================
const currentTab = ref('query');
const deptIdMap = {
  1: '新闻部',
  2: '编辑部',
  3: '运营部',
  'news': 1,
  'edit': 2,
  'operation': 3,
};

//=================== 标签切换逻辑 ===================
const handleTabChange = async (tab) => {
  currentTab.value = tab;
  switch (tab) {
    case 'query':
      await fetchTaskList();
      break;
    case 'feedback':
      await fetchAllFeedback();
      break;
    case 'manage':
      await loadUserList();
      break;
  }
};

//=================== 任务查询模块 ===================
const queryForm = reactive({
  month: '',
  article_title: ''
});

const taskList = ref([]);

// 获取任务列表
const fetchTaskList = async () => {
  try {
    const currentYear = new Date().getFullYear();
    const requestParams = {
      statistical_month: queryForm.month ? `${currentYear}-${queryForm.month.padStart(2, '0')}` : '',
      article_title: queryForm.article_title
    };
    const res = await getDepartmentRoyalty(requestParams);
    if (res.res_code === '0000') {
      taskList.value = res.data?.list || [];
    } else {
      ElMessage.error(`获取任务列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，获取任务列表失败');
  }
};

// 打开任务编辑弹窗
const openTaskEditDialog = (item = {}) => {
  taskEditForm.record_id = item.record_id || '';
  taskEditForm.article_title = item.article_title || '';
  taskEditForm.created_at = item.created_at || '';
  taskEditForm.executors = item.real_names && item.fee_amount
      ? item.real_names.map((name, index) => ({
        real_name: name,
        amount: (item.fee_amount / item.real_names.length) || 0
      }))
      : [];
  currentTab.value = 'manage';
};

// 确认删除任务
const confirmDeleteTask = async (recordId) => {
  try {
    await ElMessageBox.confirm(
        '确定要删除该任务吗？',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    );
    const res = await deleteRoyaltyRecord(recordId);
    if (res.res_code === '0000') {
      ElMessage.success('删除成功');
      await fetchTaskList();
    } else {
      ElMessage.error(`删除失败：${res.res_msg}`);
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('网络异常，删除失败');
    }
  }
};

//=================== 反馈处理模块 ===================
const feedbackList = ref([]);
const currentPage = ref(1);
const pageSize = ref(2);
const filterMonth = ref('');
const filterName = ref('');

const feedbackQueryParams = reactive({
  page: 1,
  size: 100,
  status: ''
});

// 弹窗相关
const replyDialogVisible = ref(false);
const currentFeedbackId = ref('');
const replyForm = reactive({ reply_content: '' });
const statusDialogVisible = ref(false);
const statusForm = reactive({ status: 'pending' });

// 筛选后的反馈列表
const filteredFeedbackList = computed(() => {
  let list = [...feedbackList.value];

  // 月份筛选
  if (filterMonth.value) {
    list = list.filter(item => {
      const createMonth = item.created_at?.split('-')[1];
      return createMonth === filterMonth.value.padStart(2, '0');
    });
  }

  // 姓名筛选
  if (filterName.value) {
    const keyword = filterName.value.trim().toLowerCase();
    list = list.filter(item => item.real_name?.toLowerCase().includes(keyword));
  }

  // 前端分页
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return list.slice(start, end);
});

// 总页数
const totalItemPages = computed(() => {
  let list = [...feedbackList.value];

  if (filterMonth.value) {
    list = list.filter(item => {
      const createMonth = item.created_at?.split('-')[1];
      return createMonth === filterMonth.value.padStart(2, '0');
    });
  }
  if (filterName.value) {
    const keyword = filterName.value.trim().toLowerCase();
    list = list.filter(item => item.real_name?.toLowerCase().includes(keyword));
  }

  return Math.ceil(list.length / pageSize.value) || 1;
});

// 获取反馈列表
const fetchAllFeedback = async () => {
  try {
    const res = await getAllFeedback(feedbackQueryParams);
    if (res.res_code === '0000') {
      feedbackList.value = res.data.list || [];
      currentPage.value = 1;
    } else {
      ElMessage.error(`查询反馈失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，查询反馈失败');
  }
};

// 前端筛选
const handleFrontendFilter = () => {
  currentPage.value = 1;
};

// 分页切换
const handlePageChange = (page) => {
  if (page < 1 || page > totalItemPages.value) return;
  currentPage.value = page;
};

// 回复反馈
const openReplyDialog = (feedbackId) => {
  currentFeedbackId.value = feedbackId;
  replyForm.reply_content = '';
  replyDialogVisible.value = true;
};

const closeReplyDialog = () => {
  replyDialogVisible.value = false;
  currentFeedbackId.value = '';
  replyForm.reply_content = '';
};

const submitReply = async () => {
  if (!replyForm.reply_content.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  try {
    const res = await replyFeedback(currentFeedbackId.value, { reply_content: replyForm.reply_content });
    if (res.res_code === '0000') {
      ElMessage.success('回复成功');
      closeReplyDialog();
      await updateFeedbackStatus(currentFeedbackId.value, { status: 'replied' });
      await fetchAllFeedback();
    } else {
      ElMessage.error(`回复失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，回复失败');
  }
};

// 更新状态
const openStatusDialog = (feedbackId) => {
  currentFeedbackId.value = feedbackId;
  const currentItem = feedbackList.value.find(item => item.feedback_id === feedbackId);
  if (currentItem) statusForm.status = currentItem.status || 'pending';
  statusDialogVisible.value = true;
};

const closeStatusDialog = () => {
  statusDialogVisible.value = false;
  currentFeedbackId.value = '';
  statusForm.status = 'pending';
};

const submitStatusUpdate = async () => {
  try {
    const res = await updateFeedbackStatus(currentFeedbackId.value, { status: statusForm.status });
    if (res.res_code === '0000') {
      ElMessage.success('状态更新成功');
      closeStatusDialog();
      await fetchAllFeedback();
    } else {
      ElMessage.error(`状态更新失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，状态更新失败');
  }
};
//=================== 任务管理模块 ===================

const userList = ref([]);
const taskEditForm = reactive({
  record_id: '',
  article_title: '',
  created_at: '',
  selectUserId: '',
  inputAmount: '',
  inputStudentNumber: '', // 用于存储自动填充的学号
  executors: [] // 存储多个执行人，格式：[{user_id, real_name, amount}, ...]
});

// 响应式计算总金额（多执行人金额总和）
const totalAmount = computed(() => {
  return taskEditForm.executors.reduce((sum, item) => sum + Number(item.amount), 0);
});
// 可选：响应式获取选中的部门名称（用于页面展示）
const selectedDepartmentName = computed(() => {
  // 将字符串类型的departmentId转为数字，传入formatDepartment方法
  return formatDepartment(Number(taskEditForm.departmentId));
});
// 加载用户列表
const loadUserList = async () => {
  try {
    const res = await getUserList({ page: 1, size: 999 });
    if (res.res_code === '0000') {
      userList.value = res.data.list || [];
    } else {
      ElMessage.error(`加载用户列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，加载用户列表失败');
  }
};


// 选择执行人时自动填充学号
const onUserSelect = () => {
  if (!taskEditForm.selectUserId) {
    // 清空选择时，学号也清空
    taskEditForm.inputStudentNumber = '';
    return;
  }
  // 根据选中的user_id查找对应的用户对象
  const selectedUser = userList.value.find(user => user.user_id === taskEditForm.selectUserId);
  if (selectedUser && selectedUser.student_number) {
    // 将用户的学号赋值到表单中
    taskEditForm.inputStudentNumber = selectedUser.student_number;
  } else {
    taskEditForm.inputStudentNumber = '';
    ElMessage.warning('该执行人暂无学号信息，请联系管理员补充');
  }
};
// 添加执行人（优化：增加去重、金额校验）
const addExecutor = () => {
  // 1. 基础校验
  if (!taskEditForm.selectUserId || !taskEditForm.inputAmount) {
    ElMessage.warning('请选择执行人和输入金额');
    return;
  }
  // 2. 金额合法性校验
  const amount = Number(taskEditForm.inputAmount);
  if (amount <= 0) {
    ElMessage.warning('金额必须大于0');
    return;
  }
  // 3. 查找用户
  const user = userList.value.find(u => u.user_id === taskEditForm.selectUserId);
  if (!user) {
    ElMessage.warning('选择的执行人不存在');
    return;
  }
  // 4. 去重校验（避免重复添加同一执行人）
  const isDuplicate = taskEditForm.executors.some(item => item.user_id === user.user_id);
  if (isDuplicate) {
    ElMessage.warning('该执行人已添加，请勿重复添加');
    return;
  }
  // 5. 添加执行人
  taskEditForm.executors.push({
    user_id: user.user_id,
    real_name: user.real_name,
    student_number: taskEditForm.inputStudentNumber, // 自动填充的学号
    amount: amount
  });
  // 清空选择框
  taskEditForm.selectUserId = '';
  taskEditForm.inputStudentNumber = '';
  taskEditForm.inputAmount = '';
};

// 移除单个执行人
const removeExecutor = (index) => {
  taskEditForm.executors.splice(index, 1);
};

// 批量清空所有执行人
const clearAllExecutors = () => {
  if (taskEditForm.executors.length === 0) return;
  ElMessageBox.confirm(
      '确定要清空所有执行人吗？',
      '清空确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    taskEditForm.executors = [];
  });
};

// 提交任务（适配多执行人的参数处理）
const submitTask = async () => {
  // 1. 新增部门选择的校验
  if (!taskEditForm.departmentId) {
    ElMessage.warning('请选择所属部门');
    return;
  }
  // 1. 基础表单校验
  if (!taskEditForm.article_title || !taskEditForm.created_at) {
    ElMessage.warning('请填写任务名称和日期');
    return;
  }
  if (taskEditForm.executors.length === 0) {
    ElMessage.warning('请添加至少一个执行人');
    return;
  }

  // 2. 处理多执行人的参数（关键：适配后端接收格式）
  const realNames = taskEditForm.executors.map(item => item.real_name);
  const userIds = taskEditForm.executors.map(item => Number(item.user_id));
  const studentNumbers = taskEditForm.executors.map(item => item.student_number);
  const totalFee = taskEditForm.executors.reduce((sum, item) => sum + Number(item.amount), 0);
  const [year, month] = taskEditForm.created_at.split('-');
  const statistical_month = `${year}-${month}`;

  const submitParams = {
    record_id: taskEditForm.record_id || '',
    article_title: taskEditForm.article_title,
    created_at: taskEditForm.created_at,
    statistical_month: statistical_month,
    real_names: realNames,
    user_id: userIds,
    student_numbers: studentNumbers, // 新增：学号数组（后端必填）
    article_type: taskEditForm.articleType, // 新增：稿件类型（后端必填）
    fee_amount: totalFee,
    // 将选中的部门ID转为数字类型传入
    department_id: Number(taskEditForm.departmentId),
    executor_amounts: taskEditForm.executors.map(item => item.amount)
  };

  try {
    console.log('提交参数：', submitParams);
    const res = await addRoyaltyRecord(submitParams);
    if (res.res_code === '0000') {
      ElMessage.success(taskEditForm.record_id ? '编辑任务成功' : '新建任务成功');
      // 重置表单
      resetTaskForm();
      // 切回查询标签（需确保currentTab已定义）
      // currentTab.value = 'query';
      // 刷新任务列表（需确保fetchTaskList已定义）
      // await fetchTaskList();
    } else {
      ElMessage.error(`${taskEditForm.record_id ? '编辑' : '新建'}任务失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error(`网络异常，${taskEditForm.record_id ? '编辑' : '新建'}任务失败`);
    console.error('提交任务报错：', err);
  }
};
// 重置表单时重置部门选择
const resetTaskForm = () => {
  Object.assign(taskEditForm, {
    record_id: '',
    article_title: '',
    created_at: '',
    articleType: '',
    departmentId: '1', // 重置为默认选中新闻部
    selectUserId: '',
    inputStudentNumber: '',
    inputAmount: '',
    executors: []
  });
};

// 初始化加载用户列表
onMounted(() => {
  loadUserList();
});
//=================== 初始化 ===================
onMounted(async () => {
  await fetchTaskList();
  await loadUserList();
});
</script>

<style scoped>
/* 基础样式 */
.tab-buttons {
  margin-bottom: 20px;
}
.tab-btn {
  padding: 8px 20px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
  margin-right: 2px;
  font-size: 14px;
}
.tab-btn.active {
  background-color: #9b8eb4;
  color: #fff;
}

/* 通用表单样式 */
.form-select, .form-input {
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 2px;
  font-size: 14px;
}
.form-select {
  width: 120px;
}
.form-input {
  width: 200px;
}
.small-input {
  width: 100px;
}
.query-btn, .add-btn, .submit-btn {
  padding: 6px 15px;
  border: none;
  border-radius: 2px;
  cursor: pointer;
  font-size: 14px;
}
.query-btn {
  background-color: #9b8eb4;
  color: #fff;
}
.add-btn {
  background-color: #67c23a;
  color: #fff;
}
.submit-btn {
  background-color: #409eff;
  color: #fff;
  align-self: flex-end;
}

/* 任务查询区域 */
.query-form {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
}
.task-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ccc;
}
.task-table th, .task-table td {
  padding: 8px 10px;
  border: 1px solid #ccc;
  text-align: left;
  font-size: 14px;
}
.task-table th {
  background-color: #e0e0e0;
}
.op-btn {
  padding: 4px 8px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 2px;
  cursor: pointer;
  margin-right: 5px;
  font-size: 12px;
}
.op-btn.danger {
  background-color: rgba(192, 192, 224, 0.85);
}
.empty-tip {
  text-align: center;
  padding: 30px;
  color: #999;
  font-style: italic;
}

/* 反馈处理区域 */
.feedback-filter {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}
.feedback-item-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.feedback-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  background-color: #f9f9f9;
}
.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: bold;
}
.feedback-id {
  color: #666;
}
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-tag.pending {
  background-color: #fff7e6;
  color: #ff9900;
}
.status-tag.replied {
  background-color: #e6f7ff;
  color: #1890ff;
}
.feedback-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  line-height: 1.6;
}
.feedback-content .label {
  color: #999;
  font-weight: 500;
}
.feedback-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}
.page-btn {
  padding: 6px 12px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 弹窗样式 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.dialog-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.reply-textarea {
  width: 100%;
  height: 120px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}
.dialog-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
.cancel-btn {
  padding: 8px 16px;
  background-color: #e0e0e0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.confirm-btn {
  padding: 8px 16px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 任务管理区域 */
.manage-title {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
}
.title-line {
  width: 4px;
  height: 20px;
  background-color: #9b8eb4;
  margin-right: 8px;
}
.manage-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.form-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.executor-list {
  margin-top: 10px;
}
.executor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 5px;
}
.executor-tag {
  padding: 4px 8px;
  background-color: #e6f7ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.tag-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
}
.tag-close:hover {
  color: #f56c6c;
}
</style>