<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(async () => {
  console.log('App组件初始化...')

  // 检查是否有token，如果有则验证
  const token = localStorage.getItem('pann_token')
  if (token && !userStore.authChecked) {
    console.log('发现token，开始验证...')
    await userStore.initAndValidateUser()
  } else {
    // 没有token，标记为已验证
    userStore.authChecked = true
  }
})
</script>

<style scoped>
/* 全局样式预留区 */
</style>