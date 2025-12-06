<template>
  <PageBackground>
    <!-- 导航栏（复用之前的Navbar组件） -->
    <Navbar />

    <!-- 首页内容区 -->
    <div class="home-content">
      <!-- 欢迎语 -->
      <h2 class="welcome-text">欢迎，XXX</h2>

      <!-- 最新公告 -->
      <div class="notice-section">
        <h3 class="notice-title">最新公告</h3>
        <ul class="notice-list">
          <li class="notice-item">[2025-10-31] 阿巴阿巴阿巴阿巴......</li>
          <li class="notice-item">[2025-11-01] 阿巴阿巴阿巴阿巴......</li>
          <li class="notice-item">[2025-11-11] 阿巴阿巴阿巴阿巴......</li>
        </ul>
      </div>
    </div>
  </PageBackground>
</template>

<script setup lang="ts">
// 导入需要的组件
import PageBackground from "@/components/PageBackground.vue";
import Navbar from "@/components/Navbar.vue";
</script>

<style scoped>
/* 首页内容区样式 */
.home-content {
  padding: 40px 60px; /* 整体内边距 */
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
  width: 900px; /* 限制公告区宽度 */
}

.notice-title {
  color: #333;
  font-size: 30px;
  font-weight: 400;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #A59EB2; /* 左侧紫色标识线 */
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
  background-color: rgba(255, 255, 255, 0.7); /* 半透明白色背景 */
  border-radius: 4px;
  color: #555;
  font-size: 16px;
  width: 1000px;
}
</style>