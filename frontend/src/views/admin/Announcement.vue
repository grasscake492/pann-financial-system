<template>
  <AnnounceBackground>
    <!-- 页面标题插槽 -->
    <template v-slot:page-title>所有公告</template>

    <!-- 内容卡片插槽：新增添加公告按钮 + 普通用户/编辑态适配 -->
    <template v-slot:card-content>
      <!-- 公告列表 -->
      <div class="announcement-list">
        <!-- 单个公告项 -->
        <div
            class="announcement-item"
            v-for="(item, index) in sortedAnnouncementList"
            :key="index"
            @click="toggleExpand(index)"
        >
          <!-- 公告左侧竖线 -->
          <div class="item-line"></div>

          <!-- 公告内容区域 -->
          <div class="item-content">
            <!-- 日期 -->
            <div class="item-date">{{ item.date }}</div>

            <!-- 标题（展开/收起） -->
            <div class="item-title">{{ item.title }}</div>

            <!-- 正文（仅展开时显示） -->
            <div v-if="item.isExpanded" class="item-body">
              {{ item.content }}
            </div>
          </div>
        </div>
      </div>
      <!-- 添加公告按钮 -->
      <div class="add-btn-wrapper">
        <button class="add-announcement-btn" @click="showAddModal = true">
          添加公告
        </button>
      </div>
      <!-- 添加公告弹窗 -->
      <div class="modal-mask" v-if="showAddModal">
        <div class="modal-content">
          <div class="modal-title">新增公告</div>
          <!-- 标题输入 -->
          <div class="form-item">
            <label>标题：</label>
            <input
                v-model="newAnnouncement.title"
                class="input-control"
                placeholder="请输入公告标题"
            >
          </div>
          <!-- 正文输入 -->
          <div class="form-item">
            <label>正文：</label>
            <textarea
                v-model="newAnnouncement.content"
                class="textarea-control"
                placeholder="请输入公告正文"
                rows="6"
            ></textarea>
          </div>
          <!-- 操作按钮 -->
          <div class="modal-btns">
            <button class="btn cancel-btn" @click="cancelAdd">取消</button>
            <button class="btn confirm-btn" @click="confirmAdd">确定添加</button>
          </div>
        </div>
      </div>
    </template>
  </AnnounceBackground>
</template>

<script setup>
import { ref, computed } from 'vue';
import AnnounceBackground from "@/components/AnnounceBackground.vue";

// 公告原始数据
const announcementList = ref([


]);

// 按时间倒序排序（提取日期字符串去掉[]后比较）
const sortedAnnouncementList = computed(() => {
  return [...announcementList.value].sort((a, b) => {
    // 提取纯日期字符串（去掉[]）
    const dateA = a.date.replace(/\[|\]/g, '');
    const dateB = b.date.replace(/\[|\]/g, '');
    // 倒序排列（新日期在前）
    return dateB.localeCompare(dateA);
  });
});

// 切换公告展开/收起
const toggleExpand = (index) => {
  // 注意：操作排序后的列表要映射回原始列表
  const targetItem = sortedAnnouncementList.value[index];
  const originalIndex = announcementList.value.findIndex(item => item.date === targetItem.date && item.title === targetItem.title);
  if (originalIndex !== -1) {
    announcementList.value[originalIndex].isExpanded = !announcementList.value[originalIndex].isExpanded;
  }
};

// 新增公告相关
const showAddModal = ref(false); // 新增弹窗显隐
const newAnnouncement = ref({
  title: '',
  content: ''
});

// 格式化当前日期为 [YYYY-MM-DD] 格式
const getCurrentDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  return `[${year}-${month}-${day}]`;
};

// 确认添加公告
const confirmAdd = () => {
  if (!newAnnouncement.value.title.trim()) {
    alert('请输入公告标题！');
    return;
  }
  // 构造新公告对象
  const newItem = {
    date: getCurrentDate(),
    title: newAnnouncement.value.title.trim(),
    content: newAnnouncement.value.content.trim() || '无正文',
    isExpanded: true // 新增公告默认展开
  };
  // 添加到原始列表（排序由计算属性自动处理）
  announcementList.value.push(newItem);
  // 重置表单+关闭弹窗
  newAnnouncement.value = { title: '', content: '' };
  showAddModal.value = false;
};

// 取消添加公告
const cancelAdd = () => {
  // 重置表单+关闭弹窗
  newAnnouncement.value = { title: '', content: '' };
  showAddModal.value = false;
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