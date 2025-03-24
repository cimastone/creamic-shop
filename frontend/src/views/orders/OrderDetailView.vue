<template>
  <div class="order-detail">
    <div class="back-link">
      <router-link to="/orders">&larr; 返回订单列表</router-link>
    </div>
    
    <h1>订单详情</h1>
    
    <!-- 加载中状态 -->
    <div v-if="loading" class="loading">
      <p>正在加载订单详情...</p>
    </div>
    
    <!-- 订单不存在 -->
    <div v-else-if="!order" class="no-order">
      <p>订单不存在或已被删除</p>
      <router-link to="/orders" class="back-btn">返回订单列表</router-link>
    </div>
    
    <!-- 订单详情内容 -->
    <div v-else class="order-content">
      <!-- 订单状态概览 -->
      <div class="order-status-card">
        <div class="status-header">
          <div class="status-label">
            <span>订单状态</span>
          </div>
          <div class="current-status">
            <span :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
          </div>
        </div>
        
        <div class="status-steps">
          <div class="step" :class="{ 'active': isStepActive('ORDER_CREATED'), 'completed': isStepCompleted('ORDER_CREATED') }">
            <div class="step-icon">1</div>
            <div class="step-info">
              <p class="step-name">下单成功</p>
              <p v-if="order.createdAt" class="step-time">{{ formatDate(order.createdAt) }}</p>
            </div>
          </div>
          
          <div class="step-line"></div>
          
          <div class="step" :class="{ 'active': isStepActive('PAYMENT_COMPLETED'), 'completed': isStepCompleted('PAYMENT_COMPLETED') }">
            <div class="step-icon">2</div>
            <div class="step-info">
              <p class="step-name">支付完成</p>
              <p v-if="order.paymentTime" class="step-time">{{ formatDate(order.paymentTime) }}</p>
            </div>
          </div>
          
          <div class="step-line"></div>
          
          <div class="step" :class="{ 'active': isStepActive('ORDER_SHIPPED'), 'completed': isStepCompleted('ORDER_SHIPPED') }">
            <div class="step-icon">3</div>
            <div class="step-info">
              <p class="step-name">商品出库</p>
              <p v-if="order.shipTime" class="step-time">{{ formatDate(order.shipTime) }}</p>
            </div>
          </div>
          
          <div class="step-line"></div>
          
          <div class="step" :class="{ 'active': isStepActive('ORDER_COMPLETED'), 'completed': isStepCompleted('ORDER_COMPLETED') }">
            <div class="step-icon">4</div>
            <div class="step-info">
              <p class="step-name">订单完成</p>
              <p v-if="order.completedTime" class="step-time">{{ formatDate(order.completedTime) }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 订单信息卡片 -->
      <div class="order-card">
        <h2>订单信息</h2>
        <div class="order-info-grid">
          <div class="info-item">
            <label>订单编号</label>
            <span>{{ order.orderNumber }}</span>
          </div>
          <div class="info-item">
            <label>下单时间</label>
            <span>{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>支付方式</label>
            <span>{{ getPaymentMethod(order.paymentMethod) }}</span>
          </div>
          <div class="info-item">
            <label>支付状态</label>
            <span :class="{ 'text-success': order.paymentStatus === 'PAID', 'text-warning': order.paymentStatus === 'UNPAID' }">
              {{ order.paymentStatus === 'PAID' ? '已支付' : '未支付' }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- 物流信息卡片 -->
      <div v-if="order.status !== 'UNPAID' && order.status !== 'CANCELLED'" class="order-card">
        <h2>物流信息</h2>
        <div v-if="logistics" class="logistics-info">
          <div class="logistics-header">
            <div class="info-item">
              <label>物流公司</label>
              <span>{{ logistics.company }}</span>
            </div>
            <div class="info-item">
              <label>快递单号</label>
              <span>{{ logistics.trackingNumber }}</span>
              <button class="copy-btn" @click="copyTrackingNumber(logistics.trackingNumber)">复制</button>
            </div>
          </div>
          
          <div class="logistics-timeline">
            <div v-if="logistics.tracks && logistics.tracks.length > 0" class="tracking-events">
              <div v-for="(track, index) in logistics.tracks" :key="index" class="tracking-event">
                <div class="event-time">
                  <p class="date">{{ formatEventDate(track.eventTime) }}</p>
                  <p class="time">{{ formatEventTime(track.eventTime) }}</p>
                </div>
                <div class="event-info">
                  <div class="event-dot" :class="{ 'first': index === 0 }"></div>
                  <div v-if="index !== logistics.tracks.length - 1" class="event-line"></div>
                  <p class="event-content">{{ track.content }}</p>
                  <p v-if="track.location" class="event-location">{{ track.location }}</p>
                </div>
              </div>
            </div>
            <div v-else class="no-tracking">
              <p>暂无物流跟踪信息</p>
            </div>
          </div>
        </div>
        <div v-else-if="loadingLogistics" class="loading-logistics">
          <p>正在加载物流信息...</p>
        </div>
        <div v-else class="no-logistics">
          <p>暂无物流信息</p>
        </div>
      </div>
      
      <!-- 收货信息卡片 -->
      <div class="order-card">
        <h2>收货信息</h2>
        <div class="address-info">
          <p><strong>收货人：</strong>{{ order.recipientName }}</p>
          <p><strong>联系电话：</strong>{{ order.recipientPhone }}</p>
          <p><strong>收货地址：</strong>{{ order.recipientAddress }}</p>
        </div>
      </div>
      
      <!-- 商品信息卡片 -->
      <div class="order-card">
        <h2>商品信息</h2>
        <div class="product-list">
          <div v-for="item in order.orderItems" :key="item.id" class="product-item">
            <div class="product-image">
              <img :src="item.productImage" :alt="item.productName">
            </div>
            <div class="product-info">
              <h3>{{ item.productName }}</h3>
              <p class="product-price">¥{{ item.price }}</p>
            </div>
            <div class="product-quantity">
              <span>x{{ item.quantity }}</span>
            </div>
            <div class="product-total">
              <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 价格汇总 -->
        <div class="price-summary">
          <div class="summary-item">
            <span>商品总价</span>
            <span>¥{{ order.productTotal }}</span>
          </div>
          <div class="summary-item">
            <span>运费</span>
            <span>{{ order.shippingFee > 0 ? `¥${order.shippingFee}` : '免运费' }}</span>
          </div>
          <div v-if="order.discount > 0" class="summary-item">
            <span>优惠</span>
            <span>-¥{{ order.discount }}</span>
          </div>
          <div class="summary-total">
            <span>实付款</span>
            <span>¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>
      
      <!-- 订单操作 -->
      <div class="order-actions">
        <template v-if="order.status === 'UNPAID'">
          <button class="pay-btn" @click="handlePayOrder">去支付</button>
          <button class="cancel-btn" @click="handleCancelOrder">取消订单</button>
        </template>
        
        <template v-if="order.status === 'SHIPPED'">
          <button class="confirm-btn" @click="handleCompleteOrder">确认收货</button>
        </template>
        
        <template v-if="order.status === 'COMPLETED'">
          <button class="review-btn" @click="reviewOrder">评价订单</button>
        </template>
        
        <router-link to="/orders" class="back-btn">返回订单列表</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, payOrder as apiPayOrder, cancelOrder as apiCancelOrder, completeOrder as apiCompleteOrder, getOrderLogistics } from '@/api/order'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const order = ref(null)
const logistics = ref(null)
const loading = ref(true)
const loadingLogistics = ref(false)

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true
    const orderId = route.params.id
    const res = await getOrderDetail(orderId)
    order.value = res
    
    // 如果有物流信息，加载物流详情
    if (order.value.status === 'SHIPPED' || order.value.status === 'COMPLETED') {
      fetchLogistics()
    }
  } catch (error) {
    console.error('获取订单详情失败', error)
  } finally {
    loading.value = false
  }
}

