<template>
  <div class="product-detail">
    <!-- 加载中提示 -->
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
      <button @click="goBack" class="back-btn">返回</button>
    </div>
    
    <!-- 产品详情 -->
    <div v-else-if="product" class="product-container">
      <div class="back-link">
        <a @click.prevent="goBack" href="#">&larr; 返回列表</a>
      </div>

      <div class="product-main">
        <div class="product-image">
          <img :src="product.image || getDefaultProductImage(product)" :alt="product.name" />
          <span v-if="product.status === 'OFFLINE'" class="status-badge offline">已下架</span>
        </div>

        <div class="product-info">
          <h1 class="product-name">{{ product.name }}</h1>
          
          <div class="product-meta">
            <p class="product-category">分类: {{ product.category }}</p>
            <p class="product-stock" :class="{ 'low-stock': product.stock < 10, 'out-of-stock': product.stock === 0 }">
              库存: {{ product.stock }}
            </p>
          </div>

          <p class="product-price">¥{{ product.price?.toFixed(2) || '0.00' }}</p>
          
          <div class="product-description">
            <h3>产品描述</h3>
            <p>{{ product.description || '暂无描述' }}</p>
          </div>

          <div class="product-actions">
            <div class="quantity-control">
              <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
              <input type="number" v-model.number="quantity" min="1" :max="product.stock" />
              <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
            </div>
            
            <button 
              class="add-to-cart-btn" 
              :disabled="product.stock === 0"
                    @click="addToCart" 
            >
              {{ product.stock > 0 ? '加入购物车' : '缺货' }}
            </button>
          </div>
        </div>
      </div>
      
      <div class="related-products" v-if="relatedProducts.length > 0">
        <h2>相关产品</h2>
        <div class="related-products-grid">
          <div 
            v-for="relatedProduct in relatedProducts" 
            :key="relatedProduct.id" 
            class="related-product-card"
            @click="goToProductDetail(relatedProduct.id)"
          >
            <div class="related-product-image">
              <img :src="relatedProduct.image || getDefaultProductImage(relatedProduct)" :alt="relatedProduct.name" />
            </div>
            <div class="related-product-info">
              <h3 class="related-product-name">{{ relatedProduct.name }}</h3>
              <p class="related-product-price">¥{{ relatedProduct.price?.toFixed(2) || '0.00' }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import productApi from '@/api/productApi';
import { useCartStore } from '@/stores/cart';
import { useUserStore } from '@/stores/user';

export default {
  name: 'ProductDetailView',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const cartStore = useCartStore();
    const userStore = useUserStore();
    const product = ref(null);
    const relatedProducts = ref([]);
    const loading = ref(true);
    const error = ref(null);
    const quantity = ref(1);
    
    // 获取产品详情
    const fetchProductDetail = async () => {
      const productId = route.params.id;
      if (!productId) {
        error.value = '产品ID不存在';
        loading.value = false;
        return;
      }
      
      loading.value = true;
      error.value = null;
      
      try {
        const response = await productApi.getProductById(productId);
        
        // 中英文映射
        const categoryMap = {
          'bowls': '碗',
          'cups': '杯子',
          'plates': '盘子',
          'vases': '花瓶'
        };
        
        // 转换分类为中文
        if (response.category && categoryMap[response.category]) {
          response.category = categoryMap[response.category];
        }
        
        product.value = response;
        
        // 获取相关产品
        fetchRelatedProducts(product.value.category);
      } catch (err) {
        console.error('获取产品详情失败:', err);
        error.value = '获取产品详情失败，请重试';
      } finally {
        loading.value = false;
      }
    };
    
    // 获取相关产品
    const fetchRelatedProducts = async (category) => {
      try {
        // 中英文映射（反向查找）
        const reverseCategoryMap = {
          '碗': 'bowls',
          '杯子': 'cups',
          '盘子': 'plates',
          '花瓶': 'vases'
        };
        
        // 如果是中文分类，转换为英文再请求API
        const categoryForAPI = reverseCategoryMap[category] || category;
        
        const response = await productApi.getProductsByCategory(categoryForAPI);
        console.log('相关产品响应:', response);
        
        // 确保响应是数组
        let productsData = [];
        if (Array.isArray(response)) {
          productsData = response;
        } else if (response && Array.isArray(response.data)) {
          productsData = response.data;
        } else if (response && response.data && Array.isArray(response.data.data)) {
          productsData = response.data.data;
        } else {
          console.warn('未能识别相关产品数据格式:', response);
          productsData = [];
        }
        
        // 中英文映射
        const categoryMap = {
          'bowls': '碗',
          'cups': '杯子',
          'plates': '盘子',
          'vases': '花瓶'
        };
        
        // 转换响应中的产品分类为中文
        const productsWithChineseCategories = productsData.map(p => {
          if (p.category && categoryMap[p.category]) {
            return {...p, category: categoryMap[p.category]};
          }
          return p;
        });
        
        // 过滤掉当前产品，只保留4个相关产品
        relatedProducts.value = productsWithChineseCategories
          .filter(p => p.id !== product.value.id)
          .slice(0, 4);
      } catch (err) {
        console.error('获取相关产品失败:', err);
      }
    };
    
    // 获取产品默认图片
    const getDefaultProductImage = (product) => {
      return `https://placeholder.pics/svg/300x200/DEDEDE/555555/${product.name}`;
    };
    
    // 减少数量
const decreaseQuantity = () => {
  if (quantity.value > 1) {
        quantity.value--;
  }
    };

    // 增加数量
const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
        quantity.value++;
      }
    };
    
    // 添加到购物车
    const addToCart = () => {
      // 检查用户是否已登录
      if (!userStore.checkLoginStatus()) {
        // 用户未登录，弹出提示并跳转到登录页
        if (confirm('您需要登录后才能添加商品到购物车。是否现在登录？')) {
          router.push({
            path: '/login',
            query: { redirect: route.fullPath } // 存储当前页面路径以便登录后返回
          });
        }
        return;
      }
      
      // 用户已登录，继续添加到购物车
      if (product.value && product.value.stock > 0) {
        try {
          // 创建飞入购物车的动画元素
          const productRect = document.querySelector('.product-image').getBoundingClientRect();
          const cartIcon = document.querySelector('.cart-icon') || { getBoundingClientRect: () => ({ top: 20, right: 20 }) };
          const cartRect = cartIcon.getBoundingClientRect();
          
          // 创建飞行元素
          const flyingImage = document.createElement('div');
          flyingImage.className = 'flying-image';
          flyingImage.style.cssText = `
            position: fixed;
            z-index: 9999;
            width: 70px;
            height: 70px;
            border-radius: 50%;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            background-image: url(${product.value.image || getDefaultProductImage(product.value)});
            background-size: cover;
            background-position: center;
            top: ${productRect.top + window.scrollY + productRect.height / 2 - 35}px;
            left: ${productRect.left + productRect.width / 2 - 35}px;
            opacity: 1;
            transform: scale(1);
            transition: all 0.8s cubic-bezier(0.18, 0.89, 0.32, 1.28);
          `;
          
          document.body.appendChild(flyingImage);
          
          // 触发动画
          setTimeout(() => {
            flyingImage.style.top = `${cartRect.top + window.scrollY + 5}px`;
            flyingImage.style.left = `${cartRect.left + 5}px`;
            flyingImage.style.opacity = '0.5';
            flyingImage.style.transform = 'scale(0.2)';
            
            // 动画结束后移除元素
            setTimeout(() => {
              if (document.body.contains(flyingImage)) {
                document.body.removeChild(flyingImage);
              }
              
              // 添加到购物车并显示提示
              cartStore.addToCart(product.value, quantity.value);
              
              // 让购物车图标闪烁
              const cartIcon = document.querySelector('.cart-icon');
              if (cartIcon) {
                cartIcon.classList.add('cart-pulse');
                // 动画结束后移除闪烁效果
                setTimeout(() => {
                  cartIcon.classList.remove('cart-pulse');
                }, 1000);
              }
              
              // 让购物车数量弹跳
              const cartCount = document.querySelector('.cart-count');
              if (cartCount) {
                cartCount.classList.add('cart-bounce');
                setTimeout(() => {
                  cartCount.classList.remove('cart-bounce');
                }, 1000);
              }
              
              // 显示自定义的成功提示
              const toast = document.createElement('div');
              toast.className = 'cart-toast';
              toast.innerHTML = `
                <div class="cart-toast-content">
                  <div class="cart-toast-icon">
                    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                  </div>
                  <div class="cart-toast-message">
                    已将 ${quantity.value} 件 ${product.value.name} 加入购物车
                  </div>
                </div>
                <div class="cart-toast-buttons">
                  <button class="cart-toast-btn continue">继续购物</button>
                  <button class="cart-toast-btn checkout" onclick="window.location.href='/cart'">查看购物车</button>
                </div>
              `;
              
              document.body.appendChild(toast);
              
              // 添加点击事件处理器
              const continueBtn = toast.querySelector('.continue');
              if (continueBtn) {
                continueBtn.addEventListener('click', () => {
                  document.body.removeChild(toast);
                });
              }
              
              // 3秒后自动关闭
              setTimeout(() => {
                if (document.body.contains(toast)) {
                  document.body.removeChild(toast);
                }
              }, 3000);
            }, 800);
          }, 50);
          
        } catch (error) {
          console.error('添加到购物车失败:', error);
          alert('添加到购物车失败，请重试');
        }
      } else {
        alert('该商品缺货，无法加入购物车');
      }
    };
    
    // 返回上一页
    const goBack = () => {
      router.back();
    };
    
    // 跳转到其他产品详情
    const goToProductDetail = (productId) => {
      router.push(`/products/${productId}`);
    };
    
    onMounted(() => {
      fetchProductDetail();
    });
    
    return {
      product,
      relatedProducts,
      loading,
      error,
      quantity,
      decreaseQuantity,
      increaseQuantity,
      addToCart,
      goBack,
      goToProductDetail,
      getDefaultProductImage
    };
  }
};
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading, .error {
  text-align: center;
  padding: 50px;
  color: #666;
}

