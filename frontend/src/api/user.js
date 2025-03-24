import api from './index'

export const userApi = {
  // 登录
  login(data) {
    return api.post('/api/users/login', data)
  },

  // 注册
  register(data) {
    return api.post('/api/users/register', data)
  },

  // 获取用户信息
  getUserInfo(userId) {
    return api.get(`/api/users/${userId}`)
  },

  // 更新用户信息
  updateUserInfo(userId, data) {
    return api.put(`/api/users/${userId}`, data)
  },

  // 修改密码
  changePassword(userId, data) {
    return api.put(`/api/users/${userId}/password`, data)
  },

  // 获取用户地址列表
  getUserAddresses(userId) {
    return api.get(`/api/users/${userId}/addresses`)
  },

  // 获取默认地址
  getUserDefaultAddress(userId) {
    return api.get(`/api/users/${userId}/addresses/default`)
  },

  // 添加新地址
  addAddress(userId, addressData) {
    return api.post(`/api/users/${userId}/addresses`, addressData)
  },

  // 更新地址
  updateAddress(userId, addressId, addressData) {
    return api.put(`/api/users/${userId}/addresses/${addressId}`, addressData)
  },

  // 设置默认地址
  setDefaultAddress(userId, addressId) {
    return api.put(`/api/users/${userId}/addresses/${addressId}/default`)
  },

  // 删除地址
  deleteAddress(userId, addressId) {
    return api.delete(`/api/users/${userId}/addresses/${addressId}`)
  }
} 