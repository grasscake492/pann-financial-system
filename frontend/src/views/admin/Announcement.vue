<template>
  <AnnounceBackground>
    <!-- 页面标题插槽 -->
    <template v-slot:page-title>所有公告</template>

    <!-- 内容卡片插槽 -->
    <template v-slot:card-content>
      <!-- 加载状态提示 -->
      <div v-if="announcementStore.loading" class="loading-tip">加载中...</div>

      <!-- 公告列表 -->
      <div v-else class="announcement-list">
        <div v-if="sortedAnnouncementList.length === 0" class="empty-tip">暂无公告</div>
        <div
            class="announcement-item"
            v-for="item in sortedAnnouncementList"
            :key="item.announcement_id"
            @click="toggleExpand(item.announcement_id)"
        >
          <div class="item-line"></div>
          <div class="item-content">
            <div class="item-date">{{ formatAnnounceDate(item.published_at) }}</div>
            <div class="item-title">{{ item.title }}</div>
            <div v-if="item.isExpanded" class="item-body">
              {{ item.content }}
            </div>
            <!-- 编辑/删除按钮 -->
            <div v-if="item.isExpanded" class="item-actions">
              <button class="edit-btn" @click.stop="handleEdit(item)">编辑</button>
              <button class="delete-btn" @click.stop="handleDelete(item.announcement_id)">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 添加公告按钮 -->
      <div class="add-btn-wrapper">
        <button class="add-announcement-btn" @click="openAddModal()">
          添加公告
        </button>
      </div>

      <!-- 新增/编辑公告弹窗 -->
      <div class="modal-mask" v-if="showModal">
        <div class="modal-content">
          <div class="modal-title">{{ isEdit ? '编辑公告' : '新增公告' }}</div>
          <div class="form-item">
            <label>标题：</label>
            <input
                v-model="formData.title"
                class="input-control"
                placeholder="请输入公告标题"
            >
          </div>
          <div class="form-item">
            <label>正文：</label>
            <textarea
                v-model="formData.content"
                class="textarea-control"
                placeholder="请输入公告正文"
                rows="6"
            ></textarea>
          </div>
          <div class="modal-btns">
            <button class="btn cancel-btn" @click="closeModal">取消</button>
            <button class="btn confirm-btn" @click="isEdit ? confirmEdit() : confirmAdd()">
              {{ isEdit ? '确定编辑' : '确定添加' }}
            </button>
          </div>
        </div>
      </div>
    </template>
  </AnnounceBackground>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import AnnounceBackground from "@/components/AnnounceBackground.vue";

