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
            <th>任务名称</th>
            <th>参与人</th>
            <th>金额详情</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="taskList.length === 0">
            <td colspan="4" class="empty-tip">暂无任务记录</td>
          </tr>
          <tr v-for="item in taskList" :key="item.record_id">
            <td>{{ item.created_at }}</td>
            <td>{{ item.article_title || 0 }}</td>
            <td>{{ item.real_names?.join(', ') || '-' }}</td>
            <td> {{ item.fee_amount || 0 }}元</td>
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

      <!-- 新的任务管理区域（添加稿费记录） -->
      <div v-if="currentTab === 'manage'">
        <div class="manage-title">
          <div class="title-line"></div>
          <h3>添加稿费记录</h3>
        </div>
        <div class="manage-form">
          <!-- 选择成员按钮 -->
          <div class="form-item">
            <button class="select-member-btn" @click="openUserListModal">选择成员</button>
          </div>

          <!-- 已选成员列表（动态渲染） -->
          <div class="selected-members" v-if="selectedMembers.length > 0">
            <h4>已选成员：</h4>
            <div class="member-item" v-for="(member, index) in selectedMembers" :key="member.user_id">
              <span>成员{{ index + 1 }}：姓名={{ member.real_name }}，学号={{ member.student_number }}，ID={{ member.user_id }}</span>
              <button class="del-member-btn" @click="deleteMember(index)">删除</button>
            </div>
          </div>

          <!-- 稿件标题（手动输入） -->
          <div class="form-item">
            <label>稿件标题:</label>
            <input type="text" placeholder="输入稿件标题" class="form-input" v-model="addForm.article_title">
          </div>

          <!-- 稿件类型（单选框，限定选项） -->
          <div class="form-item">
            <label>稿件类型:</label>
            <div class="radio-group">
              <label class="radio-item">
                <input type="radio" name="article_type" v-model="addForm.article_type" value="校对"> 日历图
              </label>
              <label class="radio-item">
                <input type="radio" name="article_type" v-model="addForm.article_type" value="编辑"> 策划
              </label>
            </div>
          </div>

          <!-- 稿费金额（数字输入，限制小数） -->
          <div class="form-item">
            <label>稿费金额:</label>
            <input type="number" step="1" min="0" placeholder="0.00" class="form-input" v-model="addForm.fee_amount">
          </div>

          <!-- 统计月份（月份选择器，自动匹配YYYY-MM格式） -->
          <div class="form-item">
            <label>统计月份:</label>
            <input
                type="month"
                class="form-input"
                v-model="addForm.statistical_month"
                min="2026-01"
                max="2035-12"
            >
          </div>

          <!-- 部门选择（下拉框，映射部门ID） -->
<!--          <div class="form-item">
            <label>所属部门:</label>
            <select class="form-select" v-model="addForm.department_id">
              <option value="">请选择部门</option>
              <option value="1">新闻部</option>
              <option value="2">编辑部</option>
              <option value="3">运营部</option>
            </select>
          </div>-->

          <button class="submit-btn" @click="submitAddRoyalty" :disabled="selectedMembers.length === 0">提交添加</button>
        </div>

        <!-- 用户列表弹窗 -->
        <div class="modal-mask" v-if="showUserListModal" @click="closeUserListModal">
          <div class="modal-content" @click.stop>
            <div class="modal-header">
              <h3>选择成员</h3>
              <button class="close-modal" @click="closeUserListModal">×</button>
            </div>
            <div class="modal-body">
              <!-- 用户列表（从接口获取后渲染） -->
              <div class="user-list" v-if="userList.length > 0">
                <div class="user-item" v-for="user in userList" :key="user.user_id">
                  <div class="user-info">
                    <span>姓名：{{ user.real_name }}</span>
                    <span>学号：{{ user.student_number }}</span>
                    <span>ID：{{ user.user_id }}</span>
                  </div>
                  <button
                      class="select-user-btn"
                      @click="selectUser(user)"
                      :disabled="selectedMembers.some(m => m.user_id === user.user_id)"
                  >
                    {{ selectedMembers.some(m => m.user_id === user.user_id) ? '已选择' : '选择' }}
                  </button>
                </div>
              </div>
              <div class="empty-tip" v-else>暂无用户数据</div>
            </div>
            <div class="modal-footer">
              <button class="user-modal-confirm-btn" @click="closeUserListModal">确认选择</button>
            </div>
          </div>
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
      await loadUserList(); // 切换到任务管理时加载用户列表
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

