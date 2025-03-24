import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_PRODUCT_URL || 'http://localhost:8081/product';

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10秒超时
});

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API请求错误:', error);
    return Promise.reject(error);
  }
);

/**
 * 产品相关API接口
 */
const productApi = {
  /**
   * 获取所有产品
   * @returns {Promise<Array>} 产品列表
   */
  getProducts() {
    return apiClient.get(`/api/products`)
      .then(response => response.data);
  },

  /**
   * 获取单个产品详情
   * @param {string} id 产品ID
   * @returns {Promise<Object>} 产品详情
   */
  getProductById(id) {
    return apiClient.get(`/api/products/${id}`)
      .then(response => response.data);
  },

  /**
   * 按类别获取产品
   * @param {string} category 类别名称
   * @returns {Promise<Array>} 产品列表
   */
  getProductsByCategory(category) {
    return apiClient.get(`/api/products/category/${category}`);
  },

  /**
   * 搜索产品
   * @param {Object} params 搜索参数
   * @returns {Promise<Array>} 产品列表
   */
  searchProducts(params) {
    return apiClient.get(`/api/products/search`, {
      params
    });
  },

  /**
   * 获取所有产品类别
   * @returns {Promise<Array>} 类别列表
   */
  getCategories() {
    return apiClient.get(`/api/products/categories`)
      .then(response => response.data);
  },

  /**
   * 创建新产品
   * @param {Object} product 产品数据
   * @returns {Promise<Object>} 创建的产品
   */
  createProduct(product) {
    return apiClient.post(`/api/products`, product);
  },

  /**
   * 更新产品
   * @param {string} id 产品ID
   * @param {Object} product 产品数据
   * @returns {Promise<Object>} 更新后的产品
   */
  updateProduct(id, product) {
    return apiClient.put(`/api/products/${id}`, product);
  },

  /**
   * 删除产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  deleteProduct(id) {
    return apiClient.delete(`/api/products/${id}`);
  },

  /**
   * 上架产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  productOnline(id) {
    return apiClient.put(`/api/products/${id}/online`);
  },

  /**
   * 下架产品
   * @param {string} id 产品ID
   * @returns {Promise} 请求结果
   */
  productOffline(id) {
    return apiClient.put(`/api/products/${id}/offline`);
  },
};

export default productApi; 