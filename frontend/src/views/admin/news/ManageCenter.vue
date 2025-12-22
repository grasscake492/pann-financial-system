<template>
  <PageBackground2>
    <template #card-content>
      <!-- 标签页按钮 -->
      <div class="tab-buttons">
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'query' }"
            @click="currentTab = 'query'"
        >
          任务查询
        </button>
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'manage' }"
            @click="currentTab = 'manage'"
        >
          任务管理
        </button>
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'feedback' }"
            @click="() => { currentTab = 'feedback'; fetchAllFeedback() }"
        >
          查看反馈
        </button>
      </div>

      <!-- 任务查询区域 -->
      <div v-if="currentTab === 'query'">
        <!-- 筛选栏：改为月份下拉+任务名称输入（前端过滤） -->
        <div class="query-form">
          <label>选择月份:</label>
          <select class="form-select" v-model="filterMonth">
            <option value="">全部</option>
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
          <input type="text" placeholder="输入任务名称查询" class="form-input" v-model="filterTitle">
          <button class="query-btn" @click="handleRoyaltyFilter">查询</button>
        </div>

        <!-- 稿费列表表格：适配接口返回字段 -->
        <table class="task-table">
          <thead>
          <tr>

            <th>任务名称</th>
            <th>参与人</th>
            <th>学号</th>
            <th>稿费金额</th>
            <th>统计月份</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <!-- 空数据提示 -->
          <tr v-if="royaltyList.length === 0">
            <td colspan="9" class="empty-tip">暂无稿费记录</td>
          </tr>
          <!-- 稿费记录循环 -->
          <tr v-for="item in filteredRoyaltyList" :key="item.record_id">
            <td>{{ item.article_title }}</td>
            <!-- 参与人数组转字符串 -->
            <td>{{ item.real_names?.join(', ') || '-' }}</td>
            <!-- 学号数组转字符串 -->
            <td>{{ item.student_numbers?.join(', ') || '-' }}</td>
            <td>{{ item.fee_amount }} </td>
            <td>{{ item.statistical_month }}</td>
            <td>
              <button class="op-btn" @click="openEditDialog(item)">修改</button>
              <button class="op-btn" @click="handleDelete(item.record_id)">删除</button>
            </td>
          </tr>
          </tbody>
        </table>

        <!-- 分页控件（可选，根据需要添加） -->
        <div class="pagination" v-if="royaltyList.length > 0">
          <button class="page-btn" @click="handleRoyaltyPageChange(royaltyQueryParams.page - 1)" :disabled="royaltyQueryParams.page <= 1">
            上一页
          </button>
          <span>第{{ royaltyQueryParams.page }}页 / 共{{ totalRoyaltyPages }}页</span>
          <button class="page-btn" @click="handleRoyaltyPageChange(royaltyQueryParams.page + 1)" :disabled="royaltyQueryParams.page >= totalRoyaltyPages">
            下一页
          </button>
        </div>
      </div>
      <!-- 稿费修改弹窗（新增） -->
      <div class="dialog-mask" v-if="editDialogVisible">
        <div class="dialog-content">
          <h4>修改稿费记录（ID: {{ currentEditItem.record_id }}）</h4>
          <div class="form-item">
            <label>稿件标题:</label>
            <input type="text" class="form-input" v-model="editForm.article_title">
          </div>
          <div class="form-item">
            <label>稿件类型:</label>
            <input type="text" class="form-input" v-model="editForm.article_type">
          </div>
          <div class="form-item">
            <label>稿费金额:</label>
            <input type="number" step="0.01" class="form-input" v-model="editForm.fee_amount">
          </div>
          <div class="form-item">
            <label>统计月份:</label>
            <input type="month" class="form-input" v-model="editForm.statistical_month">
          </div>
          <div class="form-item">
            <label>备注:</label>
            <textarea class="form-textarea" v-model="editForm.description"></textarea>
          </div>
          <div class="dialog-btns">
            <button class="cancel-btn" @click="editDialogVisible = false">取消</button>
            <button class="confirm-btn" @click="submitEdit">确认修改</button>
          </div>
        </div>
      </div>
      <!-- 任务管理区域（添加稿费记录） -->
      <div v-if="currentTab === 'manage'">
        <div class="manage-title">
          <div class="title-line"></div>
          <h3>添加稿费记录</h3>
        </div>
        <div class="manage-form">
          <!-- 用户ID（JSON数组，用逗号分隔输入） -->
          <div class="form-item">
            <label>用户ID:</label>
            <input type="text" placeholder="多个ID用逗号分隔（如：23,24）" class="form-input" v-model="addForm.user_id_str">
          </div>
          <!-- 真实姓名数组（逗号分隔） -->
          <div class="form-item">
            <label>真实姓名:</label>
            <input type="text" placeholder="多个姓名用逗号分隔（如：张三,李四）" class="form-input" v-model="addForm.real_names_str">
          </div>
          <!-- 学号数组（逗号分隔） -->
          <div class="form-item">
            <label>学号:</label>
            <input type="text" placeholder="多个学号用逗号分隔（如：20231234,20231235）" class="form-input" v-model="addForm.student_numbers_str">
          </div>
          <!-- 稿件标题 -->
          <div class="form-item">
            <label>稿件标题:</label>
            <input type="text" placeholder="输入稿件标题" class="form-input" v-model="addForm.article_title">
          </div>
          <!-- 稿件类型 -->
          <div class="form-item">
            <label>稿件类型:</label>
            <input type="text" placeholder="如：校对、编辑" class="form-input" v-model="addForm.article_type">
          </div>
          <!-- 稿费金额 -->
          <div class="form-item">
            <label>稿费金额:</label>
            <input type="number" step="0.01" placeholder="0.00" class="form-input" v-model="addForm.fee_amount">
          </div>
          <!-- 统计月份 -->
          <div class="form-item">
            <label>统计月份:</label>
            <input type="month" class="form-input" v-model="addForm.statistical_month">
          </div>
          <!-- 部门ID -->
          <div class="form-item">
            <label>部门ID:</label>
            <input type="number" placeholder="输入部门ID" class="form-input" v-model="addForm.department_id">
          </div>
          <button class="submit-btn" @click="submitAddRoyalty">提交添加</button>
        </div>
      </div>

      <!-- 反馈查询区域（调整为条目式布局） -->
      <div v-if="currentTab === 'feedback'">
        <!-- 反馈筛选栏（改为月份+姓名模糊查询） -->
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

        <!-- 反馈条目列表（每页2-3个） -->
        <div class="feedback-item-list">
          <!-- 空数据提示 -->
          <div v-if="filteredFeedbackList.length === 0" class="empty-tip">暂无反馈数据</div>

          <!-- 反馈条目 -->
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

        <!-- 分页控件（调整为适配条目布局） -->
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
    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { getAllFeedback, replyFeedback, getFeedbackDetail, updateFeedbackStatus } from "@/api/feedback.js";
