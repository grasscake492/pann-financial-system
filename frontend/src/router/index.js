import { createRouter, createWebHistory } from 'vue-router'
// 导入权限工具：替换为改造后的auth.js函数
import { isLogin, checkHomeLoginStatus } from '@/utils/auth.js'
// 修复：精准导入useUserStore
import { useUserStore } from '@/stores/user'
// 修复：ElMessage 导入容错
let ElMessage = null
try {
    import('element-plus').then(module => {
        ElMessage = module.ElMessage
    })
} catch (e) {
    ElMessage = { error: (msg) => alert(msg) } // 降级为alert
}

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
const EditorCenter = () => import(/* @vite-ignore */ '@/views/admin/editorial/ManageCenter.vue')
const NewsCenter = () => import(/* @vite-ignore */ '@/views/admin/news/ManageCenter.vue')
const OperationCenter = () => import(/* @vite-ignore */ '@/views/admin/operation/ManageCenter.vue')
const AdminAnnouncement = () => import(/* @vite-ignore */ '@/views/admin/Announcement.vue')
// 系统管理员专属页面
const SuperManage = () => import(/* @vite-ignore */ '@/views/admin/system/SuperManage.vue')

// 路由规则配置
const routes = [
    // 默认路由：未登录跳登录页，已登录跳首页（修复：redirect参数为当前路径）
    {
        path: '/',
        redirect: (to) => {
            return isLogin() ? '/home' : { path: '/login', query: { redirect: to.fullPath } }
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
            role: 'user', // 改为字符串，统一格式
            title: '查看公告'
        }
    },

    // 管理员公告管理（所有部门管理员可访问）
    {
        path: '/admin/announcement',
        name: 'AdminAnnouncement',
        component: AdminAnnouncement,
        meta: {
            requiresAuth: true,
            role: ['super_admin','news_admin', 'editorial_admin', 'operation_admin'], // 所有部门管理员都能进
            dept: ['新闻部', '编辑部', '运营部'],
            title: '修改公告'
        }
    },

    // 各部门管理员专属路由
    {
        path: '/admin/editorial/ManageCenter',
        name: 'EditorCenter',
        component: EditorCenter,
        meta: {
            requiresAuth: true,
            role: 'editorial_admin', // 仅编辑部管理员能进
            dept: '编辑部',
            title: '编辑部管理中心'
        }
    },
    {
        path: '/admin/news/ManageCenter',
        name: 'NewsCenter',
        component: NewsCenter,
        meta: {
            requiresAuth: true,
            role: 'news_admin', // 仅新闻部管理员能进
            dept: '新闻部',
            title: '新闻部任务管理'
        }
    },
    {
        path: '/admin/operation/ManageCenter',
        name: 'OperationCenter',
        component: OperationCenter,
        meta: {
            requiresAuth: true,
            role: 'operation_admin', // 仅运营部管理员能进
            dept: '运营部',
            title: '运营部人员管理'
        }
    },

    // 系统管理员专属路由
    {
        path: '/admin/system/ManageCenter',
        name: 'SystemManage',
        component: SuperManage,
        meta: {
            requiresAuth: true,
            role: 'super_admin',
            title: '系统管理员'
        }
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL || '/'),
    routes
})

// 全局前置守卫（核心：角色+部门权限校验）
router.beforeEach((to, from, next) => {
    // 1. 处理公开页面：已登录访问login/register，直接跳首页
    if (to.meta?.requiresAuth === false) {
        if (isLogin()) {
            if (router.resolve('/home').meta?.requiresAuth) {
                next('/home');
                return;
            }
        }
        next();
        return;
    }

    // 2. 需要登录的页面：先校验登录态
    if (!isLogin()) {
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
        return;
    }

    // 3. 已登录：初始化用户Store + 校验角色+部门权限
    const userStore = useUserStore()
    // 修复：使用userRole getter，而非role属性
    const userRole = userStore.userRole || localStorage.getItem('pann_user_role') || ''
    // 修复：localStorage解析容错
    const userInfoStr = localStorage.getItem('pann_user_info') || '{}'
    let userInfo = {}
    try {
        userInfo = JSON.parse(userInfoStr)
    } catch (e) {
        userInfo = {}
    }
    const userDept = userInfo.department_name || ''

    // 辅助函数：安全调用checkHomeLoginStatus
    const safeCheckHomeLogin = () => {
        if (typeof checkHomeLoginStatus === 'function') {
            checkHomeLoginStatus()
        }
    }

    // 4. 超级管理员：放行所有页面
    if (userRole === 'super_admin') {
        if (to.name === 'Home') safeCheckHomeLogin()
        next()
        return
    }

    // 5. 部门管理员（dept_admin）：校验部门匹配
    if (['news_admin', 'editorial_admin', 'operation_admin'].includes(userRole)) {
        // 无dept配置 = 通用页面，直接放行
        if (!to.meta.dept) {
            if (to.name === 'Home') safeCheckHomeLogin()
            next()
            return
        }
        // 统一转为数组，兼容单/多部门配置
        const allowDept = Array.isArray(to.meta.dept) ? to.meta.dept : [to.meta.dept]
        if (allowDept.includes(userDept)) {
            if (to.name === 'Home') safeCheckHomeLogin()
            next()
        } else {
            next('/home')
            ElMessage.error(`无权限访问【${allowDept.join('、')}】管理页面！`)
        }
        return
    }

    // 6. 普通用户：仅放行无角色限制/角色为user的页面
    // 修复：统一处理role为字符串/数组的情况
    const allowRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role]
    if (!to.meta.role || allowRoles.includes('user')) {
        if (to.name === 'Home') safeCheckHomeLogin()
        next()
    } else {
        next('/home')
        ElMessage.error('无访问权限，请联系管理员！')
    }

})

// 路由后置守卫：设置页面标题
router.afterEach((to) => {
    document.title = to.meta.title || 'PANN财务管理系统'
})

export default router