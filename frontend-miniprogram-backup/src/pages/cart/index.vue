<template>
  <view class="cart-container">
    <view v-if="cartItems.length === 0" class="empty-cart">
      <image src="/static/images/empty-cart.png" mode="aspectFit" />
      <text>购物车空空如也</text>
      <button type="primary" @tap="navigateToHome">去逛逛</button>
    </view>
    <view v-else>
      <view class="cart-list">
        <view class="cart-item" v-for="(item, index) in cartItems" :key="index">
          <checkbox :checked="item.selected" @tap="toggleSelect(index)" />
          <image :src="item.product.imageUrl" mode="aspectFit" class="product-image" />
          <view class="product-info">
            <text class="product-name">{{ item.product.name }}</text>
            <text class="product-specs">{{ item.specs }}</text>
            <text class="product-price">¥{{ item.product.price }}</text>
          </view>
          <view class="quantity-control">
            <text class="minus" @tap="decreaseQuantity(index)">-</text>
            <input type="number" :value="item.quantity" disabled />
            <text class="plus" @tap="increaseQuantity(index)">+</text>
          </view>
          <text class="remove-btn" @tap="removeItem(index)">×</text>
        </view>
      </view>

      <view class="cart-footer">
        <view class="select-all">
          <checkbox :checked="allSelected" @tap="toggleSelectAll" />
          <text>全选</text>
        </view>
        <view class="total-info">
          <text>合计: <text class="total-price">¥{{ totalPrice }}</text></text>
          <button type="primary" class="checkout-btn" @tap="checkout">结算({{ selectedCount }})</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { cartApi } from '../../api/index';

export default {
  data() {
    return {
      cartItems: [],
      allSelected: false
    }
  },
  computed: {
    selectedCount() {
      return this.cartItems.filter(item => item.selected).length;
    },
    totalPrice() {
      return this.cartItems
        .filter(item => item.selected)
        .reduce((sum, item) => sum + item.product.price * item.quantity, 0)
        .toFixed(2);
    }
  },
  methods: {
    fetchCartItems() {
      cartApi.getCartItems().then(res => {
        this.cartItems = res.data.map(item => ({
          ...item,
          selected: false
        }));
      }).catch(err => {
        console.error('获取购物车失败', err);
      });
    },
    toggleSelect(index) {
      this.cartItems[index].selected = !this.cartItems[index].selected;
      this.updateAllSelected();
    },
    toggleSelectAll() {
      this.allSelected = !this.allSelected;
      this.cartItems.forEach(item => {
        item.selected = this.allSelected;
      });
    },
    updateAllSelected() {
      this.allSelected = this.cartItems.length > 0 && this.cartItems.every(item => item.selected);
    },
    increaseQuantity(index) {
      const item = this.cartItems[index];
      cartApi.updateCartItem(item.id, { quantity: item.quantity + 1 }).then(() => {
        item.quantity += 1;
      }).catch(err => {
        console.error('更新数量失败', err);
      });
    },
    decreaseQuantity(index) {
      const item = this.cartItems[index];
      if (item.quantity <= 1) return;
      cartApi.updateCartItem(item.id, { quantity: item.quantity - 1 }).then(() => {
        item.quantity -= 1;
      }).catch(err => {
        console.error('更新数量失败', err);
      });
    },
    removeItem(index) {
      const item = this.cartItems[index];
      wx.showModal({
        title: '提示',
        content: '确定要删除这个商品吗？',
        success: (res) => {
          if (res.confirm) {
            cartApi.removeCartItem(item.id).then(() => {
              this.cartItems.splice(index, 1);
              this.updateAllSelected();
            }).catch(err => {
              console.error('删除商品失败', err);
            });
          }
        }
      });
    },
    checkout() {
      if (this.selectedCount === 0) {
        wx.showToast({
          title: '请选择商品',
          icon: 'none'
        });
        return;
      }
      
      const selectedItems = this.cartItems.filter(item => item.selected);
      wx.setStorageSync('checkoutItems', selectedItems);
      wx.navigateTo({
        url: '/pages/payment/index'
      });
    },
    navigateToHome() {
      wx.switchTab({
        url: '/pages/home/index'
      });
    }
  },
  onShow() {
    this.fetchCartItems();
  }
}
</script>

<style>
.cart-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}
.empty-cart image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 40rpx;
}
.empty-cart text {
  font-size: 30rpx;
  color: #999;
  margin-bottom: 40rpx;
}
.cart-list {
  margin-bottom: 100rpx;
}
.cart-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 20rpx;
  margin-bottom: 20rpx;
  border-radius: 10rpx;
  position: relative;
}
.product-image {
  width: 150rpx;
  height: 150rpx;
  margin: 0 20rpx;
}
.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.product-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
}
.product-specs {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
}
.product-price {
  font-size: 32rpx;
  color: #e4393c;
  font-weight: bold;
}
.quantity-control {
  display: flex;
  align-items: center;
  margin-right: 20rpx;
}
.quantity-control input {
  width: 80rpx;
  text-align: center;
  margin: 0 10rpx;
}
.minus, .plus {
  width: 50rpx;
  height: 50rpx;
  line-height: 50rpx;
  text-align: center;
  border: 1px solid #ccc;
  font-size: 28rpx;
}
.remove-btn {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  font-size: 40rpx;
  color: #999;
}
.cart-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
}
.select-all {
  display: flex;
  align-items: center;
}
.select-all text {
  margin-left: 10rpx;
  font-size: 28rpx;
}
.total-info {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.total-price {
  color: #e4393c;
  font-size: 36rpx;
  font-weight: bold;
  margin: 0 20rpx;
}
.checkout-btn {
  margin-left: 20rpx;
  border-radius: 50rpx;
  font-size: 28rpx;
  padding: 0 40rpx;
}
</style> 