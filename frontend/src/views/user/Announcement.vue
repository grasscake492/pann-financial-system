<template>
  <AnnounceBackground>
    <template  #page-title>所有公告</template>
    <!-- 复用通用导航栏 -->
    <Navbar />

    <!-- 公告页面内容区 -->
    <div class="announcement-container">
      <h2 class="announcement-title">所有公告</h2>

      <!-- 公告列表 -->
      <div class="announcement-list">
        <!-- 无数据提示 -->
        <div v-if="announcementList.length === 0" class="empty-tip">
          暂无公告
        </div>
        <!-- 单个公告项（循环渲染，含展开/收起逻辑） -->
        <div
            class="announcement-item"
            v-for="(item, index) in announcementList"
            :key="index"
            @click="toggleExpand(index)"
        >
          <!-- 公告日期 -->
          <div class="announcement-date">[{{ item.date }}]</div>

          <!-- 公告内容（展开/收起切换） -->
          <div class="announcement-content">
            <!-- 收起状态：显示标题 -->
            <div v-if="!item.isExpanded" class="content-collapsed">
              {{ item.title }}
            </div>
            <!-- 展开状态：显示完整内容 -->
            <div v-else class="content-expanded">
              <div class="content-title">{{ item.title }}</div>
              <div class="content-body">{{ item.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AnnounceBackground>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Navbar from "@/components/Navbar.vue";
import AnnounceBackground from "@/components/AnnounceBackground.vue";
import { getAnnouncementDetail, getAnnouncementList } from '@/api/announcement';
import { ElMessage } from 'element-plus';
import { getToken } from '@/utils/auth.js';
const announcementList = ref([]);

// 页面挂载时请求数据
onMounted(async () => {
  try {
    // 1. 检查令牌（复用你已有的getToken方法）

    const token = getToken();
    if (!token) {
      ElMessage.warning('请先登录');
      // 跳登录页（需适配你的路由）
      // router.push('/login');
      return;
    }

    // 2. 请求公告列表
    const res = await getAnnouncementList();
    // 适配公告接口返回格式：res.data是解密后的公告列表
    if (res.data && Array.isArray(res.data)) {
      announcementList.value = res.data.map(item => ({
        id: item.announcement_id,
        date: item.published_at?.split(' ')[0] || '', // 兼容字段不存在的情况
        title: item.title || '',
        content: item.content || '',
        isExpanded: false
      }));
    }

    // 若只有单条详情接口，用下面的方式（示例查ID=1）
    // const res = await getAnnouncementDetail(1);
    // if (res.data?.announcementInfo) {
    //   announcementList.value = [{
    //     id: res.data.announcementInfo.announcement_id,
    //     date: res.data.announcementInfo.published_at?.split(' ')[0] || '',
    //     title: res.data.announcementInfo.title || '',
    //     content: res.data.announcementInfo.content || '',
    //     isExpanded: false
    //   }];
    // }

  } catch (error) {
    console.error('请求公告失败：', error);
    announcementList.value = [];
    ElMessage.error('获取公告失败，请稍后重试');
  }
});

const toggleExpand = (index) => {
  announcementList.value[index].isExpanded = !announcementList.value[index].isExpanded;
};
</script>

<style scoped>
/* 公告页面容器 */
.announcement-container {
  padding: 30px 60px;
}

/* 公告标题 */
.announcement-title {
  color: #333;
  font-size: 20px;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #C1BEC7;
  background-color: #E2DFE7;
}

/* 公告列表 */
.announcement-list {
  width: 800px;
}

/* 单个公告项 */
.announcement-item {
  display: flex;
  margin-bottom: 15px;
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 4px;
  cursor: pointer;
}

/* 公告日期 */
.announcement-date {
  width: 120px;
  color: #555;
  font-weight: 500;
  margin-right: 20px;
}

/* 公告内容（收起状态） */
.content-collapsed {
  color: #555;
  line-height: 1.5;
}

/* 公告内容（展开状态） */
.content-expanded {
  color: #555;
  line-height: 1.6;
}
.content-title {
  font-weight: 500;
  margin-bottom: 8px;
}
.content-body {
  font-size: 14px;
  color: #666;
}
.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
}
</style>