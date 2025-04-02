import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout } from '@/api/auth'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(null)
  const isLoggedIn = computed(() => !!token.value)

  // 登录
  async function login(username, password) {
    try {
      const response = await apiLogin(username, password)
      
      if (response.data) {
        // 保存token
        token.value = response.data.token
        refreshToken.value = response.data.refreshToken || ''
        localStorage.setItem('token', response.data.token)
        if (response.data.refreshToken) {
          localStorage.setItem('refreshToken', response.data.refreshToken)
        }
        
        // 设置用户信息（如果返回）
        if (response.data.user) {
          userInfo.value = response.data.user
        } else {
          // 获取用户信息
          await fetchUserInfo()
        }
        return true
      } else {
        throw new Error('登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 登出
  async function logout() {
    try {
      await apiLogout()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除本地存储
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const response = await userApi.get('/api/users/current')
      if (response.data) {
        userInfo.value = response.data
      } else {
        throw new Error('获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 检查登录状态
  function checkLoginStatus() {
    if (!token.value) {
      return false
    }
    return true
  }

  // 更新用户信息
  async function updateUserInfo(info) {
    try {
      const response = await userApi.put('/api/users/current', info)
      if (response.code === 200 && response.data) {
        userInfo.value = response.data
        return true
      }
      throw new Error('更新用户信息失败')
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    login,
    logout,
    fetchUserInfo,
    checkLoginStatus,
    updateUserInfo
  }
}) 