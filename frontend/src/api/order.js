import { orderApi } from './index'

/**
 * 创建订单
 * @param {Object} orderData 订单数据
 * @returns {Promise}
 */
export function createOrder(orderData) {
  return orderApi.post('/api/orders', orderData)
}

/**
 * 获取订单详情
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function getOrderDetail(orderId) {
  return orderApi.get(`/api/orders/${orderId}`)
}

/**
 * 获取用户所有订单
 * @returns {Promise}
 */
export function getUserOrders() {
  return orderApi.get('/api/orders')
}

/**
 * 获取用户特定状态的订单
 * @param {String} status 订单状态
 * @returns {Promise}
 */
export function getUserOrdersByStatus(status) {
  return orderApi.get(`/api/orders/status/${status}`)
}

/**
 * 支付订单
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function payOrder(orderId) {
  return orderApi.post(`/api/orders/${orderId}/pay`)
}

/**
 * 取消订单
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function cancelOrder(orderId) {
  return orderApi.post(`/api/orders/${orderId}/cancel`)
}

/**
 * 完成订单
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function completeOrder(orderId) {
  return orderApi.post(`/api/orders/${orderId}/complete`)
}

/**
 * 获取订单物流信息
 * @param {Number} orderId 订单ID
 * @returns {Promise}
 */
export function getOrderLogistics(orderId) {
  return orderApi.get(`/api/orders/${orderId}/logistics`)
}