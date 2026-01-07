<template>
  <AnnounceBackground>
    <Navbar style="background-color:transparent!important;" />
    <!-- 1. 页面标题：匹配AnnounceBackground的page-title插槽 -->
    <template #page-title>所有公告</template>

    <!-- 2. 公告内容：必须放入AnnounceBackground的card-content插槽 -->
    <template #card-content>
      <!-- 公告页面内容区 -->
      <div class="announcement-container">
        <!-- 公告列表 -->
        <div class="announcement-list">
          <!-- 无数据提示 -->
          <div v-if="loading" class="empty-tip">加载中...</div>
          <div v-else-if="announcementList.length === 0" class="empty-tip">
            暂无公告
          </div>
          <!-- 单个公告项（循环渲染，含展开/收起逻辑） -->
          <div
              class="announcement-item"
              v-for="(item, index) in announcementList"
              :key="item.announcement_id"
              @click="toggleExpand(index)"
          >
            <!-- 公告日期 -->
            <div class="announcement-date">[{{ formatDate(item.published_at) }}]</div>

            <!-- 公告内容（展开/收起切换） -->
            <div class="announcement-content">
              <!-- 收起状态：显示标题 -->
              <div v-if="!item.isExpanded" class="content-title">
                {{ item.title }}
              </div>
              <!-- 展开状态：显示完整内容 -->
              <div v-else class="content-expanded">
                <div class="content-title">{{ item.title }}</div>
                <div class="content-body">{{ item.content }}</div>
                <div class="content-footer">发布人ID：{{ item.publisher_id }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AnnounceBackground>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Navbar from "@/components/Navbar.vue";
import AnnounceBackground from "@/components/AnnounceBackground.vue";
import { getAllAnnouncements} from '@/api/announcement';
import { ElMessage } from 'element-plus';
import { getToken } from '@/utils/auth.js';
import {format} from "@/utils/index.js";

const router = useRouter();
const announcementList = ref([]);
const loading = ref(true); // 加载状态
console.log('普通用户')
// 格式化日期（兼容接口返回的时间格式）
const formatDate = (dateStr) => {
  if (!dateStr) return '未知时间';
  // 处理 "2025-11-20 09:00:00" 格式，只保留年月日
  return dateStr.split(' ')[0] || dateStr;
};

// 页面挂载时请求数据
onMounted(async () => {
  try {
    const token = getToken();
    if (!token) {
      ElMessage.warning('请先登录');
     await  router.push('/login');
      return;
    }

    loading.value = true;
    // 调用「获取所有公告」接口，传入分页/排序参数（匹配2.5.31接口）
    const res = await getAllAnnouncements({
      page: 1, // 页码（必选）
      size: 20, // 每页数量（必选，1-50）
      order_by: 'published_at', // 按发布时间排序
      sort: 'desc', // 降序（最新的在前）
      sign: format.generateSign({ timestamp: Date.now() }) // 签名
    });
    loading.value = false;


    // 先判断外层响应是否成功（Axios的res.data才是Mock返回的内容）
    if (res.res_code === '0000') {
      const list = res.data?.list || []; // res.data才是业务数据，里面有list
      if (Array.isArray(list)) {
        // 修复：保留原始announcement_id，无需映射为id（避免字段混乱）
        announcementList.value = list.map(item => ({
          ...item, // 继承所有原始字段
          isExpanded: false // 新增展开状态
        }));
      }
    } else {
      ElMessage.error(res.res_msg || '获取公告失败');
    }

  } catch (error) {
    loading.value = false; // 异常时也要关闭加载状态
    console.error('请求公告失败（网络错误）：', error);
    announcementList.value = [];
    ElMessage.error('获取公告失败，请稍后重试');
  }
  console.log('最终渲染的公告列表：', announcementList.value);
});

// 展开/收起切换（保证响应式）
const toggleExpand = (index) => {
  // 修复：通过临时变量修改，确保响应式生效
  const item = announcementList.value[index];
  announcementList.value[index] = {
    ...item,
    isExpanded: !item.isExpanded
  };
};
</script>

<style scoped>
/* 公告页面容器 */
.announcement-container {
  padding: 30px 60px;
  min-height: calc(100vh - 140px); /* 适配背景高度 */
  margin-left: -150px; /* 左移20px，*/
  margin-top: -60px;
}

/*!* 公告标题 *!
.announcement-title {
  color: #333;
  font-size: 20px;
  margin-bottom: 20px;
  padding: 10px 15px; !* 优化内边距 *!
  border-left: 4px solid #C1BEC7;
  background-color: #E2DFE7;
  border-radius: 0 4px 4px 0; !* 圆角优化 *!
}*/

/* 公告列表 */
.announcement-list {
  width: 800px;
  margin: 0 auto; /* 居中显示 */
}

/* 单个公告项 */
.announcement-item {
  display: flex;
  margin-bottom: 15px;
  padding: 15px 20px;
  background-color: #E2DFE7; /* 提升透明度，更柔和 */
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s; /*  hover过渡 */
  box-shadow: 0 2px 4px rgba(0,0,0,0.05); /* 轻微阴影，提升层次感 */
}

.announcement-item:hover {
  background-color: rgba(255, 255, 255, 0.95);
}

/* 公告日期 */
.announcement-date {
  width: 120px;
  color: #555;
  font-weight: 600;
  margin-right: 20px;
  font-size: 14px;
  line-height: 1.5;
}

/* 公告内容（收起状态） */
.content-collapsed {
  color: #555;
  line-height: 1.5;
  font-size: 15px;
  width: 600px; /* 固定宽度，避免换行 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 标题过长省略 */
}

/* 公告内容（展开状态） */
.content-expanded {
  color: #555;
  line-height: 1.6;
  width: 600px;
}
.content-title {

  font-weight: 600;
  margin-bottom: 8px;
  font-size: 16px;
  color: #333;
}
.content-body {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  white-space: pre-line; /* 保留内容换行 */
}
.content-footer {
  font-size: 12px;
  color: #999;
}

/* 空数据/加载提示 */
.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
}
/* 公告内容容器（核心新增+优化） */
.announcement-content {
  flex: 1; /* 占满剩余宽度，替代固定宽度，适配性更好 */
  margin-left: -10px; /* 左移10px，拉近和日期的距离，视觉更紧凑 */
  padding-top: 2px; /* 垂直对齐日期文字 */
}

/* 公告日期（微调，配合内容左移） */
.announcement-date {
  width: 120px;
  color: #555;
  font-weight: 500;
  margin-right: 15px; /* 原有20px → 15px，配合内容左移，间距更协调 */
  font-size: 14px;
  line-height: 1.5;
  flex-shrink: 0; /* 固定宽度，不被挤压 */
}

/* 公告内容（收起状态）- 适配flex布局，去掉固定宽度 */
.content-collapsed {
  color: #555;
  line-height: 1.5;
  font-size: 15px;
  /* 去掉固定width: 600px，改用flex布局自适应 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 标题过长省略 */
}

/* 公告内容（展开状态）- 适配flex布局，去掉固定宽度 */
.content-expanded {
  color: #555;
  line-height: 1.6;
  /* 去掉固定width: 600px，改用flex布局自适应 */
}
</style>