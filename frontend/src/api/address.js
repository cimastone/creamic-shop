import { userApi as api } from './index'

/**
 * 获取用户的所有地址
 * @returns {Promise}
 */
export function getUserAddresses() {
  return api.get('/api/addresses');
}

/**
 * 获取用户的默认地址
 * @returns {Promise}
 */
export function getDefaultAddress() {
  return api.get('/api/addresses/default');
}

/**
 * 获取地址详情
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function getAddressById(addressId) {
  return api.get(`/api/addresses/${addressId}`);
}

/**
 * 创建新地址
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function createAddress(addressData) {
  return api.post('/api/addresses', addressData);
}

/**
 * 更新地址
 * @param {Number} addressId 地址ID
 * @param {Object} addressData 地址数据
 * @returns {Promise}
 */
export function updateAddress(addressId, addressData) {
  return api.put(`/api/addresses/${addressId}`, addressData);
}

/**
 * 删除地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function deleteAddress(addressId) {
  return api.delete(`/api/addresses/${addressId}`);
}

/**
 * 设置默认地址
 * @param {Number} addressId 地址ID
 * @returns {Promise}
 */
export function setDefaultAddress(addressId) {
  return api.post(`/api/addresses/${addressId}/default`);
} 