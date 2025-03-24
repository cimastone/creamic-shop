<template>
  <div class="order-detail-container">
    <div class="page-header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span> 返回订单列表
      </button>
      <h1>订单详情</h1>
    </div>

    <div class="loading" v-if="loading">加载中...</div>
    
    <div class="order-content" v-else-if="order">
      <div class="order-status-card">
        <div class="status-info">
          <div class="status-text">{{ getStatusText(order.status) }}</div>
          <div class="status-desc">{{ getStatusDescription(order.status) }}</div>
        </div>
      </div>
      
      <div class="order-section">
        <h2 class="section-title">订单信息</h2>
        <div class="info-row">
          <div class="info-label">订单编号：</div>
          <div class="info-value">{{ order.orderNumber }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">创建时间：</div>
          <div class="info-value">{{ formatDate(order.createTime) }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">支付时间：</div>
          <div class="info-value">{{ order.payTime ? formatDate(order.payTime) : '未支付' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">订单状态：</div>
          <div class="info-value status-value">{{ getStatusText(order.status) }}</div>
        </div>
      </div>

      <div class="order-section">
        <h2 class="section-title">收货信息</h2>
        <div class="info-row">
          <div class="info-label">收货人：</div>
          <div class="info-value">{{ order.recipientName }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">联系电话：</div>
          <div class="info-value">{{ order.recipientPhone }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">收货地址：</div>
          <div class="info-value">{{ order.recipientAddress }}</div>
        </div>
      </div>

      <div class="order-section">
        <h2 class="section-title">商品信息</h2>
        <div class="product-list">
          <div class="product-item" v-for="item in order.orderItems" :key="item.id">
            <img :src="item.productImage" alt="商品图片" class="product-image" />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-specs">{{ item.specs || '默认规格' }}</div>
            </div>
            <div class="product-price">¥{{ item.unitPrice }}</div>
            <div class="product-quantity">× {{ item.quantity }}</div>
            <div class="product-subtotal">¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
        
        <div class="order-summary">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>¥{{ order.shippingFee || '0.00' }}</span>
          </div>
          <div class="summary-row total">
            <span>实付金额：</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>

      <div class="order-actions">
        <button 
          v-if="order.status === 'PENDING_PAYMENT'" 
          class="btn-pay"
          @click="payOrder"
        >
          去支付
        </button>
        <button 
          v-if="order.status === 'PENDING_PAYMENT'" 
          class="btn-cancel"
          @click="cancelOrder"
        >
          取消订单
        </button>
        <button 
          v-if="order.status === 'SHIPPED'" 
          class="btn-confirm"
          @click="completeOrder"
        >
          确认收货
        </button>
      </div>
    </div>
    
    <div class="not-found" v-else>
      <p>订单不存在或已被删除</p>
      <button class="btn-back" @click="goBack">返回订单列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, payOrder as apiPayOrder, cancelOrder as apiCancelOrder, completeOrder as apiCompleteOrder } from '@/api/order'
import { formatDate as formatDateUtil } from '@/utils/dateUtils'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

// 格式化日期
const formatDate = (dateString) => {
  return formatDateUtil(dateString, 'YYYY-MM-DD HH:mm:ss')
}

// 获取状态文字
const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待付款',
    'PAID': '已付款',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

// 获取状态描述
const getStatusDescription = (status) => {
  const descMap = {
    'PENDING_PAYMENT': '请在24小时内完成支付，超时订单将自动取消',
    'PAID': '我们将尽快安排发货，请耐心等待',
    'SHIPPED': '商品已发出，请注意查收',
    'COMPLETED': '订单已完成，感谢您的购买',
    'CANCELLED': '订单已取消'
  }
  return descMap[status] || ''
}

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true
    const orderId = route.params.id
    const res = await getOrderDetail(orderId)
    order.value = res
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 支付订单
const payOrder = async () => {
  try {
    await apiPayOrder(order.value.id)
    fetchOrderDetail()
  } catch (error) {
    console.error('支付失败:', error)
  }
}

// 取消订单
const cancelOrder = async () => {
  try {
    await apiCancelOrder(order.value.id)
    fetchOrderDetail()
  } catch (error) {
    console.error('取消订单失败:', error)
  }
}

// 确认收货
const completeOrder = async () => {
  try {
    await apiCompleteOrder(order.value.id)
    fetchOrderDetail()
  } catch (error) {
    console.error('确认收货失败:', error)
  }
}

// 返回订单列表
const goBack = () => {
  router.push('/orders')
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.back-button {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #666;
  margin-right: 16px;
  font-size: 14px;
}

.back-icon {
  margin-right: 4px;
}

h1 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.loading {
  text-align: center;
  padding: 100px 0;
  color: #999;
  font-size: 16px;
}

.order-status-card {
  background: linear-gradient(135deg, #1677ff, #0958d9);
  border-radius: 8px;
  padding: 20px;
  color: white;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 14px;
  opacity: 0.8;
}

.order-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  color: #333;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
}

.info-label {
  width: 100px;
  color: #999;
}

.info-value {
  flex: 1;
  color: #333;
}

.status-value {
  color: #1677ff;
  font-weight: 500;
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

.order-summary {
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
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

.order-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.order-actions button {
  margin-left: 12px;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  transition: background 0.3s;
}

.btn-pay {
  background: #1677ff;
  color: white;
}

.btn-pay:hover {
  background: #0958d9;
}

.btn-cancel {
  background: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn-cancel:hover {
  color: #1677ff;
  border-color: #1677ff;
}

.btn-confirm {
  background: #52c41a;
  color: white;
}

.btn-confirm:hover {
  background: #389e0d;
}

.not-found {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.btn-back {
  margin-top: 16px;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  background: #1677ff;
  color: white;
  border: none;
}
</style> 