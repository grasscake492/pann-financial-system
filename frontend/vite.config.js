import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 导入node路径模块，用于配置别名

// https://vitejs.dev/config/
export default defineConfig({
  // 注册Vue插件，让Vite支持Vue文件编译
  plugins: [vue()],
  // 路径解析配置：解决多层目录相对路径冗长问题
  resolve: {
    alias: {
      // 配置@为src目录的别名，后续代码中@代表src/
      '@': path.resolve(__dirname, 'src')
    }
  },
  // 开发服务器配置：解决前端调后端接口的跨域问题
  server: {
    // 跨域代理规则
    proxy: {
      // 匹配所有以/api开头的请求
      '/api': {
        // 后端接口基础地址（后续替换为真实后端地址，比如http://localhost:8080）
        target: 'http://localhost:8080',
        // 开启跨域（必须）
        changeOrigin: true,
        // 重写路径：去掉请求URL中的/api前缀（如果后端接口没有/api前缀则需要）
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    },
    // 端口号（可选，默认5173，冲突时可改）
    port: 5173,
    // 自动打开浏览器（可选）
    open: false
  }
})