<template>
  <header class="header">
    <nav class="nav-container">
      <router-link to="/" class="logo">Ceramic Shop</router-link>
      <div class="nav-links">
        <router-link to="/products">Products</router-link>
        <router-link to="/cart" class="cart-link">
          Cart
          <span v-if="cartItemCount" class="cart-count">{{ cartItemCount }}</span>
        </router-link>
        <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
        <router-link v-if="!isLoggedIn" to="/register">Register</router-link>
        <a v-if="isLoggedIn" href="#" @click.prevent="logout">Logout</a>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)
const cartItemCount = ref(0)

const logout = () => {
  // 登出逻辑将在这里实现
  isLoggedIn.value = false
  router.push('/login')
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-links a {
  text-decoration: none;
  color: #666;
  transition: color 0.3s;
}

.nav-links a:hover {
  color: #4CAF50;
}

.cart-link {
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 0.8rem;
}
</style> 