import { ElMessage } from "element-plus";
import { FEEDBACK_STATUS_LABEL, FEEDBACK_STATUS_CLASS } from '@/utils/feedbackStatus.js';
import {addRoyaltyRecord, deleteRoyaltyRecord, getDepartmentRoyalty, updateRoyaltyRecord} from "@/api/royalty.js"; // 导入稿费接口




// ========== 稿费查询相关变量（新增） ==========
const royaltyList = ref([]); // 稿费列表数据
const filteredRoyaltyList = ref([]); // 前端过滤后的列表
const totalRoyalty = ref(0); // 稿费总条数
// 前端筛选参数
const filterMonth = ref(''); // 筛选月份（格式：MM）
const filterTitle = ref(''); // 筛选任务名称
const filterName = ref(''); // 筛选姓名
const currentPage = ref(1); // 条目分页的当前页
const pageSize = ref(2); // 每页显示2-3个条目（这里设为2）
// 稿费查询参数（适配接口2.5.10）
const royaltyQueryParams = reactive({
  startDate: '',
  endDate: '',
  page: 1,
  size: 10,
  user_id: ''
});
// 稿费总页数（计算属性）
const totalRoyaltyPages = computed(() => {
  return Math.ceil(totalRoyalty.value / royaltyQueryParams.size) || 1;
});

