<template>
  <div class="checkout-container">
    <h1>确认订单</h1>
    
    <div class="checkout-content" v-if="cart.items.length > 0">
      <div class="address-section">
        <h2>收货地址</h2>
        <div class="address-form">
          <div class="form-group">
            <label>收货人</label>
            <input 
              type="text" 
              v-model="orderInfo.recipientName" 
              placeholder="请输入收货人姓名"
            />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input 
              type="text" 
              v-model="orderInfo.recipientPhone" 
              placeholder="请输入联系电话"
            />
          </div>
          <div class="form-group full-width">
            <label>详细地址</label>
            <input 
              type="text" 
              v-model="orderInfo.recipientAddress" 
              placeholder="请输入详细地址"
            />
          </div>
        </div>
      </div>
      
      <div class="products-section">
        <h2>商品信息</h2>
        <div class="product-list">
          <div class="product-item" v-for="item in cart.items" :key="item.id">
            <img :src="item.image" alt="商品图片" class="product-image" />
            <div class="product-info">
              <div class="product-name">{{ item.name }}</div>
              <div class="product-specs">{{ item.specs || '默认规格' }}</div>
            </div>
            <div class="product-price">¥{{ item.price }}</div>
            <div class="product-quantity">× {{ item.quantity }}</div>
            <div class="product-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
      </div>
      
      <div class="payment-section">
        <h2>支付方式</h2>
        <div class="payment-options">
          <div 
            class="payment-option" 
            :class="{ active: orderInfo.paymentMethod === 'ALIPAY' }"
            @click="orderInfo.paymentMethod = 'ALIPAY'"
          >
            <span>支付宝</span>
          </div>
          <div 
            class="payment-option" 
            :class="{ active: orderInfo.paymentMethod === 'WECHAT' }"
            @click="orderInfo.paymentMethod = 'WECHAT'"
          >
            <span>微信支付</span>
          </div>
          <div 
            class="payment-option" 
            :class="{ active: orderInfo.paymentMethod === 'CREDIT_CARD' }"
            @click="orderInfo.paymentMethod = 'CREDIT_CARD'"
          >
            <span>银行卡</span>
          </div>
        </div>
      </div>
      
      <div class="order-summary">
        <div class="summary-row">
          <span>商品总价：</span>
          <span>¥{{ totalAmount }}</span>
        </div>
        <div class="summary-row">
          <span>运费：</span>
          <span>¥{{ shippingFee }}</span>
        </div>
        <div class="summary-row total">
          <span>应付金额：</span>
          <span>¥{{ (totalAmount + shippingFee).toFixed(2) }}</span>
        </div>
      </div>
      
      <div class="checkout-actions">
        <button class="btn-back" @click="goBack">返回购物车</button>
        <button 
          class="btn-submit" 
          :disabled="isSubmitting || !isFormValid" 
          @click="submitOrder"
        >
          {{ isSubmitting ? '提交中...' : '提交订单' }}
        </button>
      </div>
    </div>
    
    <div class="empty-cart" v-else>
      <p>您的购物车是空的，请先添加商品</p>
      <button class="btn-shop" @click="goToShop">去购物</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/order'

const router = useRouter()
const cart = useCartStore()
const userStore = useUserStore()
const isSubmitting = ref(false)

const orderInfo = ref({
  recipientName: '',
  recipientPhone: '',
  recipientAddress: '',
  paymentMethod: 'ALIPAY',
  note: ''
})

// 获取商品总价
const totalAmount = computed(() => {
  return cart.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 运费（示例：总价满100免运费，否则收取10元运费）
const shippingFee = computed(() => {
  return totalAmount.value >= 100 ? 0 : 10
})

// 表单验证
const isFormValid = computed(() => {
  return (
    orderInfo.value.recipientName.trim() !== '' &&
    orderInfo.value.recipientPhone.trim() !== '' &&
    orderInfo.value.recipientAddress.trim() !== ''
  )
})

// 返回购物车
const goBack = () => {
  router.push('/cart')
}

// 去购物
const goToShop = () => {
  router.push('/products')
}

// 提交订单
const submitOrder = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=/checkout')
    return
  }

  if (!isFormValid.value) {
    return
  }

  isSubmitting.value = true
  
  try {
    const orderData = {
      userId: userStore.userInfo.id,
      orderItems: cart.items.map(item => ({
        productId: item.id,
        productName: item.name,
        productImage: item.image,
        unitPrice: item.price,
        quantity: item.quantity,
        specs: item.specs || '默认规格'
      })),
      recipientName: orderInfo.value.recipientName,
      recipientPhone: orderInfo.value.recipientPhone,
      recipientAddress: orderInfo.value.recipientAddress,
      totalAmount: totalAmount.value + shippingFee.value,
      paymentMethod: orderInfo.value.paymentMethod,
      note: orderInfo.value.note || ''
    }
    
    const order = await createOrder(orderData)
    
    // 清空购物车
    cart.clearCart()
    
    // 跳转到支付页面或订单详情页
    router.push(`/orders/${order.id}`)
  } catch (error) {
    console.error('创建订单失败:', error)
    alert('订单创建失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  // 如果购物车为空，重定向到购物车页面
  if (cart.items.length === 0) {
    router.push('/cart')
    return
  }
  
  // 如果用户已登录，自动填充用户信息
  if (userStore.isLoggedIn && userStore.userInfo) {
    orderInfo.value.recipientName = userStore.userInfo.name || ''
    orderInfo.value.recipientPhone = userStore.userInfo.phone || ''
    orderInfo.value.recipientAddress = userStore.userInfo.address || ''
  }
})
</script>

<style scoped>
.checkout-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 24px;
  color: #333;
}

h2 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #333;
}

.checkout-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.address-section, .products-section, .payment-section {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.address-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.form-group {
  width: calc(50% - 8px);
}

.form-group.full-width {
  width: 100%;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #666;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

input:focus {
  border-color: #1677ff;
  outline: none;
}

.product-list {
  margin-bottom: 20px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 16px;
}

.product-info {
  flex: 1;
}

.product-name {
  margin-bottom: 8px;
  font-size: 16px;
}

.product-specs {
  color: #999;
  font-size: 14px;
}

.product-price, .product-quantity, .product-subtotal {
  width: 100px;
  text-align: center;
}

.product-subtotal {
  font-weight: 500;
  color: #ff4d4f;
}

.payment-options {
  display: flex;
  gap: 16px;
}

.payment-option {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option img {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.payment-option.active {
  border-color: #1677ff;
  color: #1677ff;
}

.order-summary {
  padding: 20px;
  background: #f9f9f9;
}

.summary-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.summary-row span:first-child {
  margin-right: 16px;
  color: #666;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 500;
  margin-top: 8px;
}

.summary-row.total span:last-child {
  color: #ff4d4f;
}

.checkout-actions {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

button {
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.btn-back {
  background: white;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn-back:hover {
  color: #1677ff;
  border-color: #1677ff;
}

.btn-submit {
  background: #1677ff;
  color: white;
}

.btn-submit:hover {
  background: #0958d9;
}

.btn-submit:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

.empty-cart {
  text-align: center;
  padding: 60px 0;
  color: #999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-image {
  width: 120px;
  margin-bottom: 16px;
}

.btn-shop {
  margin-top: 16px;
  background: #1677ff;
  color: white;
}
</style> 