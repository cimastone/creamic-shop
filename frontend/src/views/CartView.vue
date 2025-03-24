<template>
  <div class="cart">
    <h1>购物车</h1>
    
    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <p>购物车是空的</p>
      <router-link to="/products" class="continue-shopping">继续购物</router-link>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          <div class="item-details">
            <h3>{{ item.name }}</h3>
            <p class="item-price">¥{{ item.price }}</p>
          </div>
          <div class="item-quantity">
            <button @click="decreaseQuantity(item)" class="quantity-btn">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="increaseQuantity(item)" class="quantity-btn">+</button>
          </div>
          <div class="item-total">
            <p>¥{{ item.price * item.quantity }}</p>
          </div>
          <button @click="removeItem(item)" class="remove-btn">删除</button>
        </div>
      </div>

      <div class="cart-summary">
        <h3>订单摘要</h3>
        <div class="summary-item">
          <span>商品总数：</span>
          <span>{{ cartStore.itemCount }} 件</span>
        </div>
        <div class="summary-item">
          <span>商品总价：</span>
          <span>¥{{ cartStore.cartTotal }}</span>
        </div>
        <div class="summary-item">
          <span>运费：</span>
          <span>免运费</span>
        </div>
        <div class="summary-total">
          <span>总计：</span>
          <span>¥{{ cartStore.cartTotal }}</span>
        </div>
        <button @click="checkout" class="checkout-btn">结算</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useCartStore } from '../stores/cart'
import { useUserStore } from '../stores/user'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const userStore = useUserStore()

onMounted(() => {
  // 检查用户是否已登录
  if (!userStore.checkLoginStatus()) {
    // 未登录，重定向到登录页
    router.push({
      path: '/login',
      query: { redirect: '/cart' }
    })
    return
  }
  
  // 已登录，获取购物车数据
  cartStore.fetchCart()
})

const increaseQuantity = async (item) => {
  try {
    await cartStore.updateQuantity(item.id, item.quantity + 1)
  } catch (error) {
    alert('更新数量失败，请重试')
  }
}

const decreaseQuantity = async (item) => {
  if (item.quantity > 1) {
    try {
      await cartStore.updateQuantity(item.id, item.quantity - 1)
    } catch (error) {
      alert('更新数量失败，请重试')
    }
  }
}

const removeItem = async (item) => {
  if (confirm('确定要删除这个商品吗？')) {
    try {
      await cartStore.removeItem(item.id)
    } catch (error) {
      alert('删除商品失败，请重试')
    }
  }
}

const checkout = () => {
  router.push('/checkout')
}
</script>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.empty-cart {
  text-align: center;
  padding: 40px;
}

.continue-shopping {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.cart-items {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.cart-item {
  display: grid;
  grid-template-columns: 100px 2fr 1fr 1fr auto;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.item-image {
  width: 80px;
  height: 80px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-details h3 {
  margin: 0 0 5px;
  color: #333;
}

.item-price {
  color: #666;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.remove-btn {
  padding: 8px 12px;
  background: #ff4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cart-summary {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  height: fit-content;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #666;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  font-weight: bold;
  font-size: 1.2em;
}

.checkout-btn {
  width: 100%;
  padding: 15px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1.1em;
  cursor: pointer;
  margin-top: 20px;
}

.checkout-btn:hover {
  background-color: #45a049;
}
</style> 