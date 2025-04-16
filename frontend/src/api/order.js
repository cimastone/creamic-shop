import { orderApi } from './index'

/**
 * 创建订单
 * @param {Object} orderData 订单数据
 * @param {Number} orderData.addressId 收货地址ID (必填)
 * @param {Array} orderData.items 订单商品列表
 * @returns {Promise}
 */
export function createOrder(orderData) {
  if (!orderData.addressId) {
    return Promise.reject({
      success: false,
      message: '收货地址ID是必填的',
      data: null
    });
  }
  
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
  return new Promise((resolve, reject) => {
    orderApi.get(`/api/orders/${orderId}`)
      .then(response => {
        console.log('获取订单详情原始响应:', response);
        
        let orderData = null;
        
        if (response && typeof response === 'object') {
          if (response.id) {
            orderData = response;
          } else if (response.data && response.data.id) {
            orderData = response.data;
          } else if (response.data && response.data.data && response.data.data.id) {
            orderData = response.data.data;
          } else {
            console.warn('未能识别订单详情数据格式:', response);
            orderData = null;
          }
        }
        
        console.log('解析后的订单详情:', orderData);
        resolve(orderData);
      })
      .catch(error => {
        console.error('获取订单详情失败:', error);
        reject(error);
      });
  });
}

/**
 * 获取用户所有订单
 * @returns {Promise}
 */
export function getUserOrders() {
  return new Promise((resolve, reject) => {
    orderApi.get('/api/orders')
      .then(response => {
        console.log('获取订单列表原始响应:', response);
        
        let ordersData = [];
        
        if (Array.isArray(response)) {
          ordersData = response;
        } else if (response && Array.isArray(response.data)) {
          ordersData = response.data;
        } else if (response && response.data && Array.isArray(response.data.data)) {
          ordersData = response.data.data;
        } else {
          console.warn('未能识别订单列表数据格式:', response);
          ordersData = [];
        }
        
        console.log('解析后的订单列表:', ordersData);
        resolve(ordersData);
      })
      .catch(error => {
        console.error('获取订单列表失败:', error);
        reject(error);
      });
  });
}

/**
 * 获取用户特定状态的订单
 * @param {String} status 订单状态
 * @returns {Promise}
 */
export function getUserOrdersByStatus(status) {
  return new Promise((resolve, reject) => {
    orderApi.get(`/api/orders/status/${status}`)
      .then(response => {
        console.log('获取特定状态订单列表原始响应:', response);
        
        let ordersData = [];
        
        if (Array.isArray(response)) {
          ordersData = response;
        } else if (response && Array.isArray(response.data)) {
          ordersData = response.data;
        } else if (response && response.data && Array.isArray(response.data.data)) {
          ordersData = response.data.data;
        } else {
          console.warn('未能识别特定状态订单列表数据格式:', response);
          ordersData = [];
        }
        
        console.log('解析后的特定状态订单列表:', ordersData);
        resolve(ordersData);
      })
      .catch(error => {
        console.error('获取特定状态订单列表失败:', error);
        reject(error);
      });
  });
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