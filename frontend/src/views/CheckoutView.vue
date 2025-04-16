<template>
  <div class="checkout-container">
    <h1>确认订单</h1>
    
    <div class="checkout-content" v-if="cart.items.length > 0">
      <div class="address-section">
        <h2>收货地址</h2>
        <AddressSelector v-model="selectedAddressId" />
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/order'
import AddressSelector from '@/components/AddressSelector.vue'

const router = useRouter()
const cart = useCartStore()
const userStore = useUserStore()
const isSubmitting = ref(false)
const selectedAddressId = ref(null)

const orderInfo = ref({
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
  return selectedAddressId.value !== null;
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
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=/checkout')
    return
  }

  // 检查是否选择了地址
  if (!selectedAddressId.value) {
    alert('请选择收货地址');
    return
  }

  isSubmitting.value = true
  
  try {
    // 准备订单数据（只使用地址ID模式）
    const orderData = {
      addressId: selectedAddressId.value,
      items: cart.items.map(item => ({
        productId: item.id,
        productName: item.name,
        productImage: item.image,
        productSpecs: item.specs || '默认规格',
        unitPrice: item.price,
        quantity: item.quantity
      }))
    };
    
    console.log('提交订单数据:', JSON.stringify(orderData));
    
    const response = await createOrder(orderData);
    console.log('订单创建响应:', response);
    
    // 使用标准化的响应处理
    if (response.success) {
      // 清空购物车
      cart.clearCart();
      
      // 获取订单ID
      const orderData = response.data;
      let orderId = orderData?.id;
      
      if (orderId) {
        // 跳转到订单详情页
        router.push(`/orders/${orderId}`);
      } else {
        // 如果找不到订单ID，跳转到订单列表页
        alert('订单创建成功，查看所有订单');
        router.push('/orders');
      }
    } else {
      // 处理失败情况
      alert(response.message || '订单创建失败，请稍后重试');
    }
  } catch (error) {
    console.error('提交订单失败:', error);
    
    // 显示错误信息
    alert(error.message || '订单创建失败，请稍后重试');
  } finally {
    isSubmitting.value = false;
  }
}

onMounted(() => {
  // 如果购物车为空，重定向到购物车页面
  if (cart.items.length === 0) {
    router.push('/cart')
    return
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

.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #fafafa;
  border-radius: 4px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 16px;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.product-specs {
  font-size: 14px;
  color: #666;
}

.product-price, .product-quantity, .product-subtotal {
  margin-left: 16px;
  font-size: 14px;
  color: #333;
}

.product-subtotal {
  font-weight: bold;
  min-width: 80px;
  text-align: right;
}

.payment-options {
  display: flex;
  gap: 16px;
}

.payment-option {
  padding: 12px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #40a9ff;
}

.payment-option.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.order-summary {
  padding: 20px;
  background: #fafafa;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.summary-row.total {
  margin-top: 16px;
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.checkout-actions {
  padding: 20px;
  display: flex;
  justify-content: space-between;
}

.btn-back, .btn-submit, .btn-shop {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-back {
  background-color: #f0f0f0;
  color: #333;
}

.btn-submit {
  background-color: #ff4d4f;
  color: white;
}

.btn-submit:disabled {
  background-color: #ffccc7;
  cursor: not-allowed;
}

.btn-shop {
  background-color: #1890ff;
  color: white;
}

.empty-cart {
  text-align: center;
  padding: 50px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-cart p {
  margin-bottom: 20px;
  font-size: 16px;
  color: #999;
}
</style> 