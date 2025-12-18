<script setup >

</script>

<template>
  <!-- 给根容器设置渐变背景 -->
  <div class="gradient-bg">
<slot/>
  </div>
</template>

<style scoped>
.gradient-bg{
  width: 100vw; /* 宽高根据需求设置，比如占满屏幕 */
  height: 100vh;
  /* 从上到下渐变：起始色（上）→ 结束色（下） */
  background: linear-gradient(to bottom, #A59EB2,#FFFFFF );
  padding: 0; /* 去掉内边距 */
  margin: 0; /* 去掉外边距 */
  overflow: auto; /* 避免内容超出时滚动异常 */
}
</style>