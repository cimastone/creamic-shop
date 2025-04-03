import { userApi, orderApi } from './index'

// 用户地址相关API（保持原有的接口）
/**
 * 获取用户的所有地址
 * @returns {Promise}
 */
export function getUserAddresses() {
  return userApi.get('/api/addresses');
}

/**
 * 获取用户的默认地址
 * @returns {Promise}
 */
export function getDefaultAddress() {
  return userApi.get('/api/addresses/default');
}

/**
 * 获取地址详情
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function getAddressById(addressId) {
  return userApi.get(`/api/addresses/${addressId}`);
}

/**
 * 创建新地址
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function createAddress(addressData) {
  return userApi.post('/api/addresses', addressData);
}

/**
 * 更新地址
 * @param {Number} addressId 地址ID
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function updateAddress(addressId, addressData) {
  return userApi.put(`/api/addresses/${addressId}`, addressData);
}

/**
 * 删除地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function deleteAddress(addressId) {
  return userApi.delete(`/api/addresses/${addressId}`);
}

/**
 * 设置默认地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function setDefaultAddress(addressId) {
  return userApi.post(`/api/addresses/${addressId}/default`);
}

// 物流地址相关API（新增）
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