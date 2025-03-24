<script setup>
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import { useUserStore } from './stores/user'
import { useCartStore } from './stores/cart'
import { useRouter } from 'vue-router'
import './assets/styles/variables.css'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const handleLogout = () => {
  userStore.logout()
  cartStore.clearCart()
  router.push('/')
}
</script>

<template>
  <div class="app">
    <nav class="navbar">
      <div class="navbar-logo">
        <span class="logo-text">陶器优选</span>
        <RouterLink to="/" class="site-name">陶器优选</RouterLink>
      </div>
      <div class="navbar-links">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink to="/products">产品列表</RouterLink>
        <RouterLink v-if="userStore.isLoggedIn" to="/cart">
          购物车 <span v-if="cartStore.itemCount > 0" class="cart-count">{{ cartStore.itemCount }}</span>
        </RouterLink>
        <RouterLink v-if="userStore.isLoggedIn" to="/orders">我的订单</RouterLink>
        <RouterLink v-if="!userStore.isLoggedIn" to="/login">登录</RouterLink>
        <RouterLink v-if="!userStore.isLoggedIn" to="/register">注册</RouterLink>
        <div v-if="userStore.isLoggedIn" class="user-info">
          欢迎, {{ userStore.userInfo?.name || userStore.userInfo?.nickname || userStore.userInfo?.username }}
          <a href="#" @click.prevent="handleLogout">退出</a>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="footer">
      <div class="footer-content">
        <p>&copy; {{ new Date().getFullYear() }} 陶瓷商城 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 1rem 0;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-links a {
  color: #666;
  text-decoration: none;
  transition: color 0.3s;
}

.nav-links a:hover {
  color: #4CAF50;
}

.main-content {
  flex: 1;
}

.footer {
  background-color: #f8f9fa;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
  color: #666;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
}

a {
  color: #4CAF50;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

button {
  cursor: pointer;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

nav {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
  display: flex;
  align-items: center;
}

nav a {
  color: #333;
  text-decoration: none;
  padding: 0.5rem 1rem;
  margin-right: 1rem;
  border-radius: 4px;
}

nav a:hover {
  background-color: #f5f5f5;
}

nav a.router-link-active {
  color: #1976d2;
  font-weight: bold;
}

.right-menu {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.username {
  color: #666;
}

.cart-link {
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #f44336;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.logout {
  cursor: pointer;
  color: #666;
}

.logout:hover {
  color: #f44336;
}
</style>
