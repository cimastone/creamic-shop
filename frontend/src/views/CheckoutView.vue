<template>
  <div class="checkout-container">
    <h1>确认订单</h1>
    
    <div class="checkout-content" v-if="cart.items.length > 0">
      <div class="address-section">
        <h2>收货地址</h2>
        <div v-if="useAddressSelector">
          <AddressSelector v-model="selectedAddressId" />
        </div>
        <div v-else class="address-form">
          <div class="form-group">
            <label>收货人</label>
            <input 
              type="text" 
              v-model="manualAddress.receiverName" 
              placeholder="请输入收货人姓名"
            />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input 
              type="text" 
              v-model="manualAddress.receiverPhone" 
              placeholder="请输入联系电话"
            />
          </div>
          <div class="form-group full-width">
            <label>详细地址</label>
            <input 
              type="text" 
              v-model="manualAddress.receiverAddress" 
              placeholder="请输入详细地址"
            />
          </div>
          <div class="form-actions">
            <button @click="toggleAddressMode" class="toggle-btn">切换到地址选择</button>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/order'

// 有条件地导入 AddressSelector 组件
let AddressSelector = null;
try {
  AddressSelector = require('@/components/AddressSelector.vue').default;
} catch (error) {
  console.error('加载地址选择器组件失败:', error);
}

const router = useRouter()
const cart = useCartStore()
const userStore = useUserStore()
const isSubmitting = ref(false)
const selectedAddressId = ref(null)
const useAddressSelector = ref(!!AddressSelector) // 根据组件是否加载成功来决定使用哪种模式

// 手动输入地址（备选方案）
const manualAddress = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})

const orderInfo = ref({
  paymentMethod: 'ALIPAY',
  note: ''
})

// 切换地址模式
const toggleAddressMode = () => {
  useAddressSelector.value = !useAddressSelector.value
}

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
  if (useAddressSelector.value) {
    return selectedAddressId.value !== null;
  } else {
    return (
      manualAddress.value.receiverName.trim() !== '' &&
      manualAddress.value.receiverPhone.trim() !== '' &&
      manualAddress.value.receiverAddress.trim() !== ''
    );
  }
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
    alert(useAddressSelector.value ? '请选择收货地址' : '请填写完整的收货信息');
    return
  }

  isSubmitting.value = true
  
  try {
    let orderData;
    let addressId = null;
    
    if (useAddressSelector.value) {
      // 使用地址选择器，直接获取选中的地址ID
      addressId = selectedAddressId.value;
    } else {
      // 使用手动输入的地址，先保存地址获取ID
      try {
        const addressData = {
          receiverName: manualAddress.value.receiverName,
          receiverPhone: manualAddress.value.receiverPhone,
          province: '未指定',  // 提供默认值
          city: '未指定',      // 提供默认值
          district: '未指定',  // 提供默认值
          detailAddress: manualAddress.value.receiverAddress,
          isDefault: false     // 不设为默认地址
        };
        
        console.log('创建新地址:', JSON.stringify(addressData));
        
        // 导入createAddress函数
        const { createAddress } = await import('@/api/address');
        const addressResponse = await createAddress(addressData);
        
        // 适配新的响应格式
        if ((addressResponse.data && addressResponse.data.code === 0) || addressResponse.code === 0) {
          // 从响应中提取地址ID
          let createdAddress;
          if (addressResponse.data && addressResponse.data.data) {
            createdAddress = addressResponse.data.data;
          } else if (addressResponse.data) {
            createdAddress = addressResponse.data;
          } else {
            createdAddress = addressResponse;
          }
          
          addressId = createdAddress.id;
          console.log('地址创建成功，ID:', addressId);
        } else {
          const errorMsg = (addressResponse.data && addressResponse.data.message) ||
                          addressResponse.message ||
                          '创建地址失败';
          throw new Error(errorMsg);
        }
      } catch (error) {
        console.error('创建地址失败:', error);
        
        // 检查是否是登录过期错误
        if (error.message.includes('登录已过期') || error.message.includes('未授权')) {
          // 保存当前页面状态
          const currentState = {
            manualAddress: manualAddress.value,
            orderInfo: orderInfo.value,
            cartItems: cart.items
          };
          localStorage.setItem('checkoutState', JSON.stringify(currentState));
          
          // 跳转到登录页面，并指定返回地址
          router.push(`/login?redirect=${encodeURIComponent('/checkout')}`);
          return;
        }
        
        // 其他错误，询问用户是否使用当前地址
        const useDirectOrderCreation = confirm(`创建地址失败: ${error.message || '未知错误'}\n\n您希望直接使用当前输入的地址信息提交订单吗？`);
        
        if (useDirectOrderCreation) {
          // 直接使用手动输入的地址信息创建订单
          orderData = {
            receiverName: manualAddress.value.receiverName,
            receiverPhone: manualAddress.value.receiverPhone,
            receiverAddress: manualAddress.value.receiverAddress,
            items: cart.items.map(item => ({
              productId: item.id,
              productName: item.name,
              productImage: item.image,
              productSpecs: item.specs || '默认规格',
              unitPrice: item.price,
              quantity: item.quantity
            }))
          };
        } else {
          isSubmitting.value = false;
          return;
        }
      }
    }
    
    // 如果使用地址ID方式或成功创建了地址
    if (addressId) {
      // 创建订单数据，使用获取到的addressId
      orderData = {
        addressId: addressId,
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
      
      const response = await createOrder(orderData)
      
      // 适配新的响应格式
      if ((response.data && response.data.code === 0) || response.code === 0) {
        // 清空购物车
        cart.clearCart()
        
        // 获取订单ID
        let orderId;
        if (response.data && response.data.data) {
          orderId = response.data.data.id;
        } else if (response.data) {
          orderId = response.data.id;
        } else {
          orderId = response.id;
        }
        
        // 跳转到支付页面或订单详情页
        router.push(`/orders/${orderId}`)
      } else {
        const errorMsg = (response.data && response.data.message) || 
                        response.message || 
                        '订单创建失败，请稍后重试';
        alert(errorMsg);
      }
    } else {
      alert('无法获取有效的地址ID');
    }
  } catch (error) {
    console.error('提交订单失败:', error)
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
  if (!useAddressSelector.value && userStore.isLoggedIn && userStore.userInfo) {
    manualAddress.value.receiverName = userStore.userInfo.name || ''
    manualAddress.value.receiverPhone = userStore.userInfo.phone || ''
    manualAddress.value.receiverAddress = userStore.userInfo.address || ''
  }

  // 检查是否有保存的结账状态
  const savedState = localStorage.getItem('checkoutState');
  if (savedState) {
    try {
      const state = JSON.parse(savedState);
      // 恢复地址信息
      if (state.manualAddress) {
        manualAddress.value = state.manualAddress;
      }
      // 恢复订单信息
      if (state.orderInfo) {
        orderInfo.value = state.orderInfo;
      }
      // 清除保存的状态
      localStorage.removeItem('checkoutState');
    } catch (error) {
      console.error('恢复结账状态失败:', error);
      localStorage.removeItem('checkoutState');
    }
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

/* 地址表单样式 */
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

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #666;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.toggle-btn {
  margin-top: 16px;
  padding: 8px 16px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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