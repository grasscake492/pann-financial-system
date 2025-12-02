<template>
  <div class="navbar">
    <!-- 系统标题 -->
    <div class="system-title">PANN财务管理系统</div>

    <!-- 导航菜单：绑定路由跳转 + 激活判断 -->
    <div class="nav-menu">
      <!-- 首页 -->
      <span
          class="nav-item"
          :class="{ active: $route.name === 'Home' }"
          @click="toPage('Home')"
      >
        首页
      </span>
      <!-- 稿费明细（对应 user/RoyaltyDetail.vue） -->
      <span
          class="nav-item"
          :class="{ active: $route.name === 'UserRoyaltyDetail' }"
          @click="toPage('UserRoyaltyDetail')"
      >
        稿费明细
      </span>
      <!-- 账户页面（对应 user/Account.vue） -->
      <span
          class="nav-item"
          :class="{ active: $route.name === 'UserAccount' }"
          @click="toPage('UserAccount')"
      >
        账户页面
      </span>
      <!-- 公告（对应 user/Announcement.vue） -->
      <span
          class="nav-item"
          :class="{ active: $route.name === 'UserAnnouncement' }"
          @click="toPage('UserAnnouncement')"
      >
        公告
      </span>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router' // 引入路由实例

const router = useRouter()

// 路由跳转方法
const toPage = (routeName) => {
  // 按路由名称跳转（匹配路由配置中的 name 字段）
  router.push({ name: routeName })
}
</script>

<style scoped>
.navbar {
  height: 70px;
  padding: 0 60px;
  background-color:transparent;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-title {
  color: #3D3843;
  font-size: 35px;
  font-weight: 500;
}

.nav-menu {
  display: flex;
  gap: 30px;
  background: transparent;
}

.nav-item {
  color: #fff;
  font-size: 25px;
  cursor: pointer;
  transition: color 0.3s; /* 过渡动画 */
}

.nav-item:hover {
  text-decoration: underline;
}

/* 激活样式：字体变黑 */
.nav-item.active {
  color: #3D3843;
  font-weight: 500; /* 可选：加粗突出 */
}
</style>