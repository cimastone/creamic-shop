<template>
  <div class="product-list">
    <h1>陶瓷商品列表</h1>
    
    <!-- 分类过滤 -->
    <div class="category-filter">
      <span>分类筛选：</span>
      <button 
        class="category-btn" 
        :class="{ active: selectedCategory === '' }"
        @click="filterByCategory('')"
      >
        全部
      </button>
      <button 
        v-for="category in categories" 
        :key="category" 
        class="category-btn"
        :class="{ active: selectedCategory === category }"
        @click="filterByCategory(category)"
      >
        {{ category }}
      </button>
      </div>

    <!-- 搜索框 -->
    <div class="search-bar">
          <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="搜索产品..." 
        @input="debounceSearch"
      />
    </div>

    <!-- 加载中提示 -->
    <div v-if="loading" class="loading">
        <p>加载中...</p>
      </div>

    <!-- 产品列表 -->
    <div v-else class="products-container">
      <div v-if="filteredProducts.length === 0" class="no-products">
        <p>暂无商品</p>
      </div>
      <div v-else class="products-grid">
        <div 
          v-for="product in filteredProducts" 
             :key="product.id" 
          class="product-card"
          @click="goToProductDetail(product.id)"
        >
          <div class="product-image">
            <img :src="product.image || getDefaultProductImage(product)" :alt="product.name" />
            <span v-if="product.status === 'OFFLINE'" class="status-badge offline">已下架</span>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-category">分类: {{ product.category }}</p>
            <p class="product-price">¥{{ product.price.toFixed(2) }}</p>
            <p class="product-stock" :class="{ 'low-stock': product.stock < 10 }">
              库存: {{ product.stock }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import productApi from '@/api/productApi';

export default {
  name: 'ProductListView',
  setup() {
    const router = useRouter();
    const route = useRoute();
    const products = ref([]);
    const categories = ref([]);
    const loading = ref(true);
    const selectedCategory = ref('');
    const searchQuery = ref('');
    const searchTimeout = ref(null);

    // 获取所有产品
    const fetchProducts = async () => {
      loading.value = true;
      try {
        console.log('开始获取产品列表...');
        const response = await productApi.getProducts();
        console.log('ProductListView 获取到响应:', response);
        
        // 检查response的具体格式，适应不同的返回格式
        if (Array.isArray(response)) {
          products.value = response;
        } else if (response && response.data && Array.isArray(response.data)) {
          products.value = response.data;
        } else if (response && response.success && Array.isArray(response.data)) {
          products.value = response.data;
        } else {
          console.error('无法解析产品列表数据:', response);
          products.value = [];
        }
        
        console.log('处理后的产品列表数据:', products.value);
      } catch (error) {
        console.error('获取产品列表失败:', error);
        products.value = [];
      } finally {
        loading.value = false;
      }
    };

    // 获取所有分类
    const fetchCategories = async () => {
      try {
        const response = await productApi.getCategories();
        // 添加中英文映射
        const categoryMap = {
          'bowls': '碗',
          'cups': '杯子',
          'plates': '盘子',
          'vases': '花瓶'
        };
        
        // 转换为中文显示
        categories.value = response.map(cat => categoryMap[cat] || cat);
      } catch (error) {
        console.error('获取产品分类失败:', error);
      }
    };

    // 根据分类筛选产品
    const filterByCategory = (category) => {
      selectedCategory.value = category;
      
      // 如果是中文分类，需要转换为英文后再筛选
      if (category) {
        // 中英文映射（反向查找）
        const reverseCategoryMap = {
          '碗': 'bowls',
          '杯子': 'cups',
          '盘子': 'plates',
          '花瓶': 'vases'
        };
        
        const categoryForAPI = reverseCategoryMap[category] || category;
        router.replace({ query: { ...route.query, category: categoryForAPI } });
      } else {
        const { category: _, ...otherQuery } = route.query;
        router.replace({ query: otherQuery });
      }
    };

    // 搜索产品
    const searchProducts = async () => {
      if (!searchQuery.value.trim()) {
        await fetchProducts();
        return;
      }
      
      loading.value = true;
      try {
        const response = await productApi.searchProducts(searchQuery.value);
        products.value = response;
      } catch (error) {
        console.error('搜索产品失败:', error);
      } finally {
        loading.value = false;
      }
    };

    // 防抖搜索
    const debounceSearch = () => {
      if (searchTimeout.value) {
        clearTimeout(searchTimeout.value);
      }
      searchTimeout.value = setTimeout(() => {
        searchProducts();
      }, 500);
    };

    // 获取产品默认图片
    const getDefaultProductImage = (product) => {
      return `https://placeholder.pics/svg/300x200/DEDEDE/555555/${product.name}`;
    };

    // 跳转到产品详情页
    const goToProductDetail = (productId) => {
      router.push(`/products/${productId}`);
    };

    // 过滤后的产品列表
    const filteredProducts = computed(() => {
      if (!selectedCategory.value) {
        return products.value;
      }
      
      // 获取英文分类（如果选择的是中文分类）
      const reverseCategoryMap = {
        '碗': 'bowls',
        '杯子': 'cups',
        '盘子': 'plates',
        '花瓶': 'vases'
      };
      
      const categoryForFilter = reverseCategoryMap[selectedCategory.value] || selectedCategory.value;
      
      return products.value.filter(product => {
        const productCategory = product.category;
        return productCategory === categoryForFilter || productCategory === selectedCategory.value;
      });
    });

    onMounted(() => {
      // 从URL中获取分类参数
      if (route.query.category) {
        // 英文到中文映射
        const categoryMap = {
          'bowls': '碗',
          'cups': '杯子',
          'plates': '盘子',
          'vases': '花瓶'
        };
        
        // 如果是英文分类，转换为中文显示
        selectedCategory.value = categoryMap[route.query.category] || route.query.category;
      }
      
      fetchProducts();
      fetchCategories();
    });

    return {
      products,
      categories,
      loading,
      selectedCategory,
      searchQuery,
      filteredProducts,
      filterByCategory,
      debounceSearch,
      goToProductDetail,
      getDefaultProductImage
    };
  }
};
</script>

<style scoped>
.product-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.category-filter {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category-filter span {
  margin-right: 10px;
  font-weight: bold;
}

.category-btn {
  background-color: #f0f0f0;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  margin: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn.active {
  background-color: #4a6572;
  color: white;
}

.search-bar {
  margin-bottom: 30px;
}

.search-bar input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}

.no-products {
  text-align: center;
  padding: 50px;
  color: #666;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  background-color: white;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge.offline {
  background-color: rgba(244, 67, 54, 0.9);
  color: white;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 18px;
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-category {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #e53935;
  margin: 5px 0;
}

.product-stock {
  font-size: 14px;
  color: #4caf50;
  margin: 5px 0;
}

.low-stock {
  color: #ff9800;
}
</style> 