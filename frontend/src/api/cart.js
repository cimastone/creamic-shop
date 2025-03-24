import api from './index'

export const cartApi = {
  // 获取购物车
  getCart() {
    return api.get('/cart')
  },

  // 添加商品到购物车
  addToCart(productId, quantity) {
    return api.post('/cart', { productId, quantity })
  },

  // 更新购物车商品数量
  updateCartItem(itemId, quantity) {
    return api.put(`/cart/${itemId}`, { quantity })
  },

  // 从购物车移除商品
  removeFromCart(itemId) {
    return api.delete(`/cart/${itemId}`)
  },

  // 清空购物车
  clearCart() {
    return api.delete('/cart')
  }
} 