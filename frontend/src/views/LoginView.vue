<template>
  <div class="login-container">
    <div class="login-form">
      <h1>登录</h1>
      
      <!-- 注册成功提示 -->
      <div v-if="registrationSuccess" class="success-message">
        注册成功！请使用您的账号密码登录。
      </div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            id="username" 
            v-model="username" 
            type="text" 
            required 
            placeholder="请输入用户名"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            v-model="password" 
            type="password" 
            required
            placeholder="请输入密码"
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <div class="form-actions">
          <button type="submit" :disabled="isLoading">
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
          <div class="register-link">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)
const registrationSuccess = ref(false)

// 页面加载时检查URL参数
onMounted(() => {
  if (route.query.registrationSuccess === 'true') {
    registrationSuccess.value = true
    // 自动3秒后隐藏成功消息
    setTimeout(() => {
      registrationSuccess.value = false
    }, 3000)
  }
})

const handleLogin = async () => {
  errorMessage.value = ''
  isLoading.value = true
  
  try {
    const result = await userStore.login(username.value, password.value)
    if (result) {
      router.push('/')
    }
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请检查用户名和密码'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.login-form {
  width: 100%;
  max-width: 400px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 16px;
}

button {
  width: 100%;
  padding: 12px;
  background: #1677ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover {
  background: #0958d9;
}

button:disabled {
  background: #95b8ff;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  margin-top: 16px;
}

.register-link a {
  color: #1677ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.success-message {
  color: #52c41a;
  margin-bottom: 16px;
  padding: 10px;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.success-message::before {
  content: "✓";
  margin-right: 8px;
  font-weight: bold;
}
</style> 