// 导入接口
import {
  getAllAnnouncements,
  publishAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from '@/api/announcement';

// 导入Pinia Store
import { useUserStore } from '@/stores/user';
import { useNoticeStore } from '@/stores/index.js';
import { format } from '@/utils/index.js'; // 签名生成工具

// 实例化Store
const userStore = useUserStore();
const announcementStore = useNoticeStore();
console.log('管理员')
// 响应式数据：公告列表（带展开状态）
const announcementList = computed(() => {
  return announcementStore.announcementList.map(item => ({
    ...item,
    isExpanded: item.isExpanded || false
  }));
});

// 按发布时间降序排序的公告列表
const sortedAnnouncementList = computed(() => {
  return [...announcementList.value].sort((a, b) => {
    const timeA = a.published_at || '';
    const timeB = b.published_at || '';
    return timeB.localeCompare(timeA);
  });
});

// 弹窗状态管理
const showModal = ref(false);
const isEdit = ref(false);
const currentEditId = ref(''); // 存储当前编辑的公告ID

// 表单数据
const formData = ref({
  title: '',
  content: '',
  publisher_id: userStore.userInfo.user_id || ''
});

// 页面挂载时加载公告列表
onMounted(async () => {
  await fetchAnnouncementList();
});

// 核心方法：获取所有公告
const fetchAnnouncementList = async () => {
  try {
    await announcementStore.fetchAllAnnouncements({
      page: 1,
      size: 50, // 每页50条，覆盖大部分场景
      sign: format.generateSign({ timestamp: Date.now() }) // 生成签名
    });
  } catch (error) {
    console.error('获取公告列表失败：', error);
    ElMessage.error('获取公告列表失败，请重试');
  }
};

// 格式化公告日期
const formatAnnounceDate = (time) => {
  if (!time) return '未知时间';
  const date = time.split(' ')[0];
  return `[${date}]`;
};

// 切换公告展开/收起状态
const toggleExpand = (announcementId) => {
  const targetItem = announcementList.value.find(item => item.announcement_id === announcementId);
  if (targetItem) {
    targetItem.isExpanded = !targetItem.isExpanded;
  }
};

// ========== 新增公告逻辑 ==========
const openAddModal = () => {
  isEdit.value = false;
  formData.value = {
    title: '',
    content: '',
    publisher_id: userStore.userInfo.user_id || ''
  };
  showModal.value = true;
};

const confirmAdd = async () => {
  const { title, content, publisher_id } = formData.value;
  // 表单校验
  if (!title.trim()) return ElMessage.warning('请输入公告标题');
  if (!content.trim()) return ElMessage.warning('请输入公告正文');
  if (!publisher_id) return ElMessage.warning('发布者ID不能为空，请先登录');

  try {
    const res = await publishAnnouncement({
      title: title.trim(),
      content: content.trim(),
      publisher_id
    });
    // 适配接口返回格式
    if (res.res_code === '0000') {
      ElMessage.success('公告发布成功');
      await fetchAnnouncementList(); // 重新加载列表
      closeModal();
    } else {
      ElMessage.error(res.res_msg || '发布公告失败');
    }
  } catch (error) {
    console.error('发布公告失败：', error);
    ElMessage.error('发布公告失败，请重试');
  }
};

// ========== 编辑公告逻辑 ==========
const handleEdit = (item) => {
  isEdit.value = true;
  currentEditId.value = item.announcement_id;
  formData.value = {
    title: item.title,
    content: item.content,
    publisher_id: userStore.userInfo.user_id || ''
  };
  showModal.value = true;
};

const confirmEdit = async () => {
  const { title, content, publisher_id } = formData.value;
  // 表单校验
  if (!title.trim() || !content.trim()) return ElMessage.warning('标题和正文不能为空');
  if (!currentEditId.value) return ElMessage.warning('未选择要编辑的公告');

  try {
    const res = await updateAnnouncement(currentEditId.value, {
      title: title.trim(),
      content: content.trim(),
      publisher_id
    });
    if (res.res_code === '0000') {
      ElMessage.success('公告修改成功');
      await fetchAnnouncementList();
      closeModal();
    } else {
      ElMessage.error(res.res_msg || '修改公告失败');
    }
  } catch (error) {
    console.error('修改公告失败：', error);
    ElMessage.error('修改公告失败，请重试');
  }
};

// ========== 删除公告逻辑 ==========
const handleDelete = async (announcementId) => {
  if (!confirm('确定要删除这条公告吗？')) return;

  try {
    const res = await deleteAnnouncement(announcementId);
    if (res.res_code === '0000') {
      ElMessage.success('公告删除成功');
      await fetchAnnouncementList();
    } else {
      ElMessage.error(res.res_msg || '删除公告失败');
    }
  } catch (error) {
    console.error('删除公告失败：', error);
    ElMessage.error('删除公告失败，请重试');
  }
};

// ========== 弹窗控制 ==========
const closeModal = () => {
  showModal.value = false;
  formData.value = { title: '', content: '', publisher_id: userStore.userInfo.user_id || '' };
  currentEditId.value = '';
};
</script>
<style scoped>
/* 添加公告按钮容器 */
.add-btn-wrapper {
  text-align: right;
  margin-bottom: 20px;
}

.add-announcement-btn {
  padding: 8px 20px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-announcement-btn:hover {
  background-color: #8a7ba0;
}

/* 公告列表容器 */
.announcement-list {
  width: 100%;
}

/* 单个公告项 */
.announcement-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding: 15px;
  background-color: #E2DFE7;
  border-radius: 0;
  cursor: pointer;
}

/* 公告左侧竖线 */
.item-line {
  width: 4px;
  height: 100%;
  background-color: #C1BEC7;
  margin-right: 15px;
}

/* 公告内容区域 */
.item-content {
  flex: 1;
}

/* 日期样式 */
.item-date {
  color: #555;
  font-weight: 500;
  margin-bottom: 8px;
  font-size: 16px;
}

/* 标题样式 */
.item-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

/* 正文样式 */
.item-body {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  white-space: pre-line;
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
  align-items: center;
  justify-content: center;
  z-index: 999;
}

/* 弹窗内容 */
.modal-content {
  width: 500px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
  text-align: center;
}

/* 表单项 */
.form-item {
  margin-bottom: 15px;
}

.form-item label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-size: 14px;
}

.input-control {
  width: 100%;
  height: 36px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.textarea-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.6;
  box-sizing: border-box;
  resize: vertical;
}

/* 弹窗按钮 */
.modal-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.confirm-btn {
  background-color: #9b8eb4;
  color: #fff;
}

.confirm-btn:hover {
  background-color: #8a7ba0;
}

.cancel-btn {
  background-color: #eee;
  color: #666;
}

.cancel-btn:hover {
  background-color: #ddd;
}
</style>