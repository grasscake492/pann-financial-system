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
// 1. 注释掉静态全局导入的Mock
 import '../mock/index.js'
// 仅开发环境加载MSW

// 创建Vue应用实例
// 全局唯一的Pinia实例

const app = createApp(App)

// 注册全局插件/依赖
app.use(createPinia()) // 注册Pinia，后续可使用stores中的状态
app.use(router) // 注册路由，后续可通过路由跳转页面
app.use(ElementPlus) // 注册Element Plus组件库
// 将Vue实例挂载到public/index.html中的#app容器
app.mount('#app')

// 2. 注释掉开发环境动态导入的Mock
if (process.env.NODE_ENV === 'development') {
     import('../mock/index.js').then(() => {
         console.log('Mock服务已动态导入');
     });
 }