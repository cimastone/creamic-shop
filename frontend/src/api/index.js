import axios from 'axios'

// 创建用户服务API实例
const userApi = axios.create({
  baseURL: '',  // 开发时使用空，由Vite代理处理
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10秒超时
})

// 创建产品服务API实例
const productApi = axios.create({
  baseURL: '',  // 开发时使用空，由Vite代理处理
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10秒超时
})

// 创建订单服务API实例
const orderApi = axios.create({
  baseURL: '',  // 开发时使用空，由Vite代理处理
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
        const data = response.data;
        
        // 处理特殊情况：字符串响应，直接返回标准化结构
        if (typeof data === 'string') {
          console.log(`接收到字符串响应: "${data}"`);
          return {
            success: true,
            message: data,
            data: null
          };
        }
        
        // 检查是否有标准API响应结构
        if (data && typeof data === 'object') {
          // 处理标准API响应格式 {code, message, data}
          if (data.hasOwnProperty('code')) {
            const isSuccess = data.code === 0 || data.code === 200;
            return {
              success: isSuccess,
              message: data.message || (isSuccess ? 'success' : 'error'),
              data: data.data || null
            };
          }
          
          // 其他情况直接包装返回
          return {
            success: true,
            message: 'success',
            data: data
          };
        }
        
        // 兜底处理
        return {
          success: true,
          message: 'success',
          data: data
        };
      }
      
      // HTTP错误状态直接返回
      return response;
    },
    error => {
      console.error('API错误:', error);
      
      // 如果有响应对象
      if (error.response) {
        console.log('错误状态:', error.response.status);
        console.log('错误数据:', error.response.data);
        
        // 尝试获取错误信息
        let errorMessage = '未知错误';
        let errorData = null;
        
        const responseData = error.response.data;
        
        // 处理字符串类型响应
        if (typeof responseData === 'string') {
          // 如果响应是"success"字符串，特殊处理
          if (responseData === 'success') {
            return {
              success: true,
              message: 'success',
              data: null
            };
          }
          errorMessage = responseData;
        } 
        // 处理对象类型响应
        else if (responseData && typeof responseData === 'object') {
          errorMessage = responseData.message || responseData.error || '请求失败';
          errorData = responseData.data;
        }
        
        // 根据状态码处理特定错误
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
          
          errorMessage = '登录已过期，请重新登录';
        }
        
        return Promise.reject({
          success: false,
          message: errorMessage,
          data: errorData,
          status: error.response.status
        });
      } 
      // 网络错误
      else if (error.request) {
        return Promise.reject({
          success: false,
          message: '无法连接到服务器，请检查您的网络连接',
          data: null
        });
      }
      
      // 其他错误
      return Promise.reject({
        success: false,
        message: error.message || '发生未知错误',
        data: null
      });
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