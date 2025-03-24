const BASE_URL = 'http://localhost:8080/api';

// 封装请求方法
const request = (url, method, data) => {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: {
        'content-type': 'application/json',
        'Authorization': wx.getStorageSync('token') || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          // 未授权，跳转到登录页
          wx.navigateTo({
            url: '/pages/login/index'
          });
          reject(res.data);
        } else {
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};

// 商品相关API
export const productApi = {
  // 获取商品列表
  getProducts: (params) => {
    return request('/products', 'GET', params);
  },
  // 获取商品详情
  getProductDetail: (id) => {
    return request(`/products/${id}`, 'GET');
  },
  // 获取商品分类
  getCategories: () => {
    return request('/categories', 'GET');
  }
};

// 购物车相关API
export const cartApi = {
  // 获取购物车列表
  getCartItems: () => {
    return request('/cart', 'GET');
  },
  // 添加商品到购物车
  addToCart: (data) => {
    return request('/cart', 'POST', data);
  },
  // 更新购物车商品数量
  updateCartItem: (id, data) => {
    return request(`/cart/${id}`, 'PUT', data);
  },
  // 删除购物车商品
  removeCartItem: (id) => {
    return request(`/cart/${id}`, 'DELETE');
  }
};

// 订单相关API
export const orderApi = {
  // 获取订单列表
  getOrders: (params) => {
    return request('/orders', 'GET', params);
  },
  // 获取订单详情
  getOrderDetail: (id) => {
    return request(`/orders/${id}`, 'GET');
  },
  // 创建订单
  createOrder: (data) => {
    return request('/orders', 'POST', data);
  },
  // 取消订单
  cancelOrder: (id) => {
    return request(`/orders/${id}/cancel`, 'POST');
  }
};

// 支付相关API
export const paymentApi = {
  // 获取支付参数
  getPaymentParams: (orderId) => {
    return request(`/payments/${orderId}`, 'GET');
  },
  // 确认支付
  confirmPayment: (data) => {
    return request('/payments/confirm', 'POST', data);
  }
};

// 物流相关API
export const logisticsApi = {
  // 获取物流信息
  getLogisticsInfo: (orderId) => {
    return request(`/logistics/${orderId}`, 'GET');
  }
};

// 用户相关API
export const userApi = {
  // 用户登录
  login: (data) => {
    return request('/auth/login', 'POST', data);
  },
  // 获取用户信息
  getUserInfo: () => {
    return request('/users/me', 'GET');
  },
  // 更新用户信息
  updateUserInfo: (data) => {
    return request('/users/me', 'PUT', data);
  }
}; 