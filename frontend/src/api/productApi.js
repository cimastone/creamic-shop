import { productApi as api } from './index';

/**
 * 产品相关API接口
 */
const productApi = {
  /**
   * 获取所有产品
   * @returns {Promise<Array>} 产品列表
   */
  getProducts() {
    console.log('API: 请求获取所有产品');
    
    return new Promise((resolve, reject) => {
      api.get('/api/products')
        .then(response => {
          console.log('API: 获取产品原始响应:', response);
          
          // 处理各种可能的响应格式
          let products = [];
          
          if (Array.isArray(response)) {
            products = response;
          } else if (response && Array.isArray(response.data)) {
            products = response.data;
          } else if (response && response.data && Array.isArray(response.data.data)) {
            products = response.data.data;
          } else if (response && response.data && response.data.success && Array.isArray(response.data.data)) {
            products = response.data.data;
          } else {
            console.warn('API: 未能识别产品数据格式:', response);
            products = [];
          }
          
          console.log('API: 解析后的产品数据:', products);
          resolve(products);
        })
        .catch(error => {
          console.error('API: 获取产品列表失败:', error);
          reject(error);
        });
    });
  },

  /**
   * 获取单个产品详情
   * @param {string} id 产品ID
   * @returns {Promise<Object>} 产品详情
   */
  getProductById(id) {
    console.log('API: 请求获取产品详情 ID:', id);
    
    return new Promise((resolve, reject) => {
      api.get(`/api/products/${id}`)
        .then(response => {
          console.log('API: 获取产品详情原始响应:', response);
          
          // 处理各种可能的响应格式
          let product = null;
          
          if (response && typeof response === 'object' && !Array.isArray(response)) {
            if (response.id) {
              product = response;
            } else if (response.data && response.data.id) {
              product = response.data;
            } else if (response.data && response.data.data && response.data.data.id) {
              product = response.data.data;
            } else {
              console.warn('API: 未能识别产品详情数据格式:', response);
              product = null;
            }
          } else {
            console.warn('API: 未能识别产品详情数据格式:', response);
            product = null;
          }
          
          console.log('API: 解析后的产品详情:', product);
          resolve(product);
        })
        .catch(error => {
          console.error('API: 获取产品详情失败:', error);
          reject(error);
        });
    });
  },

  /**
   * 按类别获取产品
   * @param {string} category 类别名称
   * @returns {Promise<Array>} 产品列表
   */
  getProductsByCategory(category) {
    console.log('API: 请求获取分类产品 Category:', category);
    
    return new Promise((resolve, reject) => {
      api.get(`/api/products/category/${category}`)
        .then(response => {
          console.log('API: 获取分类产品原始响应:', response);
          
          // 处理各种可能的响应格式
          let products = [];
          
          if (Array.isArray(response)) {
            products = response;
          } else if (response && Array.isArray(response.data)) {
            products = response.data;
          } else if (response && response.data && Array.isArray(response.data.data)) {
            products = response.data.data;
          } else {
            console.warn('API: 未能识别分类产品数据格式:', response);
            products = [];
          }
          
          console.log('API: 解析后的分类产品数据:', products);
          resolve(products);
        })
        .catch(error => {
          console.error('API: 获取分类产品列表失败:', error);
          reject(error);
        });
    });
  },

  /**
   * 搜索产品
   * @param {Object} params 搜索参数
   * @returns {Promise<Array>} 产品列表
   */
  searchProducts(params) {
    return api.get(`/api/products/search`, {
      params
    });
  },

  /**
   * 获取所有产品类别
   * @returns {Promise<Array>} 类别列表
   */
  getCategories() {
    return api.get(`/api/products/categories`);
  },

  /**
   * 创建新产品
   * @param {Object} product 产品数据
   * @returns {Promise<Object>} 创建的产品
   */
  createProduct(product) {
    return api.post(`/api/products`, product);
  },

  /**
   * 更新产品
   * @param {string} id 产品ID
   * @param {Object} product 产品数据
   * @returns {Promise<Object>} 更新后的产品
   */
  updateProduct(id, product) {
    return api.put(`/api/products/${id}`, product);
  },

  /**
   * 删除产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  deleteProduct(id) {
    return api.delete(`/api/products/${id}`);
  },

  /**
   * 上架产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  productOnline(id) {
    return api.put(`/api/products/${id}/online`);
  },

  /**
   * 下架产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  productOffline(id) {
    return api.put(`/api/products/${id}/offline`);
  },
};

export default productApi; 