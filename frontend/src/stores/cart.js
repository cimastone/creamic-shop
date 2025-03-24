import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: []
  }),

  getters: {
    itemCount: (state) => {
      return state.items.reduce((total, item) => total + item.quantity, 0)
    },
    cartTotal: (state) => {
      return state.items.reduce((total, item) => total + item.price * item.quantity, 0)
    }
  },

  actions: {
    async fetchCart() {
      try {
        // 这里应该从后端API获取购物车数据
        // 暂时使用本地存储模拟
        const cartData = localStorage.getItem('cart')
        if (cartData) {
          this.items = JSON.parse(cartData)
        }
      } catch (error) {
        console.error('获取购物车数据失败:', error)
        throw error
      }
    },

    async addToCart(product, quantity = 1) {
      try {
        // 确保传入的是完整的商品信息
        if (!product || typeof product === 'number') {
          throw new Error('需要完整的商品信息')
        }

        // 查找商品是否已在购物车中
        const existingItem = this.items.find(item => item.id === product.id)
        
        if (existingItem) {
          // 如果商品已存在，更新数量
          existingItem.quantity += quantity
        } else {
          // 添加新商品到购物车
          this.items.push({
            id: product.id,
            name: product.name,
            price: product.price,
            image: product.image,
            quantity: quantity
          })
        }

        // 保存到本地存储
        localStorage.setItem('cart', JSON.stringify(this.items))
        return true
      } catch (error) {
        console.error('添加到购物车失败:', error)
        throw error
      }
    },

    async updateQuantity(productId, quantity) {
      try {
        const item = this.items.find(item => item.id === productId)
        if (item) {
          item.quantity = quantity
          localStorage.setItem('cart', JSON.stringify(this.items))
        }
      } catch (error) {
        console.error('更新商品数量失败:', error)
        throw error
      }
    },

    async removeItem(productId) {
      try {
        const index = this.items.findIndex(item => item.id === productId)
        if (index !== -1) {
          this.items.splice(index, 1)
          localStorage.setItem('cart', JSON.stringify(this.items))
        }
      } catch (error) {
        console.error('删除商品失败:', error)
        throw error
      }
    },

    clearCart() {
      this.items = []
      localStorage.removeItem('cart')
    }
  }
}) 