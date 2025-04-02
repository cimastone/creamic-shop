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
      // 检查响应状态
      if (response.status >= 200 && response.status < 300) {
        // 对于登录接口直接返回整个响应
        if (response.config.url.includes('/api/users/login')) {
          return response;
        }
        
        // 检查是否有code字段（自定义响应格式）
        if (response.data && response.data.hasOwnProperty('code')) {
          if (response.data.code === 200) {
            return response.data;
          } else {
            // 处理业务错误
            const error = new Error(response.data?.message || '请求失败');
            error.response = response;
            return Promise.reject(error);
          }
        }
        
        // 标准HTTP响应
        return response.data;
      }
      return response;
    },
    error => {
      console.error('API错误:', error);
      
      if (error.response) {
        console.error('错误状态:', error.response.status);
        console.error('错误数据:', error.response.data);
        
        // 对于登录接口的错误，直接返回
        if (error.config.url.includes('/api/users/login')) {
          return Promise.reject(error);
        }
        
        // 根据状态码提供更具体的错误信息
        if (error.response.status === 401) {
          // 未授权，清除token
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          
          // 保存当前路由信息
          const currentPath = window.location.pathname;
          const currentQuery = window.location.search;
          localStorage.setItem('lastRoute', JSON.stringify({
            path: currentPath,
            query: currentQuery
          }));
          
          // 如果不是登录页面，则跳转到登录页
          if (!window.location.pathname.includes('/login')) {
            window.location.href = `/login?redirect=${encodeURIComponent(currentPath + currentQuery)}`;
          }
          
          return Promise.reject(new Error('登录已过期，请重新登录'));
        } else if (error.response.status === 400) {
          const message = error.response.data?.message || error.response.data?.error || '请求参数错误';
          return Promise.reject(new Error(message));
        } else if (error.response.status === 500) {
          return Promise.reject(new Error('服务器内部错误，请稍后再试'));
        }
      } else if (error.request) {
        return Promise.reject(new Error('无法连接到服务器，请检查您的网络连接'));
      }
      
      return Promise.reject(error);
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