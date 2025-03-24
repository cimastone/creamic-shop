import axios from 'axios'

// 创建用户服务API实例
const userApi = axios.create({
  baseURL: import.meta.env.VITE_API_USER_URL || 'http://localhost:8082/user',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10秒超时
})

// 创建产品服务API实例
const productApi = axios.create({
  baseURL: import.meta.env.VITE_API_PRODUCT_URL || 'http://localhost:8081/product',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10秒超时
})

// 创建订单服务API实例
const orderApi = axios.create({
  baseURL: import.meta.env.VITE_API_ORDER_URL || 'http://localhost:8003/order',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10秒超时
})

// 添加请求拦截器
const applyInterceptors = (instance) => {
  // 请求拦截器，添加token
  instance.interceptors.request.use(
    config => {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
      return config
    },
    error => {
      return Promise.reject(error)
    }
  )

  // 响应拦截器，处理常见错误
  instance.interceptors.response.use(
    response => {
      return response.data
    },
    error => {
      console.error('API错误:', error);
      // 添加详细的错误信息
      let errorMessage = '请求失败';
      
      if (error.response) {
        console.error('错误状态:', error.response.status);
        console.error('错误数据:', error.response.data);
        
        // 根据状态码提供更具体的错误信息
        if (error.response.status === 401) {
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token');
          window.location.href = '/login';
          errorMessage = '未授权，请重新登录';
        } else if (error.response.status === 400) {
          // 尝试从响应中提取错误消息
          if (error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message;
          } else if (error.response.data && error.response.data.error) {
            errorMessage = error.response.data.error;
          } else {
            errorMessage = '请求参数错误';
          }
        } else if (error.response.status === 500) {
          errorMessage = '服务器内部错误，请稍后再试';
        }
      } else if (error.request) {
        console.error('请求未收到响应:', error.request);
        errorMessage = '无法连接到服务器，请检查您的网络连接';
      } else {
        console.error('请求设置错误:', error.message);
        errorMessage = error.message;
      }
      
      // 修改错误对象以包含更友好的消息
      const enhancedError = new Error(errorMessage);
      enhancedError.originalError = error;
      return Promise.reject(enhancedError);
    }
  )
}

// 应用拦截器到所有API实例
applyInterceptors(userApi);
applyInterceptors(productApi);
applyInterceptors(orderApi);

// 导出API实例
export { userApi, productApi, orderApi }

// 兼容性导出，保持向后兼容
export default userApi 