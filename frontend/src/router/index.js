import { createRouter, createWebHistory } from 'vue-router'
// 导入权限工具：替换为改造后的auth.js函数
import { isLogin, checkHomeLoginStatus, hasRole } from '@/utils/auth.js'
// 导入用户状态仓库：校验角色
import { useUserStore } from '@/stores/userStore.js'

// 懒加载导入页面组件（Vite 格式）
// 公开页面
const Login = () => import(/* @vite-ignore */ '@/views/Login.vue')
const Register = () => import(/* @vite-ignore */ '@/views/Register.vue')

// 普通用户通用页面（user 目录）
const Home = () => import(/* @vite-ignore */ '@/views/user/Home.vue')
const UserAccount = () => import(/* @vite-ignore */ '@/views/user/Account.vue')
const UserRoyaltyDetail = () => import(/* @vite-ignore */ '@/views/user/RoyaltyDetail.vue')
const UserAnnouncement = () => import(/* @vite-ignore */ '@/views/user/Announcement.vue')

// 管理员专属页面（admin 目录，按部门拆分）
const EditorCenter = () => import(/* @vite-ignore */ '@/views/admin/editor/ManageCenter.vue')
const NewsCenter = () => import(/* @vite-ignore */ '@/views/admin/news/ManageCenter.vue')
const OperationCenter = () => import(/* @vite-ignore */ '@/views/admin/operation/ManageCenter.vue')
// 修复语法错误：注释格式+变量名拼写
const AdminAnnouncement = () => import(/* @vite-ignore */ '@/views/admin/Announcement.vue')
//系统管理员专属页面super_admin
const SuperManage = () => import(/* @vite-ignore */ '@/views/admin/system/SuperManage.vue')

// 路由规则配置
const routes = [
    // 默认路由：未登录跳登录页，已登录跳首页（优化跳转逻辑）
    {
        path: '/',
        redirect: (to) => {
            // 补充：若登录态失效，直接跳登录页
            return isLogin() ? '/home' : { path: '/login', query: { redirect: '/' } }
        }
    },

    // 公开路由（无需登录）
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { requiresAuth: false, role: '', title: '登录页' }
    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: { requiresAuth: false, role: '', title: '注册页' }
    },

    // 登录后通用页面（所有角色可访问）
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: {
            requiresAuth: true,
            role: '',
            title: '系统首页',
            // 新增：标记首页，用于后续导航高亮
            isHome: true
        }
    },
    {
        path: '/user/account',
        name: 'UserAccount',
        component: UserAccount,
        meta: { requiresAuth: true, role: '', title: '个人账户' }
    },
    {
        path: '/user/royalty-detail',
        name: 'UserRoyaltyDetail',
        component: UserRoyaltyDetail,
        meta: { requiresAuth: true, role: '', title: '个人稿费明细' }
    },
    {
        path: '/user/announcement',
        name: 'UserAnnouncement',
        component: UserAnnouncement,
        meta: {
            requiresAuth: true,
            role: ['user'],
            title: '查看公告'
        }
    },
    {
        path: '/admin/announcement',
        name: 'AdminAnnouncement',
        component: AdminAnnouncement,
        meta:{
            requiresAuth: true,
            role: ['operation_admin', 'news_admin', 'editor_admin'],
            title: '修改公告'
        }
    },

    // 管理员专属路由（按角色控制访问）
    {
        path: '/admin/editor/ManageCenter',
        name: 'EditorCenter',
        component: EditorCenter,
        meta: {
            requiresAuth: true,
            role: 'editor_admin',
            title: '编辑部管理中心'
        }
    },
    {
        path: '/admin/news/ManageCenter',
        name: 'NewsCenter',
        component: NewsCenter,
        meta: {
            requiresAuth: true,
            role: 'news_admin',
            title: '新闻部任务管理'
        }
    },
    {
        path: '/admin/operation/ManageCenter',
        name: 'OperationCenter',
        component: OperationCenter,
        meta: {
            requiresAuth: true,
            role: 'operation_admin',
            title: '运营部人员管理'
        }
    },
    {
        path: '/admin/system/ManageCenter',
        name: 'SystemManage',
        component: SuperManage,
        meta: {
            requiresAuth: true,
            role: 'super_admin',
            title: '系统管理员'
        },
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL || '/'),
    routes
})

// 全局前置守卫（优化版）
router.beforeEach((to, from, next) => {
    // 1. 无需登录的页面直接放行
    if (!to.meta.requiresAuth) {
        next()
        return
    }

    // 2. 需要登录的页面：先校验登录态
    if (!isLogin()) {
        // 补充：携带跳转来源参数，登录后可返回原页面
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
        return
    }

    // 3. 已登录：校验角色权限（复用auth.js的hasRole函数）
    const userStore = useUserStore()
    const userRole = userStore.role
    // 角色为空（所有角色可访问）或权限匹配则放行
    if (!to.meta.role || hasRole(to.meta.role)) {
        // 特殊处理：首页路由额外校验（确保逻辑统一）
        if (to.name === 'Home') {
            checkHomeLoginStatus() // 调用首页专属校验函数
        }
        next()
    } else {
        // 无权限：跳首页并提示
        next('/home')
        // 优化提示：避免alert阻塞，改用友好提示（可替换为全局消息组件）
        ElMessage?.({
            type: 'error',
            message: '无访问权限，请联系管理员！'
        }) || alert('无访问权限，请联系管理员！')
    }
})

// 路由后置守卫：设置页面标题
router.afterEach((to) => {
    document.title = to.meta.title || 'PANN财务管理系统'
})

export default router