.error {
  color: #e53935;
}

.back-btn {
  background-color: #4a6572;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  margin-top: 20px;
  cursor: pointer;
}

.back-link {
  margin-bottom: 20px;
}

.back-link a {
  color: #4a6572;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  font-weight: 500;
  cursor: pointer;
}

.back-link a:hover {
  text-decoration: underline;
}

.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.product-image {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.product-image img {
      width: 100%;
      height: auto;
  display: block;
}

.status-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  background-color: rgba(244, 67, 54, 0.9);
  color: white;
}

.product-info {
  display: flex;
  flex-direction: column;
  }

.product-name {
    font-size: 28px;
  color: #333;
  margin-top: 0;
  margin-bottom: 15px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.product-category {
  color: #666;
  font-size: 16px;
}

.product-stock {
  color: #4caf50;
  font-size: 16px;
}

.low-stock {
  color: #ff9800;
}

.out-of-stock {
  color: #f44336;
}

.product-price {
  font-size: 32px;
  font-weight: bold;
  color: #e53935;
  margin-bottom: 30px;
}

.product-description {
  margin-bottom: 30px;
}

.product-description h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.product-description p {
  color: #666;
  line-height: 1.6;
}

.product-actions {
  margin-top: auto;
    display: flex;
    align-items: center;
  gap: 15px;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 5px;
  overflow: hidden;
}

.quantity-control button {
  width: 40px;
  height: 40px;
  background-color: #f5f5f5;
  border: none;
  font-size: 18px;
      cursor: pointer;
}

.quantity-control button:disabled {
  color: #ccc;
        cursor: not-allowed;
      }

.quantity-control input {
  width: 60px;
  height: 40px;
  border: none;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
  text-align: center;
  font-size: 16px;
}

.add-to-cart-btn {
  flex: 1;
  height: 40px;
  background-color: #4a6572;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-to-cart-btn:hover {
  background-color: #344955;
}

.add-to-cart-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.related-products {
  margin-top: 60px;
}

.related-products h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.related-products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.related-product-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
      cursor: pointer;
}

