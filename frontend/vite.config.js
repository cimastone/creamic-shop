import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api/products': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace('/api/products', '/product/api/products');
          console.log(`Proxy [products]: ${path} -> ${newPath}`);
          return newPath;
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('产品代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('产品代理请求:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('产品代理响应:', proxyRes.statusCode, req.url);
          });
        }
      },
      '/api/users': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace('/api/users', '/user/api/users');
          console.log(`Proxy [users]: ${path} -> ${newPath}`);
          return newPath;
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('用户代理错误:', err);
          });
        }
      },
      '/api/addresses': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => {
          // 构建新路径
          const basePath = path.replace('/api/addresses', '/user/api/users');
          
          // 处理不同类型的地址请求
          if (path.match(/\/api\/addresses\/\d+/)) {
            // 处理形如 /api/addresses/123 的路径
            const newPath = path.replace(/\/api\/addresses\/(\d+)(.*)/, `/user/api/users/{userId}/addresses/$1$2`);
            console.log(`Proxy [addresses]: ${path} -> ${newPath}`);
            return newPath;
          } else if (path === '/api/addresses') {
            // 处理基本路径 /api/addresses
            const newPath = `/user/api/users/{userId}/addresses`;
            console.log(`Proxy [addresses]: ${path} -> ${newPath}`);
            return newPath;
          } else if (path.includes('/api/addresses/default')) {
            // 处理默认地址请求
            const newPath = `/user/api/users/{userId}/addresses/default`;
            console.log(`Proxy [addresses]: ${path} -> ${newPath}`);
            return newPath;
          } else {
            // 处理其他与地址相关的路径
            const newPath = path.replace('/api/addresses', `/user/api/users/{userId}/addresses`);
            console.log(`Proxy [addresses]: ${path} -> ${newPath}`);
            return newPath;
          }
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('地址代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('地址代理请求:', req.method, req.url);
            
            // 从请求头中提取用户ID
            const userId = req.headers['x-user-id'] || '1';
            
            // 更新URL中的用户ID占位符
            if (proxyReq.path.includes('{userId}')) {
              const newPath = proxyReq.path.replace('{userId}', userId);
              proxyReq.path = newPath;
              console.log(`更新地址代理URL，用户ID: ${userId}, 新路径: ${proxyReq.path}`);
            }
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('地址代理响应:', proxyRes.statusCode, req.url);
          });
        }
      },
      '/api/orders': {
        target: 'http://localhost:8003',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace('/api/orders', '/order/api/orders');
          console.log(`Proxy [orders]: ${path} -> ${newPath}`);
          return newPath;
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('订单代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('订单代理请求:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('订单代理响应:', proxyRes.statusCode, req.url);
          });
        }
      },
      '/api/shipping-addresses': {
        target: 'http://localhost:8003',
        changeOrigin: true,
        rewrite: (path) => {
          const newPath = path.replace('/api/shipping-addresses', '/order/api/shipping-addresses');
          console.log(`Proxy [shipping]: ${path} -> ${newPath}`);
          return newPath;
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('物流地址代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('物流地址代理请求:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('物流地址代理响应:', proxyRes.statusCode, req.url);
          });
        }
      }
    }
  }
})