// ========== 稿费前端筛选方法（新增） ==========
/**
 * 前端过滤稿费列表（按月份+任务名称）
 * 定义位置：组件内script setup
 * 引用位置：筛选栏「查询」按钮点击事件
 */
const handleRoyaltyFilter = () => {
  let list = [...royaltyList.value];
  // 月份过滤（匹配statistical_month的MM部分）
  if (filterMonth.value) {
    list = list.filter(item => item.statistical_month?.endsWith(filterMonth.value));
  }
  // 任务名称模糊过滤
  if (filterTitle.value) {
    const keyword = filterTitle.value.trim().toLowerCase();
    list = list.filter(item => item.article_title?.toLowerCase().includes(keyword));
  }
  filteredRoyaltyList.value = list;
};
// ========== 稿费查询方法（新增） ==========
/**
 * 获取部门稿费列表
 * 定义位置：组件内script setup
 * 引用位置：template中「查询稿费」按钮点击事件
 */
const fetchDepartmentRoyalty = async () => {
  try {
    const res = await getDepartmentRoyalty(royaltyQueryParams);
    if (res.res_code === '0000') {
      royaltyList.value = res.data.list || [];
      totalRoyalty.value = res.data.total || 0;
      handleRoyaltyFilter(); // 接口返回后自动执行一次过滤
    } else {
      ElMessage.error(`查询稿费失败：${res.res_msg}`);
      royaltyList.value = [];
      filteredRoyaltyList.value = [];
    }
  } catch (err) {
    ElMessage.error('网络异常，查询稿费失败');
    royaltyList.value = [];
    filteredRoyaltyList.value = [];
  }
};

/**
 * 稿费分页切换
 * 定义位置：组件内script setup
 * 引用位置：template中分页按钮点击事件
 */
const handleRoyaltyPageChange = (page) => {
  if (page < 1 || page > totalRoyaltyPages.value) return;
  royaltyQueryParams.page = page;
  fetchDepartmentRoyalty(); // 切换页码后重新查询
};
// ========== 稿费修改弹窗变量（新增） ==========
const editDialogVisible = ref(false); // 弹窗显隐
const currentEditItem = ref({}); // 当前修改的条目
const editForm = reactive({ // 修改表单
  article_title: '',
  article_type: '',
  fee_amount: 0,
  statistical_month: '',
  description: ''
});

// ========== 稿费修改方法（新增） ==========
/**
 * 打开修改弹窗
 * 引用位置：表格「修改」按钮点击事件
 */
const openEditDialog = (item) => {
  currentEditItem.value = item;
  // 回显数据到表单
  editForm.article_title = item.article_title;
  editForm.article_type = item.article_type;
  editForm.fee_amount = item.fee_amount;
  editForm.statistical_month = item.statistical_month;
  editForm.description = item.description || '';
  editDialogVisible.value = true;
};

/**
 * 提交修改（调用接口）
 * 需在src/api/royalty.js中定义updateRoyaltyRecord方法
 */
