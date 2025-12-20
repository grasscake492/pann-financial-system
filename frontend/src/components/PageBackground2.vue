<template>
  <div class="page-container">
    <Navbar />
    <!-- 标题区域：和内容卡片统一对齐 -->
    <div class="title-wrapper">

      <slot name="page-title"></slot>
    </div>

    <!-- 内容容器：包裹所有内容，避免被Navbar遮挡 -->
    <div class="content-wrapper">
      <slot name="card-content"></slot>
    </div>
  </div>
</template>

<style scoped>
/* 页面容器：重置布局，避免溢出 */
.page-container {
  min-height: 100vh;
  background-color: #f0f0f0;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  padding-top: 0px; /* 给Navbar留空间（Navbar高度一般是60-80px） */
}

/* 标题区域：和内容卡片左对齐 */
.title-wrapper {
  display: flex;
  align-items: center;
  margin-left: 15%; /* 匹配内容卡片的左侧间距（内容宽度70%，居中后左右各15%） */
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 500;
  color: #333;
}

.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4;
  margin-right: 12px;
}

/* 内容容器：替代原content-card，统一控制内容区域 */
.content-wrapper {
  width: 80%;
  margin: 0 auto;
  background-color: white;
  border-radius: 8px;
  padding: 30px 40px;
  min-height: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}
</style>

<script setup>
import Navbar from "@/components/Navbar.vue";
</script>