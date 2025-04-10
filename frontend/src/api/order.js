import { orderApi } from './index'

/**
 * 创建订单
 * @param {Object} orderData 订单数据，支持两种形式：
 * 方式1（使用地址ID）:
 * @param {Number} orderData.addressId 收货地址ID
 * @param {Array} orderData.items 订单商品列表
 * 
 * 方式2（直接提供收货人信息）:
 * @param {String} orderData.receiverName 收货人姓名
 * @param {String} orderData.receiverPhone 收货人电话
 * @param {String} orderData.receiverAddress 收货人地址
 * @param {Array} orderData.items 订单商品列表
 * @returns {Promise}
 */
export function createOrder(orderData) {
  console.log("提交订单数据:", JSON.stringify(orderData));
  return orderApi.post('/api/orders', orderData, {
    headers: {
      'Accept': 'application/json, text/plain, */*'
    }
  });
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