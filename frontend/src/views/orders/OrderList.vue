<template>
  <div class="order-list-container">
    <h1>我的订单</h1>
    
    <div class="order-tabs">
      <div 
        v-for="tab in tabs" 
        :key="tab.value" 
        :class="['tab-item', { active: currentTab === tab.value }]"
        @click="changeTab(tab.value)"
      >
        {{ tab.label }}
      </div>
    </div>
    
    <div class="order-list" v-if="orders.length > 0">
      <div class="order-item" v-for="order in orders" :key="order.id">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号: {{ order.orderNumber }}</span>
            <span class="order-time">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="order-status">{{ getStatusText(order.status) }}</div>
        </div>
        
        <div class="order-products">
          <div class="product-item" v-for="item in order.orderItems" :key="item.id">
            <img :src="item.productImage" alt="商品图片" class="product-image" />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-price">¥{{ item.price || item.unitPrice }} × {{ item.quantity }}</div>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            订单总额: <span class="total-price">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <button 
              v-if="order.status === 'PENDING_PAYMENT'" 
              class="btn-pay"
              @click="payOrder(order.id)"
            >
              去支付
            </button>
            <button 
              v-if="order.status === 'PENDING_PAYMENT'" 
              class="btn-cancel"
              @click="cancelOrder(order.id)"
            >
              取消订单
            </button>
            <button 
              v-if="order.status === 'SHIPPED'" 
              class="btn-confirm"
              @click="completeOrder(order.id)"
            >
              确认收货
            </button>
            <button 
              class="btn-detail"
              @click="viewOrderDetail(order.id)"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-orders" v-else>
      <p>暂无订单数据</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserOrders, getUserOrdersByStatus, payOrder as apiPayOrder, cancelOrder as apiCancelOrder, completeOrder as apiCompleteOrder } from '../../api/order'
import { formatDate as formatDateUtil } from '@/utils/dateUtils'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const isLoading = ref(false)
const currentTab = ref('ALL')

const tabs = [
  { label: '全部', value: 'ALL' },
  { label: '待付款', value: 'PENDING_PAYMENT' },
  { label: '已付款', value: 'PAID' },
  { label: '已发货', value: 'SHIPPED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' }
]

// 格式化日期
const formatDate = (dateString) => {
  return formatDateUtil(dateString, 'YYYY-MM-DD HH:mm')
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

// 切换标签
const changeTab = (tab) => {
  currentTab.value = tab
  fetchOrders()
}

// 获取订单列表
const fetchOrders = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  isLoading.value = true
  try {
    let res
    if (currentTab.value === 'ALL') {
      res = await getUserOrders()
    } else {
      res = await getUserOrdersByStatus(currentTab.value)
    }
    
    // 确保orders是一个数组
    if (Array.isArray(res)) {
      orders.value = res
    } else {
      console.warn('获取到的订单数据不是数组:', res)
      orders.value = []
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orders.value = [] // 发生错误时设置为空数组
  } finally {
    isLoading.value = false
  }
}

// 支付订单
const payOrder = async (orderId) => {
  try {
    await apiPayOrder(orderId)
    fetchOrders()
  } catch (error) {
    console.error('支付订单失败:', error)
  }
}

// 取消订单
const cancelOrder = async (orderId) => {
  try {
    await apiCancelOrder(orderId)
    fetchOrders()
  } catch (error) {
    console.error('取消订单失败:', error)
  }
}

// 确认收货
const completeOrder = async (orderId) => {
  try {
    await apiCompleteOrder(orderId)
    fetchOrders()
  } catch (error) {
    console.error('确认收货失败:', error)
  }
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  router.push(`/orders/${orderId}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.order-tabs {
  display: flex;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 20px;
}

.tab-item {
  padding: 10px 20px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.tab-item.active {
  color: #1677ff;
  border-bottom: 2px solid #1677ff;
}

.order-item {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f7f7f7;
  border-bottom: 1px solid #e8e8e8;
}

.order-number {
  font-weight: 500;
  margin-right: 20px;
}

.order-time {
  color: #999;
  font-size: 14px;
}

.order-status {
  font-weight: 500;
  color: #1677ff;
}

.order-products {
  padding: 16px 20px;
}

.product-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
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

.product-price {
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f7f7f7;
  border-top: 1px solid #e8e8e8;
}

.total-price {
  font-weight: 500;
  color: #ff4d4f;
  font-size: 18px;
}

.order-actions button {
  margin-left: 12px;
  padding: 6px 16px;
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

.btn-detail {
  background: #fff;
  color: #666;
  border: 1px solid #d9d9d9;
}

.btn-detail:hover {
  color: #1677ff;
  border-color: #1677ff;
}

.empty-orders {
  text-align: center;
  padding: 60px 0;
  color: #999;
}
</style> 