<template>
  <div class="navbar">
    <!-- 系统标题 -->
    <div class="system-title">PANN财务管理系统</div>

    <!-- 导航菜单 -->
    <div class="nav-menu">
      <span
          class="nav-item"
          :class="{ active: $route.name === 'Home' }"
          @click="toPage('Home')"
      >
        首页
      </span>
      <span
          class="nav-item"
          :class="{ active: $route.name === 'UserRoyaltyDetail' }"
          @click="toPage('UserRoyaltyDetail')"
      >
        稿费明细
      </span>
      <span
          class="nav-item"
          :class="{ active: $route.name === 'UserAccount' }"
          @click="toPage('UserAccount')"
      >
        账户页面
      </span>
      <!-- 同一个“管理中心”按钮，根据角色动态跳转 -->
      <span
          v-if="['editor_admin','news_admin','operation_admin','super_admin'].includes(userRole)"
          class="nav-item"
          :class="{ active: isCurrentAdminPage }"
          @click="goAdminCenter"
      >
        管理中心
      </span>
      <span
          class="nav-item"
          :class="{ active: isCurrentAnnouncementPage }"
          @click="goAnnouncement"
      >
        公告
      </span>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted, computed } from 'vue' // 补充导入computed

const router = useRouter()
const route = useRoute() // 获取当前路由实例
// 定义角色变量，默认空字符串
const userRole = ref('')

// 页面挂载时读取本地存储的角色
onMounted(() => {
  userRole.value = localStorage.getItem('pann_user_role') || ''
})

// 普通页面跳转方法
const toPage = (routeName) => {
  router.push({ name: routeName })
}

// 管理中心动态跳转方法
const goAdminCenter = () => {
  console.log('当前用户角色：', userRole.value);
  console.log('角色路由映射：', {
    editor_admin: 'EditorCenter',
    news_admin: 'NewsCenter',
    operation_admin: 'OperationCenter',
    super_admin: 'SystemManage'
  });

  const roleToRoute = {
    editor_admin: 'EditorCenter',
    news_admin: 'NewsCenter',
    operation_admin: 'OperationCenter',
    super_admin: 'SystemManage'
  }
  const targetRoute = roleToRoute[userRole.value]
  console.log('最终跳转的路由name：', targetRoute);
  if (targetRoute) {
    router.push({ name: targetRoute })
  } else {
    router.push({ name: 'Home' })
    alert('暂无对应管理页面权限！')
  }
}

// 新增：公告动态跳转方法
const goAnnouncement = () => {
  console.log('当前用户角色（公告跳转）：', userRole.value);
  // 公告的角色-路由映射：管理员角色跳AdminAnnouncemnet，普通用户跳UserAnnouncement
  const announcementRoleToRoute = {
    editor_admin: 'AdminAnnouncement',
    news_admin: 'AdminAnnouncement',
    operation_admin: 'AdminAnnouncement',
    super_admin: 'AdminAnnouncement', // 超级管理员也跳管理员公告页（可根据需求调整）
    user: 'UserAnnouncement' // 普通用户角色
  }
  // 获取目标路由，兜底为普通用户公告页
  const targetRoute = announcementRoleToRoute[userRole.value] || 'UserAnnouncement'
  console.log('公告跳转的路由name：', targetRoute);
  router.push({ name: targetRoute })
}

// 动态判断管理中心按钮是否高亮
const isCurrentAdminPage = computed(() => {
  const roleToRoute = {
    editor_admin: 'EditorCenter',
    news_admin: 'NewsCenter',
    operation_admin: 'OperationCenter',
    super_admin: 'SystemManage'
  }
  return route.name === roleToRoute[userRole.value]
})

// 新增：动态判断公告按钮是否高亮
const isCurrentAnnouncementPage = computed(() => {
  const announcementRoleToRoute = {
    editor_admin: 'AdminAnnouncement',
    news_admin: 'AdminAnnouncement',
    operation_admin: 'AdminAnnouncement',
    super_admin: 'AdminAnnouncement',
    user: 'UserAnnouncement'
  }
  // 当前路由是否匹配角色对应的公告路由
  return route.name === announcementRoleToRoute[userRole.value] || (
      !announcementRoleToRoute[userRole.value] && route.name === 'UserAnnouncement'
  )
})
</script>

<style scoped>
/* 核心：导航栏完全透明，去掉所有独立容器样式 */
.navbar {
  height: 70px;
  padding: 0 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: transparent; /* 完全透明，和背景融合 */
  border: none; /* 去掉边框 */
  box-shadow: none; /* 去掉阴影（图片中无阴影） */
  width: 100%;
  /* 新增：让导航栏和背景容器的内边距对齐，避免错位 */
  box-sizing: border-box;
}

/* 系统标题：颜色和背景渐变的深紫色匹配 */
.system-title {
  color: #3D3843; /* 图片中的标题深紫色 */
  font-size: 35px;
  font-weight: 500;
}

.nav-menu {
  display: flex;
  gap: 30px;
  background: transparent; /* 菜单容器也透明 */
}

/* 导航项：文字颜色用白色，和背景渐变的浅紫色形成柔和对比 */
.nav-item {
  font-size: 25px;
  cursor: pointer;
  transition: color 0.3s;
  color: #fff; /* 白色文字，和背景融合 */
}

/* hover效果：文字变深紫色，和标题呼应，不突兀 */
.nav-item:hover {
  text-decoration: underline;
  color: #3D3843;
}

/* 激活样式：和标题同色，突出当前页 */
.nav-item.active {
  color: #3D3843;
  font-weight: 500;
}
</style>