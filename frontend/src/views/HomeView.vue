<template>
  <div class="home">
    <header class="hero">
      <h1>欢迎来到陶瓷商城</h1>
      <p>发现我们精美的手工陶瓷收藏</p>
    </header>

    <section class="featured-categories">
      <h2>精选分类</h2>
      <div v-if="loadingCategories" class="loading-indicator">
        <p>加载分类中...</p>
      </div>
      <div v-else class="category-grid">
        <div class="category-card" v-for="category in categories" :key="category.id">
          <router-link :to="{ name: 'product-list', query: { category: category.id }}">
            <div class="category-image">
              <img :src="getCategoryImage(category)" :alt="category.name">
            </div>
            <h3>{{ category.name }}</h3>
          </router-link>
        </div>
      </div>
    </section>

    <section class="featured-products">
      <h2>精选商品</h2>
      <div v-if="loadingProducts" class="loading-indicator">
        <p>加载商品中...</p>
      </div>
      <div v-else class="product-grid">
        <div class="product-card" v-for="product in featuredProducts" :key="product.id">
          <router-link :to="{ name: 'product-detail', params: { id: product.id }}">
            <div class="product-image">
              <img :src="getProductImage(product)" :alt="product.name">
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">¥{{ product.price }}</p>
            </div>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import productApi from '@/api/productApi'

const categories = ref([])
const featuredProducts = ref([])
const loadingCategories = ref(true)
const loadingProducts = ref(true)

// 获取分类列表
const fetchCategories = async () => {
  loadingCategories.value = true
  try {
    const response = await productApi.getCategories()
    // 中英文映射
    const categoryMap = {
      'bowls': '碗',
      'cups': '杯子',
      'plates': '盘子',
      'vases': '花瓶'
    }
    
    // 正确处理响应数据结构
    let categoryData = []
    
    if (Array.isArray(response)) {
      categoryData = response
    } else if (response && Array.isArray(response.data)) {
      categoryData = response.data
    } else if (response && response.data && Array.isArray(response.data.data)) {
      categoryData = response.data.data
    } else if (response && response.success && Array.isArray(response.data)) {
      categoryData = response.data
    } else {
      console.error('无法解析分类数据:', response)
      categoryData = []
    }
    
    categories.value = categoryData.map(cat => ({
      id: cat,
      name: categoryMap[cat] || cat // 如果有映射则使用中文，否则使用原值
    }))
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = [] // 确保失败时至少有一个空数组
  } finally {
    loadingCategories.value = false
  }
}

// 获取精选商品
const fetchFeaturedProducts = async () => {
  loadingProducts.value = true
  try {
    const response = await productApi.getProducts()
    // 获取前4个上架的商品作为精选商品
    featuredProducts.value = response
      .filter(product => product.status === 'ONLINE')
      .slice(0, 4)
  } catch (error) {
    console.error('获取精选商品失败:', error)
  } finally {
    loadingProducts.value = false
  }
}

// 获取分类图片（使用占位图或API返回的图片）
const getCategoryImage = (category) => {
  return category.image || `https://placeholder.pics/svg/300x200/DEDEDE/555555/${category.name}`
}

// 获取商品图片（使用占位图或API返回的图片）
const getProductImage = (product) => {
  return product.image || `https://placeholder.pics/svg/300x200/DEDEDE/555555/${product.name}`
}

onMounted(() => {
  fetchCategories()
  fetchFeaturedProducts()
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.hero {
  text-align: center;
  padding: 60px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 40px;
}

.hero h1 {
  font-size: 2.5em;
  color: #333;
  margin-bottom: 20px;
}

.hero p {
  font-size: 1.2em;
  color: #666;
}

.featured-categories,
.featured-products {
  margin-bottom: 40px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.loading-indicator {
  text-align: center;
  padding: 30px;
  color: #666;
}

.category-grid,
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.category-card,
.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.category-card:hover,
.product-card:hover {
  transform: translateY(-5px);
}

.category-image,
.product-image {
  height: 200px;
  overflow: hidden;
}

.category-image img,
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

h3 {
  margin: 10px 0;
  color: #333;
  font-size: 1.1em;
}

.price {
  color: #4CAF50;
  font-weight: bold;
  font-size: 1.2em;
}

a {
  text-decoration: none;
  color: inherit;
}
</style> 