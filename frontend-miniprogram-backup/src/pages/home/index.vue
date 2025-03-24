<template>
  <view class="home-container">
    <view class="banner">
      <swiper indicator-dots autoplay circular>
        <swiper-item v-for="(item, index) in banners" :key="index">
          <image :src="item.imageUrl" mode="widthFix" />
        </swiper-item>
      </swiper>
    </view>

    <view class="category-section">
      <view class="section-title">商品分类</view>
      <view class="category-list">
        <view class="category-item" v-for="(category, index) in categories" :key="index" @tap="navigateToCategory(category.id)">
          <image :src="category.iconUrl" mode="aspectFit" />
          <text>{{ category.name }}</text>
        </view>
      </view>
    </view>

    <view class="product-section">
      <view class="section-title">精选商品</view>
      <view class="product-list">
        <view class="product-item" v-for="(product, index) in featuredProducts" :key="index" @tap="navigateToProductDetail(product.id)">
          <image :src="product.imageUrl" mode="aspectFit" />
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-price">¥{{ product.price }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      banners: [
        { imageUrl: '/static/images/banner1.jpg' },
        { imageUrl: '/static/images/banner2.jpg' },
        { imageUrl: '/static/images/banner3.jpg' }
      ],
      categories: [
        { id: 1, name: '日用陶瓷', iconUrl: '/static/images/category1.png' },
        { id: 2, name: '艺术陶瓷', iconUrl: '/static/images/category2.png' },
        { id: 3, name: '收藏陶瓷', iconUrl: '/static/images/category3.png' },
        { id: 4, name: '礼品陶瓷', iconUrl: '/static/images/category4.png' }
      ],
      featuredProducts: [
        { id: 1, name: '青花瓷花瓶', price: 299, imageUrl: '/static/images/product1.jpg' },
        { id: 2, name: '手绘茶杯套装', price: 199, imageUrl: '/static/images/product2.jpg' },
        { id: 3, name: '陶瓷餐具套装', price: 399, imageUrl: '/static/images/product3.jpg' },
        { id: 4, name: '景德镇工艺品', price: 599, imageUrl: '/static/images/product4.jpg' }
      ]
    }
  },
  methods: {
    navigateToCategory(categoryId) {
      wx.navigateTo({
        url: `/pages/search/index?categoryId=${categoryId}`
      })
    },
    navigateToProductDetail(productId) {
      wx.navigateTo({
        url: `/pages/product/detail?id=${productId}`
      })
    }
  },
  onLoad() {
    // 可以在这里调用API获取数据
  }
}
</script>

<style>
.home-container {
  padding: 20rpx;
}
.banner {
  width: 100%;
  height: 300rpx;
  margin-bottom: 30rpx;
}
.banner image {
  width: 100%;
  height: 100%;
}
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin: 20rpx 0;
}
.category-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 30rpx;
}
.category-item {
  width: 24%;
  text-align: center;
  margin-bottom: 20rpx;
}
.category-item image {
  width: 80rpx;
  height: 80rpx;
}
.category-item text {
  font-size: 24rpx;
}
.product-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.product-item {
  width: 48%;
  background-color: #fff;
  border-radius: 10rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}
.product-item image {
  width: 100%;
  height: 300rpx;
}
.product-info {
  padding: 16rpx;
}
.product-name {
  font-size: 28rpx;
  color: #333;
  display: block;
}
.product-price {
  font-size: 32rpx;
  color: #e4393c;
  font-weight: bold;
}
</style> 