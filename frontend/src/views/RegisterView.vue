<template>
  <div class="register-container">
    <div class="register-form">
      <h1>注册</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            id="username" 
            v-model="formData.username" 
            type="text" 
            required 
            placeholder="请输入用户名（4-20个字符，只能包含字母、数字和下划线）"
          />
          <small class="form-text">用户名长度必须在4-20个字符之间，只能包含字母、数字和下划线</small>
        </div>
        
        <div class="form-group">
          <label for="nickname">昵称</label>
          <input 
            id="nickname" 
            v-model="formData.nickname" 
            type="text" 
            required 
            placeholder="请输入昵称"
          />
        </div>
        
        <div class="form-group">
          <label for="email">邮箱</label>
          <input 
            id="email" 
            v-model="formData.email" 
            type="email" 
            required 
            placeholder="请输入邮箱"
          />
        </div>
        
        <div class="form-group">
          <label for="phone">手机号码</label>
          <input 
            id="phone" 
            v-model="formData.phone" 
            type="tel" 
            placeholder="请输入手机号码（例如：13800138000）"
          />
          <small class="form-text">请输入11位手机号码，例如：13800138000</small>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            v-model="formData.password" 
            type="password" 
            required
            placeholder="请输入密码（6-20个字符）"
          />
          <small class="form-text">密码长度必须在6-20个字符之间</small>
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            id="confirmPassword" 
            v-model="confirmPassword" 
            type="password" 
            required
            placeholder="请再次输入密码"
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <div class="form-actions">
          <button type="submit" :disabled="isLoading">
            {{ isLoading ? '注册中...' : '注册' }}
          </button>
          <div class="login-link">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </form>
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
}

.register-form {
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

.form-text {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 16px;
  padding: 10px;
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  border-radius: 4px;
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

.login-link {
  text-align: center;
  margin-top: 16px;
}

.login-link a {
  color: #1677ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style> 