import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// 移除vite-plugin-mock（和MSW冲突，二选一）
// import { viteMockServe } from 'vite-plugin-mock'

export default defineConfig(({ command, mode }) => {
  // 加载环境变量（区分mock/后端环境）
  const env = loadEnv(mode, process.cwd());
  return {
    plugins: [
      vue(),
      // 删掉vite-plugin-mock相关配置（改用纯MSW）
      // viteMockServe({...}) 这一段全部删除
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      // 仅在非mock环境下启用代理（对接后端），mock环境保留空proxy（MSW拦截）
      proxy: env.VITE_USE_MSW === 'true' ? {} : {
        '/api': {
          target: env.VITE_API_TARGET || 'http://localhost:8080', // 后端地址
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '') // 去掉/api前缀
        }
      },
      port: 8090,
      open: false,
      host: '0.0.0.0',
      headers: {
        // 核心修改：保留必要规则 + 新增允许pannfmis地址
        'Content-Security-Policy': [
          "default-src 'self' 'unsafe-inline' 'unsafe-eval'",
          "worker-src 'self' blob: data:", // 允许Worker创建（解决MSW拦截）
          "script-src 'self' 'unsafe-inline' 'unsafe-eval'", // 允许MSW脚本执行
          // 关键：添加http://pannfmis + 保留原有允许的地址
          `connect-src 'self' localhost:8090 ws://localhost:8090 http://pannfmis ${env.VITE_USE_MSW === 'false' ? env.VITE_API_TARGET : ''}`
        ].join('; ')
      }
    },
    // 可选：添加优化依赖，避免MSW加载问题
    //optimizeDeps: {
    //  include: ['msw']
    //}
  }

});