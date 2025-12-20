<template>
  <PageBackground>
    <Navbar style="background-color:transparent!important;" />
    <!-- 首页内容区 -->
    <div class="home-content">
      <!-- 加载状态提示 -->
      <div v-if="isLoading" class="loading-tip">加载中...</div>

      <!-- 非加载状态：统一包裹在v-else中 -->
      <div v-else>
        <!-- 欢迎语：从userStore取用户信息，兜底更严谨 -->
        <h2 class="welcome-text">欢迎，{{ userName }}</h2>

        <!-- 最新公告 -->
        <div class="notice-section">
          <h3 class="notice-title">最新公告</h3>
          <!-- 公告列表：从noticeStore的getter取值 -->
          <ul v-if="latestAnnouncements.length > 0" class="notice-list">
            <li
                v-for="item in latestAnnouncements"
                :key="item.announcement_id"
                class="notice-item"
            >
              [{{ formatDate(item.published_at) }}] {{ item.title }}：{{ item.content }}
            </li>
          </ul>
          <div v-else class="empty-notice">暂无最新公告</div>
        </div>
      </div>
    </div>
  </PageBackground>
</template>

<script setup>
  // 1. 导入核心依赖
  import { ref, onMounted, computed } from 'vue';
  import PageBackground from "@/components/PageBackground.vue";
  import Navbar from "@/components/Navbar.vue";
  // 导入登录态校验工具
  import { storage, format } from '@/utils' // 补充format（生成签名用）
  import { useUserStore } from '@/stores'
  import { useNoticeStore } from '@/stores/notice' // 导入公告Store
  // 2. 定义响应式数据
  const isLoading = ref(true); // 加载状态
  const userStore = useUserStore()
  const noticeStore = useNoticeStore() // 初始化公告Store
  userStore.loadUserProfile();
  // 计算属性：从userStore取用户信息（无需单独定义userInfo）
  const userName = computed(() => {
  return userStore.userInfo.real_name || '未知用户';
});

  // 计算属性：从noticeStore的getter取最新3条公告（响应式）
  const latestAnnouncements = computed(() => {
    return noticeStore.latestAnnouncements(3); // 这里依赖noticeStore的getter定义正确
  });

  // 工具函数：日期格式化（YYYY-MM-DD）
  const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

  // 4. 核心函数：初始化页面数据
  const initPageData = async () => {
    try {
      // 校验登录态（无需改）
      const token = storage.getToken();
      if (!token) {
        window.location.href = '/login';
        return;
      }

  // 生成接口签名（按接口文档2.2节规则）
  const sign = format.generateSign({ timestamp: Date.now() });

  // 并行请求：用户信息 + 公告列表（提升加载效率）
      const [userRes] = await Promise.all([
        userStore.loadUserProfile({ sign }),
        noticeStore.fetchAllAnnouncements({
          sign: sign, // 直接传你生成的sign
          pageNum: 1,
          pageSize: 10
        })
      ]);

  // 赋值用户信息到userStore（对齐你现有store逻辑）
  if (userRes.res_code === '0000') { // 改为接口文档的成功码
    userStore.setUserInfo(userRes.data);// 假设userStore有setUserInfo方法
    userStore.setIsSuperAdmin(!!userRes.data.is_super_admin);
    userStore.setDeptName((userRes.data.department_name || '').trim());
    userStore.setIsAdmin(!!userRes.data.admin_id);
}
} catch (error) {
  // 异常处理：打印错误+友好提示
  console.error('首页数据请求失败：', error);
  alert('数据加载失败，请刷新页面重试');
} finally {
  // 无论成功/失败，结束加载状态
  isLoading.value = false;
}
};

  // 5. 页面挂载时初始化数据
  onMounted(async () => {
  await initPageData(); // 统一调用初始化函数
});
</script>

<style>
/* 首页内容区样式 */
.home-content {
  padding: 20px 60px;
  margin-top: 0;
  position: relative; /* 为加载提示定位 */
}

/* 加载提示样式 */
.loading-tip {
  text-align: center;
  font-size: 20px;
  color: #70687D;
  padding: 50px 0;
}

/* 欢迎语样式 */
.welcome-text {
  color: #70687D;
  font-size: 55px;
  margin-top: -20px;
  font-weight: 500;
  text-align: center;
}

/* 公告区域样式 */
.notice-section {
  width: 900px;
  margin-top: 40px;
}

.notice-title {
  color: #333;
  font-size: 30px;
  font-weight: 400;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #A59EB2;
}

/* 公告列表样式 */
.notice-list {
  list-style: none;
  padding: 0;
  width: 90vh;
}

.notice-item {
  padding: 15px;
  margin-bottom: 10px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 4px;
  color: #555;
  font-size: 16px;
  width: 1000px;
}

/* 空公告提示样式 */
.empty-notice {
  color: #999;
  font-size: 16px;
  padding: 20px;
  text-align: center;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 4px;
}
</style>