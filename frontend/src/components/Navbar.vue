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
      <!-- 管理中心按钮：基于Store的getters判断显示 -->
      <span
          v-if="userStore?.isSuperAdmin || (userStore?.isDeptAdmin && userStore?.userInfo.department_name)"
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
import {computed, onMounted, watch} from 'vue'
// 导入Pinia的userStore
import { useUserStore } from '@/stores/user'
const router = useRouter()
const route = useRoute()
// 初始化userStore
const userStore = useUserStore()

// 移除原本地存储的userRole/userDeptName，改为从Store读取
// 管理中心按钮高亮判断：基于Store的角色和部门
const isCurrentAdminPage = computed(() => {
  if (!userStore) return false; // 兜底Store未初始化
  const deptToRoute = {
    '编辑部': 'EditorCenter',
    '新闻部': 'NewsCenter',
    '运营部': 'OperationCenter'
  }
  let targetRoute = ''
  if (userStore.isSuperAdmin) {
    targetRoute = 'SystemManage'
  } else if (userStore.isDeptAdmin) {
    const deptName = (userStore.userInfo.department_name || '').trim()
    targetRoute = deptToRoute[deptName]
  }
  return route.name === targetRoute
})

// 公告按钮高亮判断：基于Store的角色
const isCurrentAnnouncementPage = computed(() => {
  if (!userStore) return false; // 兜底Store未初始化
  const targetRoute = userStore.isSuperAdmin || userStore.isDeptAdmin
      ? 'AdminAnnouncement'
      : 'UserAnnouncement'
  return route.name === targetRoute
})

// 普通页面跳转方法（保留）
const toPage = (routeName) => {
  if (!userStore) return; // 兜底
  router.push({ name: routeName })
}

// 管理中心动态跳转方法：基于Store数据
const goAdminCenter = () => {
  if (!userStore) return; // 兜底
  console.log('当前角色：', userStore.userRole);
  console.log('当前部门：', userStore.userInfo.department_name);
  const deptToRoute = {
    '编辑部': 'EditorCenter',
    '新闻部': 'NewsCenter',
    '运营部': 'OperationCenter'
  }
  let targetRoute = '';
  if (userStore.isSuperAdmin) {
    targetRoute = 'SystemManage'; // 超级管理员→系统管理
  } else if (userStore.isDeptAdmin) {
    const deptName = userStore.userInfo.department_name.trim();
    targetRoute = deptToRoute[deptName]; // 按部门跳对应页面
  }

  if (targetRoute) {
    const routeExists = router.getRoutes().some(item => item.name === targetRoute);
    if (routeExists) {
      router.push({ name: targetRoute });
    } else {
      alert(`暂无【${deptName}】对应的管理页面！`);
    }
  } else {
    alert('暂无管理页面权限！');
    router.push({ name: 'Home' });
  }
};

// 公告动态跳转方法：基于Store角色
const goAnnouncement = () => {
  if (!userStore) return; // 兜底
  console.log('【公告跳转】当前用户角色：', userStore.userRole);
  const isAdmin = userStore.isSuperAdmin || userStore.isDeptAdmin
  const targetRoute = isAdmin ? 'AdminAnnouncement' : 'UserAnnouncement'

  console.log('【公告跳转】目标路由：', targetRoute);
  const routeExists = router.getRoutes().some(item => item.name === targetRoute)
  if (routeExists) {
    router.push({ name: targetRoute })
  } else {
    alert(`路由未注册：${targetRoute}，请检查路由表！`)
    router.push({ name: 'Home' })
  }
}

// 监听路由变化+Store数据变化，确保按钮状态实时更新
watch([() => route.name, () => userStore?.userRole, () => userStore?.deptName], () => {
  console.log('【Navbar】路由/用户信息变化，更新按钮状态');
}, { immediate: false })

// 页面加载时主动拉取用户信息（确保Store有数据）
const fetchUserProfileSafely = async () => {
  try {
    await userStore.loadUserProfile();
  } catch (e) {
    console.error('拉取用户信息失败：', e);
  }
};
fetchUserProfileSafely();
userStore.loadUserProfile()

onMounted(() => {
  if (!userStore.userInfo.token) {
    router.push({ path: '/login' }); // 无Token则跳登录页
    console.log('【用户Store数据】', userStore.userInfo);
    console.log('【是否新闻部管理员】', userStore.isNewsAdmin);
    console.log('【是否部门管理员】', userStore.isDeptAdmin);
    console.log('【部门名称】', userStore.userInfo.department_name);
  }
});
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