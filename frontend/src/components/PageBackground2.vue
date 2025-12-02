<template>
  <div class="page-container">
    <Navbar />
    <!-- 标题区域（带紫色标识线） -->
    <div class="title-wrapper">
      <div class="title-line"></div>
      <slot name="page-title">默认标题</slot>
    </div>

    <!-- 白色内容卡片 -->
    <div class="content-card">
      <slot name="card-content"></slot>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  min-height: 100vh; /* 确保占满整个视口高度 */
  background-color: #f0f0f0; /* 浅灰背景 */
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 标题区域样式（带左侧紫色线） */
.title-wrapper {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  margin-left: 150px; /* 与卡片左侧对齐 */
  margin-top: 50px;
  margin-bottom: 20px; /* 与卡片拉开距离 */
  font-size: 24px;
  font-weight: 500;
  color: #333;
}

/* 标题前的紫色标识线 */
.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4; /* 紫色线条 */
  margin-right: 12px; /* 与文字间距 */
}

/* 白色内容卡片样式 */
.content-card {
  background-color: white;
  border-radius: 8px;
  padding: 60px 80px; /* 增加内边距，更宽松 */
  width: 70%; /* 调整宽度更接近设计 */
  min-height: 400px; /* 最小高度，内容多会撑开 */
  margin: 0 auto; /* 水平居中 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); /* 轻微阴影 */
  box-sizing: border-box;
}
</style>

<script setup>
import Navbar from "@/components/Navbar.vue";
</script>