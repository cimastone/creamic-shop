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
  max-width: 1100px;
  margin: 0 auto;
  padding: 30px 20px;
}

h1 {
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
  font-weight: 600;
}

h2 {
  font-size: 20px;
  margin: 0 0 20px 0;
  color: #333;
  font-weight: 500;
}

.checkout-content {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.address-section, .products-section, .payment-section {
  padding: 30px;
  border-bottom: 1px solid #f0f0f0;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.product-image {
  width: 90px;
  height: 90px;
  object-fit: cover;
  margin-right: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 17px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #333;
}

.product-specs {
  font-size: 14px;
  color: #777;
}

.product-price, .product-quantity {
  margin-left: 20px;
  font-size: 15px;
  color: #555;
}

.product-subtotal {
  font-weight: 600;
  min-width: 90px;
  text-align: right;
  font-size: 16px;
  color: #e53935;
  margin-left: 20px;
}

.payment-options {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.payment-option {
  padding: 16px 24px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
}

.payment-option:hover {
  border-color: #4CAF50;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.payment-option.active {
  border-color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.05);
  box-shadow: 0 4px 10px rgba(76, 175, 80, 0.1);
}

.order-summary {
  padding: 25px 30px;
  background: #fafafa;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 15px;
  color: #555;
}

.summary-row.total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  font-size: 20px;
  font-weight: 600;
  color: #e53935;
}

.checkout-actions {
  padding: 25px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-back, .btn-submit, .btn-shop {
  padding: 14px 30px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-back {
  background-color: #f5f5f5;
  color: #555;
}

.btn-back:hover {
  background-color: #eeeeee;
}

.btn-submit {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

.btn-submit:hover {
  background-color: #43A047;
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.3);
  transform: translateY(-2px);
}

.btn-submit:disabled {
  background-color: #A5D6A7;
  box-shadow: none;
  cursor: not-allowed;
  transform: none;
}

.btn-shop {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

.btn-shop:hover {
  background-color: #43A047;
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.3);
  transform: translateY(-2px);
}

.empty-cart {
  text-align: center;
  padding: 80px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.empty-cart p {
  margin-bottom: 30px;
  font-size: 18px;
  color: #777;
}

@media (max-width: 768px) {
  .checkout-container {
    padding: 20px 15px;
  }
  
  h1 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .address-section, .products-section, .payment-section, .order-summary, .checkout-actions {
    padding: 20px;
  }
  
  .product-item {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .product-image {
    width: 70px;
    height: 70px;
    margin-right: 15px;
  }
  
  .product-subtotal {
    margin-left: auto;
  }
  
  .btn-back, .btn-submit, .btn-shop {
    padding: 12px 20px;
    font-size: 15px;
  }
}
</style> 