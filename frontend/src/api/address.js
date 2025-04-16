import { userApi, orderApi } from './index'

// Helper function to get current user ID
function getCurrentUserId() {
  const userId = localStorage.getItem('userId') || '';
  if (!userId) {
    console.warn('警告: 未找到用户ID，地址API可能会失败');
  }
  return userId;
}

// 用户地址相关API
/**
 * 获取用户的所有地址
 * @returns {Promise}
 */
export function getUserAddresses() {
  const userId = getCurrentUserId();
  
  return userApi.get('/api/addresses', {
    headers: {
      'X-User-Id': userId
    }
  }).then(response => {
    return response;
  }).catch(error => {
    throw error;
  });
}

/**
 * 获取用户的默认地址
 * @returns {Promise}
 */
export function getDefaultAddress() {
  const userId = getCurrentUserId();
  return userApi.get('/api/addresses/default', {
    headers: {
      'X-User-Id': userId
    }
  });
}

/**
 * 获取地址详情
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function getAddressById(addressId) {
  const userId = getCurrentUserId();
  return userApi.get(`/api/addresses/${addressId}`, {
    headers: {
      'X-User-Id': userId
    }
  });
}

/**
 * 创建新地址
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function createAddress(addressData) {
  const userId = getCurrentUserId();
  return userApi.post('/api/addresses', addressData, {
    headers: {
      'X-User-Id': userId
    }
  });
}

/**
 * 更新地址
 * @param {Number} addressId 地址ID
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function updateAddress(addressId, addressData) {
  const userId = getCurrentUserId();
  return userApi.put(`/api/addresses/${addressId}`, addressData, {
    headers: {
      'X-User-Id': userId
    }
  });
}

/**
 * 删除地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function deleteAddress(addressId) {
  const userId = getCurrentUserId();
  return userApi.delete(`/api/addresses/${addressId}`, {
    headers: {
      'X-User-Id': userId
    }
  });
}

/**
 * 设置默认地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function setDefaultAddress(addressId) {
  const userId = getCurrentUserId();
  return userApi.post(`/api/addresses/${addressId}/default`, null, {
    headers: {
      'X-User-Id': userId
    }
  });
}

// 物流地址相关API
/**
 * 获取订单的物流地址
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function getOrderShippingAddress(orderId) {
  return orderApi.get(`/api/shipping-addresses/order/${orderId}`);
}

/**
 * 获取物流地址详情
 * @param {Number} addressId 物流地址ID
 * @returns {Promise}
 */
export function getShippingAddressById(addressId) {
  return orderApi.get(`/api/shipping-addresses/${addressId}`);
}

/**
 * 更新物流地址
 * @param {Number} addressId 物流地址ID
 * @param {Object} addressData 物流地址数据
 * @returns {Promise}
 */
export function updateShippingAddress(addressId, addressData) {
  return orderApi.put(`/api/shipping-addresses/${addressId}`, addressData);
}