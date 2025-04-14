<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <div class="register-form">
        <h1>注册</h1>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <i class="input-icon">👤</i>
              <input 
                id="username" 
                v-model="formData.username" 
                type="text" 
                required 
                placeholder="请输入用户名（4-20个字符，只能包含字母、数字和下划线）"
              />
            </div>
            <small class="form-text">用户名长度必须在4-20个字符之间，只能包含字母、数字和下划线</small>
          </div>
          
          <div class="form-group">
            <label for="nickname">昵称</label>
            <div class="input-wrapper">
              <i class="input-icon">😊</i>
              <input 
                id="nickname" 
                v-model="formData.nickname" 
                type="text" 
                required 
                placeholder="请输入昵称"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="email">邮箱</label>
            <div class="input-wrapper">
              <i class="input-icon">📧</i>
              <input 
                id="email" 
                v-model="formData.email" 
                type="email" 
                required 
                placeholder="请输入邮箱"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="phone">手机号码</label>
            <div class="input-wrapper">
              <i class="input-icon">📱</i>
              <input 
                id="phone" 
                v-model="formData.phone" 
                type="tel" 
                placeholder="请输入手机号码（例如：13800138000）"
              />
            </div>
            <small class="form-text">请输入11位手机号码，例如：13800138000</small>
          </div>
          
          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <i class="input-icon">🔒</i>
              <input 
                id="password" 
                v-model="formData.password" 
                type="password" 
                required
                placeholder="请输入密码（6-20个字符）"
              />
            </div>
            <small class="form-text">密码长度必须在6-20个字符之间</small>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <div class="input-wrapper">
              <i class="input-icon">🔐</i>
              <input 
                id="confirmPassword" 
                v-model="confirmPassword" 
                type="password" 
                required
                placeholder="请再次输入密码"
              />
            </div>
          </div>
          
          <div v-if="errorMessage" class="error-message">
            <i class="error-icon">❌</i>
            {{ errorMessage }}
          </div>
          
          <div class="form-actions">
            <button type="submit" :disabled="isLoading" class="submit-button">
              <span class="button-text">{{ isLoading ? '注册中...' : '注册' }}</span>
              <span v-if="isLoading" class="loading-spinner"></span>
            </button>
            <div class="login-link">
              已有账号？<router-link to="/login">立即登录</router-link>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formData = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: ''
})
const confirmPassword = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

// 表单验证函数
const validateForm = () => {
  // 验证用户名
  if (!/^[a-zA-Z0-9_]{4,20}$/.test(formData.username)) {
    errorMessage.value = '用户名必须是4-20个字符，只能包含字母、数字和下划线'
    return false
  }
  
  // 验证密码
  if (formData.password.length < 6 || formData.password.length > 20) {
    errorMessage.value = '密码长度必须在6-20个字符之间'
    return false
  }
  
  // 验证邮箱
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.email)) {
    errorMessage.value = '请输入有效的邮箱地址'
    return false
  }
  
  // 验证手机号（如果填写了）
  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    errorMessage.value = '请输入有效的11位手机号码'
    return false
  }
  
  // 验证两次密码是否一致
  if (formData.password !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return false
  }
  
  return true
}

const handleRegister = async () => {
  // 先进行表单验证
  if (!validateForm()) {
    return
  }
  
  errorMessage.value = ''
  isLoading.value = true
  
  try {
    const result = await userStore.register(formData)
    if (result) {
      // 注册成功后跳转到登录页面，并带上成功消息
      router.push({
        path: '/login',
        query: { registrationSuccess: 'true' }
      })
    }
  } catch (error) {
    errorMessage.value = error.message || '注册失败，请稍后再试'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
}

.register-form-wrapper {
  width: 100%;
  max-width: 480px;
  padding: 15px;
}

.register-form {
  width: 100%;
  padding: 35px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  animation: fadeIn 0.5s ease-out;
}

.register-form:hover {
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

.form-text {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-light);
  font-style: italic;
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

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.login-link a {
  color: var(--primary-color);
  font-weight: 500;
  transition: color 0.3s;
  text-decoration: none;
}

.login-link a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}
</style> 