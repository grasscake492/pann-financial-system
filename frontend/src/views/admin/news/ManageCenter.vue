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
            <input type="number" step="1" class="form-input" v-model="editForm.fee_amount">
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
            <button class="feedback-confirm-btn" @click="submitEdit">确认修改</button>
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
                <input type="radio" name="article_type" v-model="addForm.article_type" value="校对"> 校对
              </label>
              <label class="radio-item">
                <input type="radio" name="article_type" v-model="addForm.article_type" value="编辑"> 编辑
              </label>
            </div>
          </div>

          <!-- 稿费金额（数字输入，限制小数） -->
          <div class="form-item">
            <label>稿费金额:</label>
            <input type="number" step="0.01" min="0" placeholder="0.00" class="form-input" v-model="addForm.fee_amount">
          </div>

          <!-- 统计月份（月份选择器，自动匹配YYYY-MM格式） -->
          <div class="form-item">
            <label>统计月份:</label>
            <input
                type="month"
                class="form-input"
                v-model="addForm.statistical_month"
                min="2026-01"
            >
          </div>

          <!-- 部门选择（下拉框，映射部门ID） -->
          <div class="form-item">
            <label>所属部门:</label>
            <select class="form-select" v-model="addForm.department_id">
              <option value="">请选择部门</option>
              <option value="1">新闻部</option>
              <option value="2">编辑部</option>
              <option value="3">运营部</option>
            </select>
          </div>

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
              <button class="feedback-confirm-btn" @click="submitReply">提交回复</button>
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
              <button class="feedback-confirm-btn" @click="submitStatusUpdate">确认更新</button>
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
import { ElMessage, ElMessageBox } from "element-plus"; // 新增：引入ElMessageBox（删除确认）
import { FEEDBACK_STATUS_LABEL, FEEDBACK_STATUS_CLASS } from '@/utils/feedbackStatus.js';
import {addRoyaltyRecord, deleteRoyaltyRecord, getDepartmentRoyalty, updateRoyaltyRecord} from "@/api/royalty.js";
// 新增：导入用户列表接口
import { getUserList } from '@/api/user.js';
import {generateSign} from "@/utils/format.js";

// ========== 新增：选择成员弹窗相关变量 ==========
const showUserListModal = ref(false); // 用户列表弹窗显隐
const userList = ref([]); // 从接口获取的用户列表
const selectedMembers = ref([]); // 已选成员列表

// ========== 稿费查询相关变量（原有，保留） ==========
const royaltyList = ref([]);
const filteredRoyaltyList = ref([]);
const totalRoyalty = ref(0);
const filterMonth = ref('');
const filterTitle = ref('');
const filterName = ref('');
const currentPage = ref(1);
const pageSize = ref(2);
const royaltyQueryParams = reactive({
  startDate: '',
  endDate: '',
  page: 1,
  size: 10,
  user_id: ''
});
const totalRoyaltyPages = computed(() => {
  return Math.ceil(totalRoyalty.value / royaltyQueryParams.size) || 1;
});

// ========== 稿费前端筛选方法（原有，保留） ==========
const handleRoyaltyFilter = () => {
  let list = [...royaltyList.value];
  if (filterMonth.value) {
    list = list.filter(item => item.statistical_month?.endsWith(filterMonth.value));
  }
  if (filterTitle.value) {
    const keyword = filterTitle.value.trim().toLowerCase();
    list = list.filter(item => item.article_title?.toLowerCase().includes(keyword));
  }
  filteredRoyaltyList.value = list;
};