// 打开任务编辑弹窗（兼容原有逻辑，跳转到任务管理标签）
const openTaskEditDialog = (item = {}) => {
  currentTab.value = 'manage';
  // 可选：如果需要编辑功能，可在这里给addForm赋值原有数据
  addForm.article_title = item.article_title || '';
  addForm.article_type = item.article_type || '';
  addForm.fee_amount = item.fee_amount || '';
  // 强制校验：回显月份不能小于2026-01
  const targetMonth = item.statistical_month || '2026-01';
  addForm.statistical_month = targetMonth < '2026-01' ? '2026-01' : targetMonth;
  addForm.department_id = item.department_id || '';
  // 可选：回显已选成员
  if (item.user_id && item.real_name && item.student_number) {
    selectedMembers.value = Array.isArray(item.user_id)
        ? item.user_id.map((id, index) => ({
          user_id: id,
          real_name: item.real_name[index],
          student_number: item.student_number[index]
        }))
        : [{
          user_id: item.user_id,
          real_name: item.real_name,
          student_number: item.student_number
        }];
  }
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

//=================== 新任务管理模块（添加稿费记录） ===================
// ============ 选择成员弹窗相关变量 ============
const showUserListModal = ref(false); // 用户列表弹窗显隐
const userList = ref([]); // 从接口获取的用户列表
const selectedMembers = ref([]); // 已选成员列表

// ============ 添加稿费记录相关变量 ============
const addForm = reactive({
  article_title: '',
  article_type: '', // 校对/编辑
  fee_amount: '', // 改为空字符串，避免初始0值干扰
  statistical_month: '2026-01',// YYYY-MM（固定默认最小月份）
  department_id: '' // 部门ID（下拉框值）
});

// ============ 选择成员弹窗方法 ============
// 加载用户列表（独立封装，供标签切换和弹窗打开调用）
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

// 打开用户列表弹窗并加载数据
const openUserListModal = async () => {
  showUserListModal.value = true;
  await loadUserList(); // 打开弹窗时确保用户列表已加载
};

// 关闭用户列表弹窗
const closeUserListModal = () => {
  showUserListModal.value = false;
};

// 选择单个用户
const selectUser = (user) => {
  // 避免重复选择
  if (selectedMembers.value.some(m => m.user_id === user.user_id)) {
    ElMessage.warning(`已选择过${user.real_name}，请勿重复选择`);
    return;
  }
  // 添加到已选列表
  selectedMembers.value.push({
    user_id: user.user_id,
    real_name: user.real_name,
    student_number: user.student_number
  });
  ElMessage.success(`已选择 ${user.real_name}`);
};

// 删除已选成员
const deleteMember = (index) => {
  selectedMembers.value.splice(index, 1);
  ElMessage.success('已删除该成员');
};

// ============ 添加稿费记录方法 ============
const submitAddRoyalty = async () => {
  // 1. 表单验证（增强）
  if (selectedMembers.value.length === 0) {
    ElMessage.warning('请至少选择一名成员');
    return;
  }
  if (!addForm.article_title) {
    ElMessage.warning('请输入稿件标题');
    return;
  }
  if (!addForm.article_type) {
    ElMessage.warning('请选择稿件类型（校对/编辑）');
    return;
  }
  if (!addForm.fee_amount || Number(addForm.fee_amount) <= 0) {
    ElMessage.warning('请输入有效的稿费金额（大于0）');
    return;
  }
  if (!addForm.statistical_month) {
    ElMessage.warning('请选择统计月份');
    return;
  }
// 二次校验：确保月份不小于2026-01
  if (addForm.statistical_month < '2026-01') {
    ElMessage.warning('统计月份不能早于2026年1月，请重新选择');
    addForm.statistical_month = '2026-01'; // 强制重置为最小月份
    return;
  }
  /*if (!addForm.department_id) {
    ElMessage.warning('请选择所属部门');
    return;
  }*/

  // 2. 整理接口参数（从已选成员自动生成数组）
  const submitData = {
    user_id: selectedMembers.value.map(m => m.user_id), // ID数组
    real_names: selectedMembers.value.map(m => m.real_name), // 姓名数组
    student_numbers: selectedMembers.value.map(m => m.student_number), // 学号数组
    article_title: addForm.article_title,
    article_type: addForm.article_type,
    fee_amount: Number(addForm.fee_amount), // 转为数字
    statistical_month: addForm.statistical_month,
    //department_id: Number(addForm.department_id) // 转为数字
  };

  // 3. 调用添加接口
  try {
    const res = await addRoyaltyRecord(submitData);
    if (res.res_code === '0000') {
      ElMessage.success('添加稿费记录成功');
      // 清空表单和已选成员
      Object.keys(addForm).forEach(key => {
        addForm[key] = key === 'statistical_month' ? '2026-01' : '';
      });
      selectedMembers.value = [];
      // 切换到查询标签页并刷新
      currentTab.value = 'query';
      await fetchTaskList();
    } else {
      ElMessage.error(`添加失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，添加失败');
    console.error('添加稿费失败详情：', err);
  }
};

//=================== 初始化 ===================
onMounted(async () => {
  await fetchTaskList();
  await fetchAllFeedback();
});
</script>
<style scoped>
/* ============ 新增/修改的任务管理区域样式（放在最前面） ============ */
/* 整体视觉优化 - 圆角、阴影、间距调整 */
.manage-title {
margin-bottom: 24px;
display: flex;
align-items: center;
}
.manage-title h3 {
margin: 0;
font-size: 18px;
font-weight: 600;
color: #333;
}
.manage-form {
max-width: 700px;
padding: 20px;
background-color: #fff;
border-radius: 12px;
/*box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);*/
gap: 20px;
}
/* 表单元素优化 */
.manage-form .form-item {
margin-bottom: 0;
}
.manage-form .form-item label {
color: #444;
font-size: 14px;
margin-bottom: 6px;
}
.manage-form .form-input,
.manage-form .form-select {
border-color: #e5e7eb;
transition: border-color 0.2s ease, box-shadow 0.2s ease;
font-size: 14px;
}
.manage-form .form-input:focus,
.manage-form .form-select:focus {
border-color: #9b8eb4;
box-shadow: 0 0 0 3px rgba(155, 142, 180, 0.1);
outline: none;
}
/* 按钮样式优化 */
.select-member-btn {
background-color: #9b8eb4;
transition: background-color 0.2s ease;
border-radius: 6px;
font-size: 14px;
font-weight: 500;
}
.select-member-btn:hover {
background-color: #8a7aa8;
}
.manage-form .submit-btn {
background-color: #9b8eb4;
border-radius: 6px;
font-size: 14px;
font-weight: 500;
padding: 10px 24px;
transition: background-color 0.2s ease;
margin-top: 8px;
}
.manage-form .submit-btn:hover:not(:disabled) {
background-color: #8a7aa8;
}
.manage-form .submit-btn:disabled {
background-color: #d1c9d9;
color: #fff;
}
/* 已选成员列表优化 */
.selected-members {
border-radius: 8px;
border-color: #f0f0f0;
background-color: #f9f9fb;
margin: 12px 0;
padding: 16px;
}
.selected-members h4 {
margin: 0 0 12px 0;
font-size: 14px;
color: #555;
font-weight: 600;
}
.member-item {
padding: 8px 0;
border-bottom-color: #eee;
align-items: center;
}
.member-item span {
font-size: 13px;
color: #666;
line-height: 1.5;
}
.del-member-btn {
border-radius: 4px;
font-size: 12px;
padding: 3px 8px;
background-color: #fef2f2;
color: #f56c6c;
transition: background-color 0.2s ease;
}
.del-member-btn:hover {
background-color: #fee2e2;
}
/* 弹窗样式优化 */
.modal-mask {
backdrop-filter: blur(2px);
}
.modal-content {
border-radius: 12px;
box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
width: 650px;
padding: 24px;
}
.modal-header {
margin-bottom: 20px;
padding-bottom: 12px;
border-bottom-color: #f0f0f0;
}
.modal-header h3 {
margin: 0;
font-size: 16px;
font-weight: 600;
color: #333;
}
.close-modal {
font-size: 22px;
color: #999;
transition: color 0.2s ease;
}
.close-modal:hover {
color: #f56c6c;
}
.user-item {
border-radius: 8px;
border-color: #f0f0f0;
padding: 12px;
transition: background-color 0.2s ease;
}
.user-item:hover {
background-color: #f9f9fb;
}
.user-info {
gap: 20px;
}
.user-info span {
font-size: 13px;
color: #666;
}
.select-user-btn {
border-radius: 6px;
font-size: 13px;
padding: 4px 12px;
border-color: #9b8eb4;
color: #9b8eb4;
transition: all 0.2s ease;
}
.select-user-btn:hover:not(:disabled) {
background-color: #9b8eb4;
color: #fff;
}
.select-user-btn:disabled {
border-color: #ddd;
color: #999;
background-color: #f5f5f5;
}
.user-modal-confirm-btn {
border-radius: 6px;
font-size: 14px;
padding: 8px 20px;
background-color: #9b8eb4;
transition: background-color 0.2s ease;
}
.user-modal-confirm-btn:hover {
background-color: #8a7aa8;
}
.empty-tip {
color: #999;
font-size: 14px;
padding: 40px 20px;
}
/* 单选框样式优化 */
.radio-group {
gap: 24px;
}
.radio-item {
font-size: 14px;
color: #666;
}
.radio-item input {
width: 16px;
height: 16px;
margin-right: 6px;
accent-color: #9b8eb4;
}

/* ============ 原有样式（保持不变） ============ */
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

/* ============ 原有任务管理区域样式（保持不变） ============ */
/* 选择成员相关样式 */
.select-member-btn {
padding: 8px 16px;
background-color: #9b8eb4;
color: #fff;
border: none;
border-radius: 4px;
cursor: pointer;
margin-bottom: 10px;
}
.select-member-btn:hover {
background-color:#C0C0C0;
}

/* 已选成员列表 */
.selected-members {
margin: 10px 0;
padding: 10px;
border: 1px solid #eee;
border-radius: 4px;
}
.member-item {
display: flex;
justify-content: space-between;
align-items: center;
padding: 6px 0;
border-bottom: 1px dashed #eee;
}
.del-member-btn {
padding: 2px 8px;
background-color: #f56c6c;
color: #fff;
border: none;
border-radius: 2px;
cursor: pointer;
font-size: 12px;
}

/* 单选框组 */
.radio-group {
display: flex;
gap: 20px;
margin-top: 8px;
}
.radio-item {
display: flex;
align-items: center;
cursor: pointer;
}
.radio-item input {
margin-right: 4px;
}

/* 下拉选择框 */
.manage-form .form-select {
width:150px;
padding: 8px;
border: 1px solid #dcdfe6;
border-radius: 4px;
margin-top: 4px;
}

/* 弹窗遮罩 */
.modal-mask {
position: fixed;
top: 0;
left: 0;
width: 100%;
height: 100%;
background-color: rgba(0, 0, 0, 0.5);
display: flex;
justify-content: center;
align-items: center;
z-index: 1000;
}
.modal-content {
width: 600px;
background-color: #fff;
border-radius: 8px;
padding: 20px;
}
.modal-header {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 15px;
border-bottom: 1px solid #eee;
padding-bottom: 10px;
}
.close-modal {
background: none;
border: none;
font-size: 20px;
cursor: pointer;
color: #999;
}
.modal-body {
max-height: 400px;
overflow-y: auto;
margin-bottom: 15px;
}
.user-list {
display: flex;
flex-direction: column;
gap: 10px;
}
.user-item {
display: flex;
justify-content: space-between;
align-items: center;
padding: 8px;
border: 1px solid #eee;
border-radius: 4px;
}
.user-info {
display: flex;
gap: 15px;
}
.select-user-btn {
padding: 4px 12px;
border: 1px solid #9b8eb4;
color: #9b8eb4;
background: #fff;
border-radius: 4px;
cursor: pointer;
}
.select-user-btn:disabled {
background-color: #eee;
color: #999;
border-color: #ddd;
cursor: not-allowed;
}
.modal-footer {
display: flex;
justify-content: flex-end;
}
/* 选择成员弹窗确认按钮 */
.user-modal-confirm-btn {
padding: 8px 16px;
background-color: #9b8eb4;
color: #fff;
border: none;
border-radius: 4px;
cursor: pointer;
}

/* 任务管理区域样式 */
.manage-title {
margin-bottom: 20px;
}

.manage-title .title-line {
width: 4px;
height: 20px;
background-color: #9b8eb4;
margin-right: 8px;
}

.manage-form {
max-width: 600px;
display: flex;
flex-direction: column;
gap: 15px;
}
.manage-form .form-item {
margin-bottom: 15px;
}
.manage-form .form-item label {
display: block;
margin-bottom: 4px;
font-weight: 500;
}
.manage-form .form-input {
width: 150px;
padding: 8px;
border: 1px solid #dcdfe6;
border-radius: 4px;
}
.manage-form .submit-btn {
padding: 10px 20px;
background-color: #9b8eb4;
color: #fff;
border: none;
border-radius: 4px;
cursor: pointer;
align-self: flex-start;
}
.manage-form .submit-btn:disabled {
background-color: #bbb;
cursor: not-allowed;
}
</style>