import { defineStore } from 'pinia'
import { userApi } from '../api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    userInfo: null,
    token: null
  }),

  actions: {
    checkLoginStatus() {
      console.log('检查登录状态...');
      // 从localStorage获取登录状态
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')
      
      if (token && userInfo) {
        try {
          this.token = token;
          this.userInfo = JSON.parse(userInfo);
          this.isLoggedIn = true;
          console.log('已找到有效token和用户信息');
          return true;
        } catch (e) {
          console.error('解析用户信息失败:', e);
          this.logout(); // 出错时清除状态
          return false;
        }
      }
      
      console.log('未找到有效token或用户信息');
      this.isLoggedIn = false;
      this.userInfo = null;
      this.token = null;
      return false;
    },

    async login(username, password) {
      try {
        console.log('尝试登录用户:', username);
        const response = await userApi.login({
          username,
          password
        });
        
        console.log('登录响应:', response);
        
        if (!response.token) {
          console.error('登录响应中没有token');
          throw new Error('登录失败: 没有收到有效token');
        }
        
        // 保存登录状态和令牌
        this.token = response.token;
        this.userInfo = response.user;
        this.isLoggedIn = true;
        
        localStorage.setItem('token', response.token);
        localStorage.setItem('userInfo', JSON.stringify(response.user));
        
        console.log('登录成功，用户信息已保存');
        return true;
      } catch (error) {
        console.error('登录失败:', error);
        throw error;
      }
    },
    
    async register(userData) {
      try {
        console.log('发送注册请求数据:', {
          username: userData.username,
          password: userData.password,
          email: userData.email,
          nickname: userData.nickname,
          phone: userData.phone
        });
        
        const response = await userApi.register({
          username: userData.username,
          password: userData.password,
          email: userData.email,
          nickname: userData.nickname,
          phone: userData.phone
        });
        
        console.log('注册响应:', response);
        
        // 不再自动登录，只返回成功结果
        return true;
      } catch (error) {
        console.error('注册失败详细信息:', error);
        throw error;
      }
    },

    logout() {
      console.log('用户登出，清除数据');
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      this.isLoggedIn = false;
      this.userInfo = null;
      this.token = null;
    }
  }
}) 