const submitEdit = async () => {
  try {
    const res = await updateRoyaltyRecord(currentEditItem.value.record_id, editForm);
    if (res.res_code === '0000') {
      ElMessage.success('修改成功');
      editDialogVisible.value = false;
      await fetchDepartmentRoyalty(); // 刷新列表
    } else {
      ElMessage.error(`修改失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，修改失败');
  }
};
// ========== 稿费删除方法（新增） ==========
/**
 * 删除稿费记录（调用接口）
 * 引用位置：表格「删除」按钮点击事件
 * 需在src/api/royalty.js中定义deleteRoyaltyRecord方法
 */
const handleDelete = async (recordId) => {
  try {
    // 可先加确认提示（需引入ElMessageBox）
    const res = await deleteRoyaltyRecord(recordId);
    if (res.res_code === '0000') {
      ElMessage.success('删除成功');
      await fetchDepartmentRoyalty(); // 刷新列表
    } else {
      ElMessage.error(`删除失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，删除失败');
  }
};
// ========== 添加稿费记录相关变量（新增） ==========
const addForm = reactive({
  // 字符串形式输入（前端转数组）
  user_id_str: '',
  real_names_str: '',
  student_numbers_str: '',
  // 接口必填字段
  article_title: '',
  article_type: '',
  fee_amount: 0,
  statistical_month: '',
  department_id: ''
});
// ========== 提交添加稿费记录（新增） ==========
/**
 * 提交添加稿费记录（调用2.5.12接口）
 * 引用位置：任务管理区域「提交添加」按钮点击事件
 */
const submitAddRoyalty = async () => {
  // 1. 表单验证
  if (!addForm.article_title || !addForm.article_type || !addForm.fee_amount || !addForm.statistical_month || !addForm.department_id) {
    ElMessage.warning('请填写所有必填字段');
    return;
  }
  if (!addForm.user_id_str || !addForm.real_names_str || !addForm.student_numbers_str) {
    ElMessage.warning('用户ID、姓名、学号不能为空');
    return;
  }

  // 2. 前端将字符串转数组（适配接口JSON数组要求）
  const submitData = {
    user_id: addForm.user_id_str.split(',').map(id => Number(id.trim())),
    real_names: addForm.real_names_str.split(',').map(name => name.trim()),
    student_numbers: addForm.student_numbers_str.split(',').map(num => num.trim()),
    article_title: addForm.article_title,
    article_type: addForm.article_type,
    fee_amount: addForm.fee_amount,
    statistical_month: addForm.statistical_month,
    department_id: addForm.department_id
  };

  // 3. 调用添加接口
  try {
    const res = await addRoyaltyRecord(submitData);
    if (res.res_code === '0000') {
      ElMessage.success('添加稿费记录成功');
      // 清空表单
      Object.keys(addForm).forEach(key => {
        addForm[key] = key.includes('_str') ? '' : 0;
      });
      // 可选：切换到任务查询标签页并刷新列表
      currentTab.value = 'query';
      fetchDepartmentRoyalty();
    } else {
      ElMessage.error(`添加失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，添加失败');
  }
};
// ========== 生命周期补充（可选） ==========
// 若默认选中「任务查询」标签页，挂载时自动查询稿费
onMounted(() => {
  if (currentTab.value === 'query') {
    fetchDepartmentRoyalty();
  }
});



// ========== 基础变量定义 ==========
const currentTab = ref('query');
const feedbackList = ref([]); // 原始反馈列表（接口返回）
const totalFeedback = ref(0);



// 反馈查询参数（接口用）
const feedbackQueryParams = reactive({
  page: 1,
  size: 100, // 接口一次性多查点数据，前端做分页
  status: ''
});

// 状态弹窗/回复弹窗变量
const replyDialogVisible = ref(false);
const currentFeedbackId = ref('');
const replyForm = reactive({ reply_content: '' });
const statusDialogVisible = ref(false);
const statusForm = reactive({ status: 'pending' });

// ========== 计算属性（前端筛选+分页） ==========
// 筛选后的列表（按月份+姓名模糊匹配）
const filteredFeedbackList = computed(() => {
  let list = [...feedbackList.value];

  // 月份筛选
  if (filterMonth.value) {
    list = list.filter(item => {
      const createMonth = item.created_at?.split('-')[1];
      return createMonth === filterMonth.value.padStart(2, '0');
    });
  }

  // 姓名模糊筛选
  if (filterName.value) {
    const keyword = filterName.value.trim().toLowerCase();
    list = list.filter(item => item.real_name?.toLowerCase().includes(keyword));
  }

  // 前端分页（按当前页+每页条数截取）
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return list.slice(start, end);
});

// 条目分页的总页数
const totalItemPages = computed(() => {
  let list = [...feedbackList.value];

  // 先做筛选
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

// ========== 方法定义 ==========
// 获取接口数据
const fetchAllFeedback = async () => {
  try {
    const res = await getAllFeedback(feedbackQueryParams);
    if (res.res_code === '0000') {
      feedbackList.value = res.data.list || [];
      totalFeedback.value = res.data.total || 0;
      currentPage.value = 1; // 重置页码
    } else {
      ElMessage.error(`查询失败：${res.res_msg}`);
      feedbackList.value = [];
    }
  } catch (err) {
    ElMessage.error('网络异常，查询反馈失败');
    feedbackList.value = [];
  }
};

// 前端筛选按钮点击
const handleFrontendFilter = () => {
  currentPage.value = 1; // 筛选后重置页码
};

// 条目分页切换
const handlePageChange = (page) => {
  if (page < 1 || page > totalItemPages.value) return;
  currentPage.value = page;
};

// 回复弹窗相关
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

// 状态弹窗相关
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

// 查看详情（现在ID直接显示在条目里，保留方法）
const viewFeedbackDetail = async (feedbackId) => {
  try {
    const res = await getFeedbackDetail(feedbackId);
    if (res.res_code === '0000') {
      ElMessage.info(`已查询反馈详情，ID：${feedbackId}`);
      console.log('反馈详情：', res.data.feedbackInfo);
    } else {
      ElMessage.error(`查询详情失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，查询详情失败');
  }
};

// ========== 生命周期 ==========
onMounted(() => {
  if (currentTab.value === 'feedback') fetchAllFeedback();
});
</script>

<style scoped>
/* 原有样式保留 */
.tab-buttons {
  margin-bottom: 20px;
}
.tab-btn {
  padding: 8px 20px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
  margin-right: 2px;
}
.tab-btn.active {
  background-color: #c0c0c0;
}
.query-form {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
}
.form-select, .form-input {
  padding: 6px 8px;
  border: 1px solid #ccc;
}
.query-btn {
  padding: 6px 15px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
}
.task-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ccc;
}
.task-table th, .task-table td {
  padding: 8px 10px;
  border: 1px solid #ccc;
}
.task-table th {
  background-color: #e0e0e0;
}
.op-btn {
  border-radius: 5px;
  padding: 4px 8px;
  background-color: #A59EB2;
  border: none;
  cursor: pointer;
  margin-right: 5px;
  color: white;
}
.manage-title {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.manage-title .title-line {
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
.small-input {
  width: 100px;
}
.add-btn, .submit-btn {
  padding: 6px 12px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
}
.submit-btn {
  align-self: flex-end;
}

/* 反馈区域新样式 */
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
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-tag.pending {
  background-color: #fff7e6;
  color: #ff3c3c;
}
.status-tag.replied {
  background-color: #e6f7ff;
  color: #40a9ff;
}
.empty-tip {
  text-align: center;
  padding: 30px;
  color: #999;
}
.pagination {
  margin-top: 20px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  align-items: center;
}
.page-btn {
  padding: 4px 12px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
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
  border-radius: 4px;
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.reply-textarea {
  width: 100%;
  height: 100px;
  padding: 8px;
  border: 1px solid #ccc;
  resize: none;
}
.dialog-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
.cancel-btn {
  padding: 6px 12px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
}
.confirm-btn {
  padding: 6px 12px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  cursor: pointer;
}
.form-textarea{
  width: 100%;
  height: 100px;
  padding: 8px;
  border: 1px solid #ccc;
  resize: none;
}
</style>