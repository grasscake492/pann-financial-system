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
// 创建Vue应用实例
const app = createApp(App)

// 注册全局插件/依赖
app.use(createPinia()) // 注册Pinia，后续可使用stores中的状态
app.use(router) // 注册路由，后续可通过路由跳转页面

// 将Vue实例挂载到public/index.html中的#app容器
app.mount('#app')