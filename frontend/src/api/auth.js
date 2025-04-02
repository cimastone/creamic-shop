import { userApi } from './index'

/**
 * 用户登录
 * @param {string} username 用户名
 * @param {string} password 密码
 * @returns {Promise} 登录结果
 */
export function login(username, password) {
  return userApi.post('/api/users/login', {
    username,
    password
  })
}

/**
 * 用户注册
 * @param {Object} userData 用户数据
 * @returns {Promise} 注册结果
 */
export function register(userData) {
  return userApi.post('/api/users/register', userData)
}

/**
 * 刷新token
 * @param {string} refreshToken 刷新token
 * @returns {Promise} 新的token
 */
export function refreshToken(refreshToken) {
  return userApi.post('/api/users/refresh', { refreshToken })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return userApi.post('/api/users/logout')
} 