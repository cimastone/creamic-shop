<template>
  <view class="order-container">
    <view class="tab-nav">
      <view 
        v-for="(tab, index) in tabs" 
        :key="index" 
        class="tab-item" 
        :class="{ active: currentTab === index }"
        @tap="switchTab(index)"
      >
        {{ tab }}
      </view>
    </view>
    
    <view class="order-list" v-if="orders.length > 0">
      <view class="order-item" v-for="(order, index) in orders" :key="index" @tap="navigateToDetail(order.id)">
        <view class="order-header">
          <text class="order-number">订单号: {{ order.orderNumber }}</text>
          <text class="order-status" :class="'status-' + order.status">{{ getStatusText(order.status) }}</text>
        </view>
        
        <view class="product-list">
          <view class="product-item" v-for="(product, productIndex) in order.products" :key="productIndex">
            <image :src="product.imageUrl" mode="aspectFit" class="product-image" />
            <view class="product-info">
              <text class="product-name">{{ product.name }}</text>
              <text class="product-specs">{{ product.specs }}</text>
              <view class="product-price-qty">
                <text class="product-price">¥{{ product.price }}</text>
                <text class="product-qty">x{{ product.quantity }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="order-footer">
          <view class="order-total">
            <text>共{{ getTotalQuantity(order) }}件商品 合计:</text>
            <text class="total-price">¥{{ order.totalAmount }}</text>
          </view>
          
          <view class="order-actions">
            <button 
              v-if="order.status === 'PENDING_PAYMENT'" 
              class="action-btn payment-btn" 
              @tap.stop="navigateToPayment(order.id)"
            >
              去支付
            </button>
            <button 
              v-if="order.status === 'PENDING_PAYMENT'" 
              class="action-btn cancel-btn" 
              @tap.stop="cancelOrder(order.id)"
            >
              取消订单
            </button>
            <button 
              v-if="order.status === 'SHIPPED'" 
              class="action-btn" 
              @tap.stop="confirmReceive(order.id)"
            >
              确认收货
            </button>
            <button 
              v-if="order.status === 'COMPLETED'" 
              class="action-btn" 
              @tap.stop="reviewOrder(order.id)"
            >
              评价
            </button>
          </view>
        </view>
      </view>
    </view>
    
    <view v-else class="empty-order">
      <image src="/static/images/empty-order.png" mode="aspectFit" />
      <text>暂无订单</text>
      <button type="primary" @tap="navigateToHome">去购物</button>
    </view>
  </view>
</template>

<script>
import { orderApi } from '../../api/index';

export default {
  data() {
    return {
      tabs: ['全部', '待付款', '待发货', '待收货', '已完成'],
      currentTab: 0,
      orders: []
    }
  },
  methods: {
    switchTab(index) {
      this.currentTab = index;
      this.fetchOrders();
    },
    fetchOrders() {
      const statusMap = ['', 'PENDING_PAYMENT', 'PAID', 'SHIPPED', 'COMPLETED'];
      const params = { status: statusMap[this.currentTab] };
      
      orderApi.getOrders(params).then(res => {
        this.orders = res.data;
      }).catch(err => {
        console.error('获取订单列表失败', err);
      });
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING_PAYMENT': '待付款',
        'PAID': '待发货',
        'SHIPPED': '待收货',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return statusMap[status] || status;
    },
    getTotalQuantity(order) {
      return order.products.reduce((sum, product) => sum + product.quantity, 0);
    },
    navigateToDetail(orderId) {
      wx.navigateTo({
        url: `/pages/orderDetail/index?id=${orderId}`
      });
    },
    navigateToPayment(orderId) {
      wx.navigateTo({
        url: `/pages/payment/index?orderId=${orderId}`
      });
    },
    navigateToHome() {
      wx.switchTab({
        url: '/pages/home/index'
      });
    },
    cancelOrder(orderId) {
      wx.showModal({
        title: '提示',
        content: '确定要取消订单吗？',
        success: (res) => {
          if (res.confirm) {
            orderApi.cancelOrder(orderId).then(() => {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              });
              this.fetchOrders();
            }).catch(err => {
              console.error('取消订单失败', err);
            });
          }
        }
      });
    },
    confirmReceive(orderId) {
      wx.showModal({
        title: '提示',
        content: '确认已收到商品吗？',
        success: (res) => {
          if (res.confirm) {
            orderApi.confirmReceipt(orderId).then(() => {
              wx.showToast({
                title: '确认收货成功',
                icon: 'success'
              });
              this.fetchOrders();
            }).catch(err => {
              console.error('确认收货失败', err);
            });
          }
        }
      });
    },
    reviewOrder(orderId) {
      wx.navigateTo({
        url: `/pages/review/index?orderId=${orderId}`
      });
    }
  },
  onShow() {
    this.fetchOrders();
  }
}
</script>

<style>
.order-container {
  padding-bottom: 40rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}
.tab-nav {
  display: flex;
  background-color: #fff;
  border-bottom: 1rpx solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  color: #333;
  position: relative;
}
.tab-item.active {
  color: #1296db;
}
.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background-color: #1296db;
}
.order-list {
  padding: 20rpx;
}
.order-item {
  background-color: #fff;
  border-radius: 10rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}
.order-number {
  font-size: 28rpx;
  color: #666;
}
.order-status {
  font-size: 28rpx;
  font-weight: bold;
}
.status-PENDING_PAYMENT {
  color: #ff9800;
}
.status-PAID {
  color: #1296db;
}
.status-SHIPPED {
  color: #8bc34a;
}
.status-COMPLETED {
  color: #4caf50;
}
.status-CANCELLED {
  color: #999;
}
.product-list {
  padding: 20rpx;
}
.product-item {
  display: flex;
  margin-bottom: 20rpx;
}
.product-item:last-child {
  margin-bottom: 0;
}
.product-image {
  width: 140rpx;
  height: 140rpx;
  border-radius: 6rpx;
  margin-right: 20rpx;
}
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.product-name {
  font-size: 28rpx;
  color: #333;
}
.product-specs {
  font-size: 24rpx;
  color: #999;
}
.product-price-qty {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-price {
  font-size: 28rpx;
  color: #e4393c;
}
.product-qty {
  font-size: 24rpx;
  color: #999;
}
.order-footer {
  padding: 20rpx;
  border-top: 1rpx solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-total {
  font-size: 28rpx;
  color: #666;
}
.total-price {
  font-size: 32rpx;
  color: #e4393c;
  font-weight: bold;
}
.order-actions {
  display: flex;
}
.action-btn {
  margin-left: 20rpx;
  font-size: 24rpx;
  padding: 0 20rpx;
  height: 60rpx;
  line-height: 60rpx;
  border-radius: 30rpx;
  background-color: #fff;
  border: 1rpx solid #ddd;
}
.payment-btn {
  color: #fff;
  background-color: #e4393c;
  border-color: #e4393c;
}
.empty-order {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}
.empty-order image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 40rpx;
}
.empty-order text {
  font-size: 30rpx;
  color: #999;
  margin-bottom: 40rpx;
}
</style> 