// ========== 稿费查询方法（原有，保留） ==========
const fetchDepartmentRoyalty = async () => {
  try {
    const res = await getDepartmentRoyalty(royaltyQueryParams);
    if (res.res_code === '0000') {
      royaltyList.value = res.data.list || [];
      totalRoyalty.value = res.data.total || 0;
      handleRoyaltyFilter();
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

// ========== 稿费分页切换（原有，保留） ==========
const handleRoyaltyPageChange = (page) => {
  if (page < 1 || page > totalRoyaltyPages.value) return;
  royaltyQueryParams.page = page;
  fetchDepartmentRoyalty();
};

// ========== 稿费修改弹窗变量（原有，保留） ==========
const editDialogVisible = ref(false);
const currentEditItem = ref({});
const editForm = reactive({
  article_title: '',
  article_type: '',
  fee_amount: 0,
  statistical_month: '',
  description: ''
});

// ========== 稿费修改方法（原有，保留） ==========
const openEditDialog = (item) => {
  currentEditItem.value = item;
  editForm.article_title = item.article_title;
  editForm.article_type = item.article_type;
  editForm.fee_amount = item.fee_amount;
  editForm.statistical_month = item.statistical_month;
  editForm.description = item.description || '';
  editDialogVisible.value = true;
};
const submitEdit = async () => {
  try {
    const res = await updateRoyaltyRecord(currentEditItem.value.record_id, editForm);
    if (res.res_code === '0000') {
      ElMessage.success('修改成功');
      editDialogVisible.value = false;
      await fetchDepartmentRoyalty();
    } else {
      ElMessage.error(`修改失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，修改失败');
  }
};

// ========== 稿费删除方法（原有，优化：增加确认提示） ==========
const handleDelete = async (recordId) => {
  try {
    // 新增：删除前确认
    const confirm = await ElMessageBox.confirm(
        '确定要删除这条稿费记录吗？',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    );
    if (!confirm) return;

    const res = await deleteRoyaltyRecord(recordId);
    if (res.res_code === '0000') {
      ElMessage.success('删除成功');
      await fetchDepartmentRoyalty();
    } else {
      ElMessage.error(`删除失败：${res.res_msg}`);
    }
  } catch (err) {
    if (err !== 'cancel') { // 排除取消操作的异常
      ElMessage.error('网络异常，删除失败');
    }
  }
};

// ========== 添加稿费记录相关变量（修改：删除手动输入的_str字段，新增规范字段） ==========
const addForm = reactive({
  // 移除：手动输入的字符串字段
  // user_id_str: '',
  // real_names_str: '',
  // student_numbers_str: '',
  // 保留：接口必填字段（优化默认值）
  article_title: '',
  article_type: '', // 校对/编辑
  fee_amount: '', // 改为空字符串，避免初始0值干扰
  statistical_month: '2026-01',// YYYY-MM
  department_id: '' // 部门ID（下拉框值）
});

// ========== 新增：选择成员弹窗方法 ==========
// 打开用户列表弹窗并加载数据
const openUserListModal = async () => {
  showUserListModal.value = true;
  try {
    // 调用用户列表接口（按需传参，这里默认查第1页，每页100条）
    const sign = await generateSign();
    const res = await getUserList({
      page: 1,
      size: 100,
      sign: sign// 替换为实际签名生成逻辑
    });
    if (res.res_code === '0000') {
      userList.value = res.data.list || [];
    } else {
      ElMessage.error(`加载用户列表失败：${res.res_msg}`);
      userList.value = [];
    }
  } catch (err) {
    ElMessage.error('网络异常，加载用户列表失败');
    userList.value = [];
  }
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

// ========== 添加稿费记录（修改：适配选择成员逻辑） ==========
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
  if (!addForm.department_id) {
    ElMessage.warning('请选择/输入部门ID');
    return;
  }

  // 2. 整理接口参数（从已选成员自动生成数组）
  const submitData = {
    user_id: selectedMembers.value.map(m => m.user_id), // ID数组
    real_names: selectedMembers.value.map(m => m.real_name), // 姓名数组
    student_numbers: selectedMembers.value.map(m => m.student_number), // 学号数组
    article_title: addForm.article_title,
    article_type: addForm.article_type,
    fee_amount: Number(addForm.fee_amount), // 转为数字
    statistical_month: addForm.statistical_month,
    department_id: Number(addForm.department_id) // 转为数字
  };

  // 3. 调用添加接口
  try {
    const res = await addRoyaltyRecord(submitData);
    if (res.res_code === '0000') {
      ElMessage.success('添加稿费记录成功');
      // 清空表单和已选成员
      Object.keys(addForm).forEach(key => {
        addForm[key] = '';
      });
      selectedMembers.value = [];
      // 切换到查询标签页并刷新
      currentTab.value = 'query';
      fetchDepartmentRoyalty();
    } else {
      ElMessage.error(`添加失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，添加失败');
    console.error('添加稿费失败详情：', err);
  }
};

// ========== 生命周期补充（原有，保留） ==========
onMounted(() => {
  if (currentTab.value === 'query') {
    fetchDepartmentRoyalty();
  }
});

// ========== 基础变量定义（原有，保留） ==========
const currentTab = ref('query');
const feedbackList = ref([]);
const totalFeedback = ref(0);

// 反馈查询参数（原有，保留）
const feedbackQueryParams = reactive({
  page: 1,
  size: 100,
  status: ''
});

// 状态弹窗/回复弹窗变量（原有，保留）
const replyDialogVisible = ref(false);
const currentFeedbackId = ref('');
const replyForm = reactive({ reply_content: '' });
const statusDialogVisible = ref(false);
const statusForm = reactive({ status: 'pending' });

// ========== 计算属性（前端筛选+分页）（原有，保留） ==========
const filteredFeedbackList = computed(() => {
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
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return list.slice(start, end);
});

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

// ========== 方法定义（反馈相关，原有，保留） ==========
const fetchAllFeedback = async () => {
  try {
    const res = await getAllFeedback(feedbackQueryParams);
    if (res.res_code === '0000') {
      feedbackList.value = res.data.list || [];
      totalFeedback.value = res.data.total || 0;
      currentPage.value = 1;
    } else {
      ElMessage.error(`查询失败：${res.res_msg}`);
      feedbackList.value = [];
    }
  } catch (err) {
    ElMessage.error('网络异常，查询反馈失败');
    feedbackList.value = [];
  }
};

const handleFrontendFilter = () => {
  currentPage.value = 1;
};

const handlePageChange = (page) => {
  if (page < 1 || page > totalItemPages.value) return;
  currentPage.value = page;
};

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

// ========== 生命周期（原有，保留） ==========
onMounted(() => {
  console.log('进入新闻部，组件挂载，开始初始化数据');
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
  width: 150px;
  padding: 6px 8px;
  border: 1px solid #ccc;
}
.query-btn {
  padding: 6px 15px;
  background-color: #9b8eb4;
  color: #fff;
  border:  1px solid;
  border-radius: 5px;
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
/* 反馈弹窗确认按钮 */
.feedback-confirm-btn {
  padding: 6px 12px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}
.form-textarea{
  width: 100%;
  height: 100px;
  padding: 8px;
  border: 1px solid #ccc;
  resize: none;
}
/* 新增样式：选择成员按钮 */
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
.form-select {
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
.empty-tip {
  text-align: center;
  padding: 20px;
  color: #999;
}

/* 原有样式保留 */
.manage-title {
  margin-bottom: 20px;
}
.title-line {
  width: 4px;
  height: 20px;
  background-color: #9b8eb4;
  display: inline-block;
  margin-right: 8px;
  vertical-align: middle;
}
.manage-form {
  max-width: 600px;
}
.form-item {
  margin-bottom: 15px;
}
.form-item label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
}
.form-input {
  width: 150px;
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.submit-btn {
  padding: 10px 20px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.submit-btn:disabled {
  background-color: #bbb;
  cursor: not-allowed;
}
</style>