// 获取物流信息
const fetchLogistics = async () => {
  loadingLogistics.value = true
  try {
    const orderId = route.params.id
    const res = await getOrderLogistics(orderId)
    logistics.value = res
  } catch (error) {
    console.error('获取物流信息失败', error)
  } finally {
    loadingLogistics.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 格式化物流事件日期
const formatEventDate = (dateString) => {
  if (!dateString) return '-'
  
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

// 格式化物流事件时间
const formatEventTime = (dateString) => {
  if (!dateString) return '-'
  
  const date = new Date(dateString)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取订单状态类
const getStatusClass = (status) => {
  switch(status) {
    case 'UNPAID': return 'status-unpaid'
    case 'PAID': return 'status-paid'
    case 'SHIPPED': return 'status-shipped'
    case 'COMPLETED': return 'status-completed'
    case 'CANCELLED': return 'status-cancelled'
    default: return ''
  }
}

// 获取订单状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'UNPAID': return '待付款'
    case 'PAID': return '待发货'
    case 'SHIPPED': return '待收货'
    case 'COMPLETED': return '已完成'
    case 'CANCELLED': return '已取消'
    default: return '未知状态'
  }
}

// 获取支付方式文本
const getPaymentMethod = (method) => {
  switch(method) {
    case 'WECHAT': return '微信支付'
    case 'ALIPAY': return '支付宝'
    case 'COD': return '货到付款'
    default: return '未知方式'
  }
}

// 判断是否是当前步骤
const isStepActive = (step) => {
  if (!order.value) return false
  
  switch(step) {
    case 'ORDER_CREATED':
      return order.value.status === 'UNPAID'
    case 'PAYMENT_COMPLETED':
      return order.value.status === 'PAID'
    case 'ORDER_SHIPPED':
      return order.value.status === 'SHIPPED'
    case 'ORDER_COMPLETED':
      return order.value.status === 'COMPLETED'
    default:
      return false
  }
}

// 判断步骤是否已完成
const isStepCompleted = (step) => {
  if (!order.value) return false
  
  const statusOrder = ['UNPAID', 'PAID', 'SHIPPED', 'COMPLETED']
  const currentStatusIndex = statusOrder.indexOf(order.value.status)
  
  if (currentStatusIndex === -1 || order.value.status === 'CANCELLED') return false
  
  switch(step) {
    case 'ORDER_CREATED':
      return currentStatusIndex >= 0
    case 'PAYMENT_COMPLETED':
      return currentStatusIndex >= 1
    case 'ORDER_SHIPPED':
      return currentStatusIndex >= 2
    case 'ORDER_COMPLETED':
      return currentStatusIndex >= 3
    default:
      return false
  }
}

// 复制快递单号
const copyTrackingNumber = (trackingNumber) => {
  navigator.clipboard.writeText(trackingNumber)
    .then(() => {
      alert('快递单号已复制到剪贴板')
    })
    .catch(err => {
      console.error('复制失败:', err)
      alert('复制失败，请手动复制')
    })
}

// 支付订单
const handlePayOrder = async () => {
  const orderId = route.params.id
  if (!orderId) return
  
  try {
    await apiPayOrder(orderId)
    await fetchOrderDetail() // 刷新订单详情
  } catch (error) {
    console.error('支付失败', error)
  }
}

// 取消订单
const handleCancelOrder = async () => {
  const orderId = route.params.id
  if (!orderId) return
  
  if (!confirm('确定要取消这个订单吗？')) return
  
  try {
    await apiCancelOrder(orderId)
    await fetchOrderDetail() // 刷新订单详情
  } catch (error) {
    console.error('取消订单失败', error)
    alert('取消订单失败，请重试')
  }
}

// 确认收货
const handleCompleteOrder = async () => {
  const orderId = route.params.id
  if (!orderId) return
  
  if (!confirm('确认已收到商品？')) return
  
  try {
    await apiCompleteOrder(orderId)
    await fetchOrderDetail()
  } catch (error) {
    console.error('确认收货失败', error)
    alert('确认收货失败，请重试')
  }
}

// 评价订单
const reviewOrder = () => {
  router.push(`/orders/${route.params.id}/review`)
}

onMounted(() => {
  // 检查用户是否已登录
  if (!userStore.checkLoginStatus()) {
    return
  }
  
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1, h2 {
  color: #333;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
}

h2 {
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.back-link {
  margin-bottom: 20px;
}

.back-link a {
  color: #4a6572;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.back-link a:hover {
  text-decoration: underline;
}

.loading, .no-order {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card, .order-status-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  padding: 20px;
}

/* 订单状态卡片 */
.order-status-card {
  margin-bottom: 10px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.status-label {
  font-size: 18px;
  font-weight: bold;
}

.current-status {
  font-size: 18px;
  font-weight: bold;
}

.status-steps {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 22%;
  position: relative;
}

.step-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #e0e0e0;
  color: #757575;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  margin-bottom: 10px;
}

.step.active .step-icon {
  background-color: #4a6572;
  color: white;
}

.step.completed .step-icon {
  background-color: #4CAF50;
  color: white;
}

.step-line {
  flex: 1;
  height: 2px;
  background-color: #e0e0e0;
  margin-top: 15px;
}

.step.active .step-line, .step.completed .step-line {
  background-color: #4CAF50;
}

.step-info {
  text-align: center;
}

.step-name {
  margin: 0;
  font-weight: bold;
}

.step-time {
  margin: 5px 0 0;
  font-size: 12px;
  color: #757575;
}

/* 订单信息 */
.order-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  color: #757575;
  font-size: 14px;
  margin-bottom: 5px;
}

/* 物流信息 */
.logistics-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.copy-btn {
  display: inline-block;
  margin-left: 10px;
  padding: 2px 8px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.logistics-timeline {
  border-left: 2px solid #e0e0e0;
  padding-left: 20px;
  margin-left: 10px;
}

.tracking-event {
  display: flex;
  margin-bottom: 20px;
  position: relative;
}

.event-time {
  width: 80px;
  text-align: right;
  margin-right: 20px;
  flex-shrink: 0;
}

.date, .time {
  margin: 0;
  font-size: 14px;
}

.time {
  color: #757575;
}

.event-info {
  position: relative;
  flex: 1;
}

.event-dot {
  position: absolute;
  left: -26px;
  top: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #4a6572;
}

.event-dot.first {
  background-color: #e53935;
}

.event-line {
  position: absolute;
  left: -22px;
  top: 10px;
  width: 2px;
  height: calc(100% + 20px);
  background-color: #e0e0e0;
}

.event-content {
  margin: 0 0 5px;
}

.event-location {
  margin: 0;
  font-size: 14px;
  color: #757575;
}

.no-tracking, .loading-logistics, .no-logistics {
  padding: 20px 0;
  text-align: center;
  color: #757575;
}

/* 收货信息 */
.address-info p {
  margin: 10px 0;
}

/* 商品信息 */
.product-list {
  margin-bottom: 20px;
}

.product-item {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr;
  gap: 15px;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-image {
  width: 80px;
  height: 80px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.product-info h3 {
  margin: 0 0 5px;
  font-size: 16px;
}

.product-price {
  margin: 0;
  color: #757575;
}

.product-quantity, .product-total {
  text-align: center;
}

.product-total {
  font-weight: bold;
}

/* 价格汇总 */
.price-summary {
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.summary-item {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.summary-item span:first-child {
  margin-right: 20px;
  color: #757575;
}

.summary-total {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  font-weight: bold;
  font-size: 18px;
}

.summary-total span:first-child {
  margin-right: 20px;
}

/* 订单操作 */
.order-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.pay-btn, .cancel-btn, .confirm-btn, .review-btn, .back-btn {
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-size: 16px;
  text-decoration: none;
}

.pay-btn {
  background-color: #e53935;
  color: white;
}

.cancel-btn {
  background-color: #9e9e9e;
  color: white;
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
}

.review-btn {
  background-color: #ff9800;
  color: white;
}

.back-btn {
  background-color: #f0f0f0;
  color: #333;
}

/* 状态颜色 */
.status-unpaid {
  color: #ff9800;
}

.status-paid {
  color: #2196F3;
}

.status-shipped {
  color: #9C27B0;
}

.status-completed {
  color: #4CAF50;
}

.status-cancelled {
  color: #f44336;
}

.text-success {
  color: #4CAF50;
}

.text-warning {
  color: #ff9800;
}

@media (max-width: 768px) {
  .order-info-grid,
  .logistics-header {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .status-steps {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .step {
    width: 100%;
    flex-direction: row;
    align-items: flex-start;
  }
  
  .step-icon {
    margin-right: 15px;
    margin-bottom: 0;
  }
  
  .step-line {
    display: none;
  }
  
  .step-info {
    text-align: left;
  }
  
  .product-item {
    grid-template-columns: 80px 1fr;
    gap: 10px;
  }
  
  .product-quantity, .product-total {
    grid-column: 2;
    text-align: left;
  }
  
  .order-actions {
    flex-direction: column;
  }
  
  .pay-btn, .cancel-btn, .confirm-btn, .review-btn, .back-btn {
    width: 100%;
    text-align: center;
  }
}
</style> 