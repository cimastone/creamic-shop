import axios from 'axios';

// 测试产品API代理
async function testProductsApi() {
  try {
    console.log('测试 /api/products 路径...');
    const response = await axios.get('http://localhost:3000/api/products');
    console.log('成功! 返回产品数量:', response.data.length);
    return true;
  } catch (error) {
    console.error('产品API测试失败:', error.message);
    if (error.response) {
      console.error('  状态码:', error.response.status);
      console.error('  响应数据:', error.response.data);
    } else if (error.request) {
      console.error('  请求已发送但没有收到响应');
    } else {
      console.error('  错误详情:', error.toString());
    }
    return false;
  }
}

// 测试产品详情API代理
async function testProductDetailApi() {
  try {
    console.log('测试 /api/products/1 路径...');
    const response = await axios.get('http://localhost:3000/api/products/1');
    console.log('成功! 返回产品名称:', response.data.name);
    return true;
  } catch (error) {
    console.error('产品详情API测试失败:', error.message);
    if (error.response) {
      console.error('  状态码:', error.response.status);
      console.error('  响应数据:', error.response.data);
    } else if (error.request) {
      console.error('  请求已发送但没有收到响应');
    } else {
      console.error('  错误详情:', error.toString());
    }
    return false;
  }
}

// 测试订单API代理 (预期会失败，因为需要认证)
async function testOrdersApi() {
  try {
    console.log('测试 /api/orders 路径...');
    const response = await axios.get('http://localhost:3000/api/orders');
    console.log('成功! 返回数据:', response.data);
    return true;
  } catch (error) {
    if (error.response && error.response.status === 403) {
      console.log('订单API返回403 Forbidden (预期结果，因为需要认证)');
      return true;
    } else {
      console.error('订单API测试失败 (非预期错误):', error.message);
      if (error.response) {
        console.error('  状态码:', error.response.status);
        console.error('  响应数据:', error.response.data);
      } else if (error.request) {
        console.error('  请求已发送但没有收到响应');
      } else {
        console.error('  错误详情:', error.toString());
      }
      return false;
    }
  }
}

async function runTests() {
  console.log('开始测试代理配置...');
  
  let allTestsPassed = true;
  
  // 测试产品API
  allTestsPassed = await testProductsApi() && allTestsPassed;
  
  // 测试产品详情API
  allTestsPassed = await testProductDetailApi() && allTestsPassed;
  
  // 测试订单API
  allTestsPassed = await testOrdersApi() && allTestsPassed;
  
  if (allTestsPassed) {
    console.log('所有测试通过! 代理配置正常工作。');
  } else {
    console.error('测试失败! 代理配置需要修复。');
  }
}

runTests(); 