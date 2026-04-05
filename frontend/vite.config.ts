import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv, type ConfigEnv, type UserConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export function createViteConfig({ mode }: ConfigEnv): UserConfig {
  const env = loadEnv(mode, process.cwd(), '')
  const apiProxyTarget = (env.VITE_API_PROXY_TARGET ?? 'http://127.0.0.1:8080').replace(/\/$/, '')

  return {
    plugins: [
      vue(),
      vueDevTools(),
    ],
    server: {
      proxy: {
        '/api': {
          // Prefer IPv4 loopback in local dev to avoid intermittent localhost IPv6 resolution failures.
          target: apiProxyTarget,
          changeOrigin: true,
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },
  }
}

export default defineConfig(createViteConfig)
