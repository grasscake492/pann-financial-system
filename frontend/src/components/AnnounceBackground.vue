<template>
  <div class="page-container">
    <!-- 导航栏保持在顶部 -->
    <Navbar />

    <!-- 标题区域：调整间距，避免被导航栏遮挡 -->
    <div class="title-wrapper">
      <div class="title-line"></div>
      <slot name="page-title">默认标题</slot>
    </div>

    <!-- 内容卡片：移除多余嵌套，直接承载内容 -->
    <div class="content-card">
      <!-- 白色内容容器：确保z-index正常，不被遮挡 -->
      <slot name="card-content"></slot>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  min-height: 100vh;
  /* 页面背景 */
  background: linear-gradient(to bottom,#BEBCC5,#FFFFFF );
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  /* 新增：确保内容在导航栏下方 */
  padding-top: 0px; /* 与Navbar的height一致 */
}

/* 标题区域：调整位置，避免与导航栏重叠 */
.title-wrapper {
  display: flex;
  align-items: center;
  margin-left: 150px;
  margin-top: 20px; /* 减少顶部间距 */
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 500;
  color: #333;
  position: relative;
  z-index: 1; /* 确保标题在最上层 */
}

.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4;
  margin-right: 12px;
}

/* 内容卡片：直接设置白色背景，去掉多余嵌套 */
.content-card {
  width: 70%;
  margin: 0 auto;
  background-color: transparent;
  border-radius: 8px;
  padding: 60px 80px;
  min-height: 400px;
  box-sizing: border-box;
  position: relative;
  z-index: 1; /* 确保内容在最上层 */
}
</style>

<script setup>
import Navbar from "@/components/Navbar.vue";
</script>