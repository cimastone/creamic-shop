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
          const newPath = path.replace('/api/addresses', '/user/api/addresses');
          console.log(`Proxy [addresses]: ${path} -> ${newPath}`);
          return newPath;
        },
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('地址代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('地址代理请求:', req.method, req.url);
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
