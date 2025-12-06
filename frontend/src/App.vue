<template>
  <!-- 路由出口：所有匹配到的页面组件都会渲染在这里 -->
  <!-- 比如访问/login时，这里渲染Login.vue；访问/home时渲染Layout.vue -->
  <router-view />
</template>

<script setup>
// 根组件仅作为路由容器，无业务逻辑
</script>

<style scoped>
/* 全局样式预留区，暂时为空（后续补样式时添加） */
/* 可设置全局默认样式，比如body的margin、font-family等 */
</style>