<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-form">
        <h1>登录</h1>
        
        <!-- 注册成功提示 -->
        <div v-if="registrationSuccess" class="success-message">
          <i class="success-icon">✓</i>
          注册成功！请使用您的账号密码登录。
        </div>
        
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <i class="input-icon">👤</i>
              <input 
                id="username" 
                v-model="username" 
                type="text" 
                required 
                placeholder="请输入用户名"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <i class="input-icon">🔒</i>
              <input 
                id="password" 
                v-model="password" 
                type="password" 
                required
                placeholder="请输入密码"
              />
            </div>
          </div>
          
          <div v-if="errorMessage" class="error-message">
            <i class="error-icon">❌</i>
            {{ errorMessage }}
          </div>
          
          <div class="form-actions">
            <button type="submit" :disabled="isLoading" class="submit-button">
              <span class="button-text">{{ isLoading ? '登录中...' : '登录' }}</span>
              <span v-if="isLoading" class="loading-spinner"></span>
            </button>
            <div class="register-link">
              还没有账号？<router-link to="/register">立即注册</router-link>
            </div>
          </div>
        </form>
      </div>
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
    // 表单验证
    if (!username.value || !password.value) {
      throw new Error('请输入用户名和密码')
    }

    // 调用登录
    const result = await userStore.login(username.value, password.value)
    
    if (result) {
      // 获取重定向地址
      const redirect = route.query.redirect || '/'
      
      // 检查是否有保存的路由信息
      const lastRoute = localStorage.getItem('lastRoute')
      if (lastRoute) {
        try {
          const { path, query } = JSON.parse(lastRoute)
          localStorage.removeItem('lastRoute')
          router.push({ path, query: query ? JSON.parse(query) : {} })
          return
        } catch (e) {
          console.error('解析保存的路由信息失败:', e)
        }
      }
      
      // 如果没有保存的路由信息，使用redirect参数
      router.push(decodeURIComponent(redirect))
    }
  } catch (error) {
    console.error('登录失败:', error)
    errorMessage.value = error.response?.data?.message || error.message || '登录失败，请检查用户名和密码'
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
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
}

.login-form-wrapper {
  width: 100%;
  max-width: 420px;
  padding: 15px;
}

.login-form {
  width: 100%;
  padding: 35px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  animation: fadeIn 0.5s ease-out;
}

.login-form:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.12);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: var(--primary-color);
  font-size: 28px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 25px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-light);
  font-style: normal;
}

input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
}

.error-message {
  display: flex;
  align-items: center;
  color: var(--error-color);
  margin-bottom: 20px;
  padding: 12px;
  background-color: #fff2f0;
  border-radius: 8px;
  border-left: 3px solid var(--error-color);
  font-size: 14px;
}

.error-icon {
  margin-right: 8px;
  font-style: normal;
}

.submit-button {
  position: relative;
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.submit-button:hover {
  background: linear-gradient(135deg, var(--primary-dark) 0%, #0d47a1 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(25, 118, 210, 0.3);
}

.submit-button:disabled {
  background: #b7c6d8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.button-text {
  z-index: 2;
}

.loading-spinner {
  position: absolute;
  right: 15px;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link a {
  color: var(--primary-color);
  font-weight: 500;
  transition: color 0.3s;
  text-decoration: none;
}

.register-link a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

.success-message {
  display: flex;
  align-items: center;
  color: var(--success-color);
  margin-bottom: 20px;
  padding: 12px;
  background-color: #f6ffed;
  border-radius: 8px;
  border-left: 3px solid var(--success-color);
  font-size: 14px;
  animation: slideDown 0.5s ease-out;
}

.success-icon {
  margin-right: 8px;
  font-style: normal;
  font-weight: bold;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style> 