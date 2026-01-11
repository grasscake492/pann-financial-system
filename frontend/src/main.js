// 导入Vue核心创建函数
import { createApp } from 'vue'
// 导入Pinia状态管理（全局共享数据）
import { createPinia } from 'pinia'
// 导入路由配置
import router from './router'
// 导入根组件
import App from './App.vue'

// 导入全局样式（核心步骤）
import './assets/css/global.css'
import ElementPlus from 'element-plus' // 引入Element Plus核心
import 'element-plus/dist/index.css' // 引入样式

// 创建Vue应用实例
const app = createApp(App)
const pinia = createPinia()

// 关键修改：在安装路由之前执行清理
const cleanupStorage = () => {
    console.log('应用启动，清理可能残留的登录数据...')

    // 检查并清理不完整的登录数据
    const token = localStorage.getItem('pann_token')
    const userRole = localStorage.getItem('pann_user_role')
    const userInfo = localStorage.getItem('pann_user_info')

    // 如果只有token没有其他信息，视为无效数据
    if (token && (!userRole || !userInfo)) {
        console.warn('发现不完整的登录数据，执行清理...')
        localStorage.removeItem('pann_token')
        localStorage.removeItem('pann_user_role')
        localStorage.removeItem('pann_user_info')
    }

    // 尝试解析userInfo，如果不是有效JSON，清理
    if (userInfo) {
        try {
            JSON.parse(userInfo)
        } catch {
            console.warn('userInfo不是有效JSON，执行清理...')
            localStorage.removeItem('pann_token')
            localStorage.removeItem('pann_user_role')
            localStorage.removeItem('pann_user_info')
        }
    }
}

// 执行清理
cleanupStorage()

// 注册全局插件/依赖
app.use(pinia)
app.use(router)
app.use(ElementPlus)

// 将Vue实例挂载到public/index.html中的#app容器
app.mount('#app')


// =============================================
// Mock 模拟数据开关配置
// 通过注释以下两处代码来控制是否启用Mock数据
// =============================================

// ==========【方法一：静态全局导入Mock】==========
// 如果启用Mock数据，取消下面这行的注释
import '../mock/index.js'  // 取消注释开启静态Mock

//==========【方法二：动态导入Mock】==========
//如果启用Mock数据，取消下面整个if语句的注释
if (process.env.NODE_ENV === 'development') {
    import('../mock/index.js').then(() => {
        console.log('Mock服务已动态导入');
    });
}