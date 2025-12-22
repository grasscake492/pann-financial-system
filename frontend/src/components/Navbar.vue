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
      <!-- 管理中心按钮：直接判断超级管理员/部门管理员 -->
      <span
          v-if="isShowAdminCenter"
          class="nav-item"
          :class="{ active: isCurrentAdminPage }"
          @click="goAdminCenter"
      >管理中心
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
import { computed, onMounted, watch, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()


// 修复：管理中心显示条件（超级管理员直接显示，部门管理员需有部门ID和admin_id）
const isShowAdminCenter = computed(() => {
  // 超级管理员无条件显示
  const isSuperAdmin = userStore.userInfo.is_super_admin === true;
  // 部门管理员：有admin_id + 有department_id
  const isDeptAdmin = !!userStore.userInfo.admin_id && !!userStore.userInfo.department_id;
  // 打印判断结果（调试用）
  return isSuperAdmin || isDeptAdmin;
})

// 修复：部门ID映射（匹配实际返回的"1"/"2"等ID）
const deptIdToRoute = {
  '1': 'EditorCenter',    // 编辑部ID=1
  '2': 'NewsCenter',      // 新闻部ID=2
  '3': 'OperationCenter'  // 运营部ID=3
}

// 管理中心高亮判断
const isCurrentAdminPage = computed(() => {
  if (!isShowAdminCenter.value) return false;

  let targetRoute = ''
  // 超级管理员→系统管理页
  if (userStore.userInfo.is_super_admin) {
    targetRoute = 'SystemManage'
  }
  // 部门管理员→根据实际department_id匹配
  else if (userStore.userInfo.admin_id && userStore.userInfo.department_id) {
    targetRoute = deptIdToRoute[userStore.userInfo.department_id] || ''
  }

  return route.name === targetRoute
})

// 公告页面高亮判断
const isCurrentAnnouncementPage = computed(() => {
  const isAdmin = userStore.userInfo.is_super_admin || !!userStore.userInfo.admin_id;
  const targetRoute = isAdmin ? 'AdminAnnouncement' : 'UserAnnouncement';
  return route.name === targetRoute;
})

// 普通页面跳转
const toPage = (routeName) => {
  router.push({ name: routeName })
}

// 管理中心跳转（修复部门ID映射）
const goAdminCenter = () => {
  let targetRoute = ''

  // 超级管理员→系统管理
  if (userStore.userInfo.is_super_admin) {
    targetRoute = 'SystemManage';
  }
  // 部门管理员→匹配实际department_id
  else if (userStore.userInfo.admin_id && userStore.userInfo.department_id) {
    targetRoute = deptIdToRoute[userStore.userInfo.department_id] || '';
  }

  if (targetRoute) {
    const routeExists = router.getRoutes().some(item => item.name === targetRoute);
    if (routeExists) {
      router.push({ name: targetRoute });
    } else {
      alert(`暂无【${userStore.userInfo.department_name}】对应的管理页面！`);
    }
  } else {
    alert('暂无管理页面权限！');
    router.push({ name: 'Home' });
  }
}

// 公告页面跳转
const goAnnouncement = () => {
  console.log('当前用户:', {
    is_super_admin: userStore.userInfo.is_super_admin,
    admin_id: userStore.userInfo.admin_id,
    dept_id: userStore.userInfo.department_id
  })
  const isAdmin = userStore.userInfo.is_super_admin || !!userStore.userInfo.admin_id;
  console.log('是否是管理员:', isAdmin)
  const targetRoute = isAdmin ? 'AdminAnnouncement' : 'UserAnnouncement';
  console.log('目标路由:', targetRoute)
  const routeExists = router.getRoutes().some(item => item.name === targetRoute);
  if (routeExists) {
    router.push({ name: targetRoute });
  } else {
    alert(`路由未注册：${targetRoute}，请检查路由表！`);
    router.push({ name: 'Home' });
  }
}

// 修复：监听用户信息变化（深度监听+立即执行）
watch([() => route.name, () => userStore.userInfo], () => {
  // 强制更新DOM（解决响应式更新延迟）
  nextTick(() => {});
}, {
  immediate: true,
  deep: true
})
/*
// 修复：延迟执行loadUserProfile，避免覆盖初始数据
const fetchUserProfileSafely = async () => {
  // 等待DOM更新后再拉取，避免覆盖登录后的初始数据
  await nextTick();
  try {
    const res = await userStore.loadUserProfile();
    // 拉取后重新打印用户信息（检查是否被覆盖）
    console.log('【拉取用户信息后】', userStore.userInfo);
  } catch (e) {
    console.error('拉取用户信息失败：', e);
  }
}*/

// 挂载时执行
onMounted(() => {
  // 无Token跳登录
  if (!userStore.userInfo.token) {
    router.push({ path: '/login' });
    return;
  }
  /*// 延迟拉取用户信息，避免覆盖初始数据
 fetchUserProfileSafely();*/
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
  box-sizing: border-box; /* 让内边距计入宽度，避免错位 */
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