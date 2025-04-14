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

const getUserInitial = () => {
  const name = userStore.userInfo?.name || userStore.userInfo?.nickname || userStore.userInfo?.username || ''
  return name.charAt(0).toUpperCase()
}
</script>

<template>
  <div class="app">
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <RouterLink to="/" class="site-logo">
            <span class="logo-icon">🏺</span>
            <span class="logo-text">陶器优选</span>
          </RouterLink>
        </div>
        <div class="navbar-links">
          <RouterLink to="/" class="nav-link" active-class="active">
            <i class="nav-icon">🏠</i>
            <span>首页</span>
          </RouterLink>
          <RouterLink to="/products" class="nav-link" active-class="active">
            <i class="nav-icon">🛍️</i>
            <span>产品列表</span>
          </RouterLink>
          <RouterLink v-if="userStore.isLoggedIn" to="/cart" class="nav-link" active-class="active">
            <div class="cart-icon-wrapper">
              <i class="nav-icon">🛒</i>
              <span v-if="cartStore.itemCount > 0" class="cart-count">{{ cartStore.itemCount }}</span>
            </div>
            <span>购物车</span>
          </RouterLink>
          <RouterLink v-if="userStore.isLoggedIn" to="/orders" class="nav-link" active-class="active">
            <i class="nav-icon">📋</i>
            <span>我的订单</span>
          </RouterLink>
        </div>
        <div class="navbar-auth">
          <template v-if="!userStore.isLoggedIn">
            <RouterLink to="/login" class="auth-link login" active-class="active">登录</RouterLink>
            <RouterLink to="/register" class="auth-link register" active-class="active">注册</RouterLink>
          </template>
          <div v-else class="user-dropdown">
            <div class="user-info">
              <span class="user-avatar">{{ getUserInitial() }}</span>
              <span class="user-name">{{ userStore.userInfo?.name || userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <i class="dropdown-icon">▼</i>
            </div>
            <div class="dropdown-menu">
              <RouterLink to="/profile" class="dropdown-item">个人资料</RouterLink>
              <a href="#" @click.prevent="handleLogout" class="dropdown-item logout">退出登录</a>
            </div>
          </div>
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
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  color: var(--text-primary);
  background-color: #f8f9fa;
}

.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Navbar Styles */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.navbar-logo {
  display: flex;
  align-items: center;
}

.site-logo {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--primary-color);
  font-weight: 700;
  font-size: 1.5rem;
  transition: transform 0.3s;
}

.site-logo:hover {
  transform: scale(1.05);
}

.logo-icon {
  font-size: 1.8rem;
  margin-right: 8px;
}

.logo-text {
  background: linear-gradient(45deg, var(--primary-color), var(--primary-dark));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.navbar-links {
  display: flex;
  gap: 5px;
  align-items: center;
}

.nav-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: var(--text-secondary);
  padding: 10px 15px;
  border-radius: 8px;
  transition: all 0.3s;
  font-size: 14px;
}

.nav-link:hover {
  background-color: rgba(25, 118, 210, 0.05);
  color: var(--primary-color);
  transform: translateY(-2px);
}

.nav-link.active {
  color: var(--primary-color);
  background-color: rgba(25, 118, 210, 0.1);
  font-weight: 500;
}

.nav-icon {
  font-size: 1.3rem;
  margin-bottom: 3px;
  font-style: normal;
}

.cart-icon-wrapper {
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -10px;
  background-color: var(--error-color);
  color: white;
  border-radius: 50%;
  min-width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  font-weight: bold;
  padding: 0 4px;
}

.navbar-auth {
  display: flex;
  align-items: center;
  gap: 10px;
}

.auth-link {
  padding: 8px 20px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.auth-link.login {
  color: var(--primary-color);
  background-color: rgba(25, 118, 210, 0.1);
}

.auth-link.login:hover {
  background-color: rgba(25, 118, 210, 0.2);
}

.auth-link.register {
  color: #fff;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-dark));
  box-shadow: 0 4px 8px rgba(25, 118, 210, 0.2);
}

.auth-link.register:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(25, 118, 210, 0.3);
}

/* User dropdown styles */
.user-dropdown {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: rgba(25, 118, 210, 0.1);
}

.user-info:hover {
  background-color: rgba(25, 118, 210, 0.15);
}

.user-avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(45deg, var(--primary-color), var(--primary-dark));
  color: white;
  border-radius: 50%;
  font-weight: bold;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
  color: var(--text-primary);
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-icon {
  font-size: 10px;
  color: var(--text-secondary);
  transition: transform 0.3s;
  font-style: normal;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 120%;
  width: 160px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.3s;
  z-index: 1000;
}

.user-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: block;
  padding: 10px 15px;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.3s;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: rgba(25, 118, 210, 0.05);
  color: var(--primary-color);
}

.dropdown-item.logout {
  color: var(--error-color);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  margin-top: 5px;
  padding-top: 10px;
}

.dropdown-item.logout:hover {
  background-color: rgba(229, 57, 53, 0.05);
}

/* Main content */
.main-content {
  flex: 1;
}

/* Footer */
.footer {
  background-color: #fff;
  padding: 25px 0;
  margin-top: auto;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
