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
          // 保存用户ID到localStorage
          if (response.data.user.id) {
            localStorage.setItem('userId', response.data.user.id.toString())
            console.log('已保存用户ID到localStorage:', response.data.user.id)
          }
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
      // 这里不需要处理登出失败，因为可能只是因为已经登出导致的401错误
      console.log('登出请求完成:', error ? '有错误' : '成功');
    } finally {
      // 无论登出API调用成功与否，都要清除本地存储
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userId')
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const response = await userApi.get('/api/users/current')
      console.log('获取用户信息响应:', response);
      
      if (response.data) {
        userInfo.value = response.data
        // 保存用户ID到localStorage，供API请求使用
        if (response.data.id) {
          localStorage.setItem('userId', response.data.id.toString())
          console.log('已保存用户ID到localStorage:', response.data.id)
        } else {
          console.warn('警告: 用户信息中没有找到ID字段')
          // 查看响应数据结构
          console.log('用户信息数据结构:', JSON.stringify(response.data).substring(0, 200))
        }
        return response.data
      } else {
        console.error('获取用户信息失败: 响应中没有data字段')
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