.related-product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.related-product-image {
  height: 160px;
  overflow: hidden;
}

.related-product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.related-product-card:hover .related-product-image img {
  transform: scale(1.05);
}

.related-product-info {
  padding: 15px;
}

.related-product-name {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.related-product-price {
  font-size: 18px;
  font-weight: bold;
  color: #e53935;
  margin: 0;
}

@media (max-width: 768px) {
  .product-main {
    grid-template-columns: 1fr;
  }
  
  .product-image {
    margin-bottom: 20px;
  }
}

/* 飞入购物车动画样式 */
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.5); }
  70% { box-shadow: 0 0 0 15px rgba(76, 175, 80, 0); }
  100% { box-shadow: 0 0 0 0 rgba(76, 175, 80, 0); }
}

.flying-image {
  animation: pulse 1s infinite;
}

/* 购物车图标闪烁动画 */
@keyframes cartPulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1); }
}

:global(.cart-pulse) {
  animation: cartPulse 0.5s ease-in-out;
  color: #4CAF50;
}

/* 购物车提示样式 */
.cart-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: white;
  padding: 25px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  min-width: 320px;
  max-width: 400px;
  opacity: 0;
  transform: translateY(-20px);
  animation: cartToastFadeIn 0.4s forwards;
  border-top: 4px solid transparent;
  background-image: linear-gradient(white, white), 
                    linear-gradient(to right, #4CAF50, #8BC34A);
  background-origin: border-box;
  background-clip: padding-box, border-box;
}

@keyframes cartToastFadeIn {
  0% { opacity: 0; transform: translateY(-20px); }
  100% { opacity: 1; transform: translateY(0); }
}

.cart-toast-content {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.cart-toast-icon {
  width: 32px;
  height: 32px;
  min-width: 32px;
  background-color: rgba(76, 175, 80, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4CAF50;
}

.cart-toast-message {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.cart-toast-buttons {
  display: flex;
  gap: 12px;
  align-self: flex-end;
}

.cart-toast-btn {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.cart-toast-btn.continue {
  background-color: #f5f5f5;
  color: #555;
}

.cart-toast-btn.continue:hover {
  background-color: #e8e8e8;
}

.cart-toast-btn.checkout {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 3px 10px rgba(76, 175, 80, 0.2);
}

.cart-toast-btn.checkout:hover {
  background-color: #43A047;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
  transform: translateY(-2px);
}
</style> 