<template>
  <div class="address-selector">
    <div class="address-list" v-if="addresses.length > 0">
      <div 
        v-for="address in addresses" 
        :key="address.id" 
        class="address-item" 
        :class="{ active: selectedAddressId === address.id }"
        @click="selectAddress(address.id)"
      >
        <div class="address-info">
          <div class="recipient-info">
            <span class="name">{{ address.receiverName }}</span>
            <span class="phone">{{ address.receiverPhone }}</span>
            <span v-if="address.isDefault" class="default-tag">默认</span>
          </div>
          <div class="address-detail">
            {{ formatAddress(address) }}
          </div>
        </div>
        <div class="address-actions">
          <button class="edit-btn" @click.stop="editAddress(address.id)">编辑</button>
          <button class="delete-btn" @click.stop="deleteAddressConfirm(address.id)">删除</button>
        </div>
      </div>
    </div>
    
    <div class="empty-address" v-else>
      <div class="empty-icon">📍</div>
      <p class="empty-text">您还没有收货地址</p>
      <p class="empty-subtext">添加地址以便我们为您配送商品</p>
    </div>
    
    <div class="address-actions-bar">
      <button class="add-address-btn" @click="showAddressForm = true">
        <span class="btn-icon">+</span>
        <span class="btn-text">添加新地址</span>
      </button>
    </div>
    
    <!-- 添加/编辑地址表单 -->
    <div class="address-form-modal" v-if="showAddressForm">
      <div class="modal-overlay" @click="cancelForm"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">{{ isEditing ? '编辑收货地址' : '新增收货地址' }}</h3>
          <button class="modal-close" @click="cancelForm">×</button>
        </div>
        <div class="modal-body">
          <div class="address-form">
            <div class="form-group">
              <label class="form-label">收货人</label>
              <input 
                type="text" 
                class="form-input" 
                v-model="addressForm.receiverName" 
                placeholder="请输入收货人姓名" 
              />
            </div>
            <div class="form-group">
              <label class="form-label">手机号码</label>
              <input 
                type="tel" 
                class="form-input" 
                v-model="addressForm.receiverPhone" 
                placeholder="请输入手机号码" 
              />
            </div>
            <div class="region-selectors">
              <div class="form-group">
                <label class="form-label">省份</label>
                <input 
                  type="text" 
                  class="form-input" 
                  v-model="addressForm.province" 
                  placeholder="请输入省份" 
                />
              </div>
              <div class="form-group">
                <label class="form-label">城市</label>
                <input 
                  type="text" 
                  class="form-input" 
                  v-model="addressForm.city" 
                  placeholder="请输入城市" 
                />
              </div>
              <div class="form-group">
                <label class="form-label">区/县</label>
                <input 
                  type="text" 
                  class="form-input" 
                  v-model="addressForm.district" 
                  placeholder="请输入区/县" 
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">详细地址</label>
              <textarea 
                class="form-input form-textarea" 
                v-model="addressForm.detailAddress" 
                placeholder="请输入详细地址，如街道、门牌号等"
                rows="3"
              ></textarea>
            </div>
            <div class="checkbox-group">
              <input 
                type="checkbox" 
                id="defaultAddress" 
                class="checkbox-input" 
                v-model="addressForm.isDefault" 
              />
              <label for="defaultAddress" class="checkbox-label">设为默认地址</label>
            </div>
            <div class="form-actions">
              <button class="form-btn cancel" @click="cancelForm">取消</button>
              <button class="form-btn submit" @click="saveAddress">{{ isEditing ? '保存修改' : '保存地址' }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { getUserAddresses, createAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address';
import { useUserStore } from '@/stores/user';

const props = defineProps({
  modelValue: {
    type: Number,
    default: null
  }
});

const emit = defineEmits(['update:modelValue']);

const addresses = ref([]);
const selectedAddressId = ref(null);
const showAddressForm = ref(false);
const isEditing = ref(false);
const editingAddressId = ref(null);

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
});

// 监听传入的值变化
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    selectedAddressId.value = newValue;
  }
});

// 初始化时加载地址列表
onMounted(async () => {
  await loadAddresses();
  
  // 如果有传入的默认值，使用它
  if (props.modelValue) {
    selectedAddressId.value = props.modelValue;
  } 
  // 否则如果有默认地址，自动选择默认地址
  else if (addresses.value.length > 0) {
    const defaultAddress = addresses.value.find(addr => addr.isDefault);
    selectedAddressId.value = defaultAddress ? defaultAddress.id : addresses.value[0].id;
    emit('update:modelValue', selectedAddressId.value);
  }
});

// 加载地址列表
async function loadAddresses() {
  try {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      console.error('获取地址列表失败: 未找到用户ID，请先登录');
      
      // 检查当前页面是否已经有登录提示
      if (window.location.pathname.includes('/checkout')) {
        console.log('在结账页面，不主动显示登录提示');
        addresses.value = [];
        return;
      }
      
      // 这里可以尝试重新获取用户信息
      try {
        const userStore = useUserStore();
        if (userStore.isLoggedIn) {
          console.log('尝试重新获取用户信息...');
          await userStore.fetchUserInfo();
          console.log('用户信息已更新，重新尝试加载地址');
          // 重新检查用户ID
          const newUserId = localStorage.getItem('userId');
          if (!newUserId) {
            throw new Error('仍然无法获取用户ID');
          }
          console.log('已获取用户ID:', newUserId);
        } else {
          throw new Error('用户未登录');
        }
      } catch (userError) {
        console.error('重新获取用户信息失败:', userError);
        // 只有在非结账页面才显示提示
        if (!window.location.pathname.includes('/checkout')) {
          alert('您需要登录后才能查看收货地址');
        }
        addresses.value = [];
        return;
      }
    }

    console.log('正在获取用户地址，用户ID:', userId);
    
    // 设置超时处理
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('获取地址列表超时，请检查网络连接')), 10000)
    );
    
    // 带超时的地址获取
    const addressPromise = getUserAddresses();
    const response = await Promise.race([addressPromise, timeoutPromise]);
    
    console.log('地址选择器获取到的地址列表:', response);
    
    // 处理响应格式
    if (response && response.success && Array.isArray(response.data)) {
      addresses.value = response.data;
    } else if (response && Array.isArray(response.data)) {
      addresses.value = response.data;
    } else if (Array.isArray(response)) {
      addresses.value = response;
    } else {
      console.warn('无法解析地址数据格式:', response);
      addresses.value = [];
    }
    
    // 如果没有地址，可以尝试加载默认地址
    if (addresses.value.length === 0) {
      console.log('未找到地址列表，尝试获取默认地址...');
      try {
        const defaultAddressResponse = await getDefaultAddress();
        if (defaultAddressResponse && defaultAddressResponse.data) {
          const defaultAddress = defaultAddressResponse.data;
          addresses.value = [defaultAddress];
          console.log('已加载默认地址:', defaultAddress);
        }
      } catch (defaultAddressError) {
        console.error('获取默认地址失败:', defaultAddressError);
      }
    }
  } catch (error) {
    console.error('获取地址列表出错:', error);
    addresses.value = [];
    
    // 检查是否是已被API拦截器处理过的错误
    if (error && error.handled) {
      console.log('错误已由API拦截器处理，不再显示提示');
      return;
    }
    
    // 检查是否是未授权错误（401）
    if (error && error.status === 401) {
      // 在结账页面不显示提示，由父组件处理
      if (window.location.pathname.includes('/checkout')) {
        console.log('在结账页面检测到401错误，跳过提示');
        return;
      }
      
      // 其他页面提示用户登录
      alert('您的登录已过期，请重新登录');
    } else if (error && error.message) {
      // 显示其他错误信息，但在结账页面不显示
      if (!window.location.pathname.includes('/checkout')) {
        let errorMessage = `获取地址列表失败: ${error.message}`;
        
        // 添加更多调试信息
        const userId = localStorage.getItem('userId');
        if (userId) {
          errorMessage += `\n当前用户ID: ${userId}`;
        } else {
          errorMessage += `\n未找到用户ID，请确保您已登录`;
        }
        
        alert(errorMessage);
      }
    }
  }
}

// 选择地址
function selectAddress(addressId) {
  selectedAddressId.value = addressId;
  emit('update:modelValue', addressId);
}

// 格式化地址
function formatAddress(address) {
  return `${address.province} ${address.city} ${address.district} ${address.detailAddress}`;
}

// 编辑地址
function editAddress(addressId) {
  const address = addresses.value.find(a => a.id === addressId);
  if (address) {
    Object.assign(addressForm, {
      receiverName: address.receiverName,
      receiverPhone: address.receiverPhone,
      province: address.province,
      city: address.city,
      district: address.district,
      detailAddress: address.detailAddress,
      isDefault: address.isDefault
    });
    isEditing.value = true;
    editingAddressId.value = addressId;
    showAddressForm.value = true;
  }
}

// 删除地址确认
function deleteAddressConfirm(addressId) {
  if (confirm('确定要删除这个地址吗？')) {
    deleteAddressById(addressId);
  }
}

// 删除地址
async function deleteAddressById(addressId) {
  try {
    const response = await deleteAddress(addressId);
    console.log('删除地址响应:', response);
    
    // 重新加载地址列表
    await loadAddresses();
    
    // 如果删除的是当前选中的地址，重新选择一个地址
    if (selectedAddressId.value === addressId) {
      if (addresses.value.length > 0) {
        const defaultAddress = addresses.value.find(addr => addr.isDefault);
        selectedAddressId.value = defaultAddress ? defaultAddress.id : addresses.value[0].id;
        emit('update:modelValue', selectedAddressId.value);
      } else {
        selectedAddressId.value = null;
        emit('update:modelValue', null);
      }
    }
  } catch (error) {
    console.error('删除地址出错:', error);
    alert('删除地址失败，请稍后重试');
  }
}

// 取消表单
function cancelForm() {
  resetForm();
  showAddressForm.value = false;
}

// 重置表单
function resetForm() {
  Object.assign(addressForm, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  });
  isEditing.value = false;
  editingAddressId.value = null;
}

// 保存地址
async function saveAddress() {
  try {
    let response;
    
    if (isEditing.value) {
      // 更新地址
      response = await updateAddress(editingAddressId.value, addressForm);
    } else {
      // 创建新地址
      response = await createAddress(addressForm);
    }
    
    console.log('保存地址响应:', response);
    
    // 重新加载地址列表
    await loadAddresses();
    
    // 重置表单和关闭弹窗
    showAddressForm.value = false;
    resetForm();
    
    // 如果是新创建的地址且设为默认，或者地址列表只有一项，自动选中它
    if (!isEditing.value && (addressForm.isDefault || addresses.value.length === 1)) {
      // 查找新添加的地址（通常是列表中的最后一个）
      const newAddress = addresses.value[addresses.value.length - 1];
      if (newAddress) {
        selectedAddressId.value = newAddress.id;
        emit('update:modelValue', newAddress.id);
      }
    }
  } catch (error) {
    console.error('保存地址失败:', error);
    alert(isEditing.value ? '更新地址失败，请稍后重试' : '添加地址失败，请稍后重试');
  }
}
</script>

<style scoped>
.address-selector {
  width: 100%;
  margin-bottom: 30px;
}

.address-section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.address-item {
  position: relative;
  padding: 20px;
  border-radius: 12px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.address-item:hover {
  border-color: #4CAF50;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transform: translateY(-3px);
}

.address-item.active {
  border: 2px solid #4CAF50;
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.15);
  background-color: #f8fff8;
}

.recipient-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.phone {
  color: #666;
  font-size: 14px;
}

.default-tag {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #43A047, #66BB6A);
  color: white;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 0 12px 0 12px;
  font-weight: 500;
  box-shadow: 0 2px 5px rgba(76, 175, 80, 0.2);
}

.address-detail {
  color: #555;
  font-size: 14px;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 10px;
  margin-top: 5px;
  align-self: flex-end;
}

.edit-btn, .delete-btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.edit-btn {
  background-color: #f0f7ff;
  color: #1976D2;
}

.edit-btn:hover {
  background-color: #e3f2fd;
  box-shadow: 0 2px 5px rgba(25, 118, 210, 0.1);
}

.delete-btn {
  background-color: #fff0f0;
  color: #f44336;
}

.delete-btn:hover {
  background-color: #ffebee;
  box-shadow: 0 2px 5px rgba(244, 67, 54, 0.1);
}

.add-address-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: #f8f8f8;
  border: 2px dashed #d0d0d0;
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
  max-width: 200px;
  font-size: 0;
  position: relative;
  overflow: hidden;
}

.add-address-btn:hover {
  background-color: #e8f5e9;
  border-color: #4CAF50;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.1);
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  background-color: #4CAF50;
  color: white;
  border-radius: 50%;
  font-size: 20px;
  font-weight: 400;
  transition: all 0.3s;
}

.add-address-btn:hover .btn-icon {
  transform: scale(1.1);
  box-shadow: 0 0 0 5px rgba(76, 175, 80, 0.1);
}

.btn-text {
  font-size: 15px;
  font-weight: 500;
  color: #555;
  transition: all 0.3s;
}

.add-address-btn:hover .btn-text {
  color: #4CAF50;
}

.address-actions-bar {
  display: flex;
  justify-content: flex-start;
  margin-top: 20px;
}

/* Modal styles */
.address-form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  position: relative;
  background-color: white;
  border-radius: 16px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s ease-out;
  z-index: 1;
}

@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 24px;
  color: #777;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  transition: all 0.2s;
}

.modal-close:hover {
  background-color: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 24px;
}

.address-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #555;
}

.form-input {
  padding: 12px 15px;
  border-radius: 10px;
  border: 1px solid #ddd;
  font-size: 15px;
  transition: all 0.2s;
  background-color: #f9f9f9;
}

.form-input:focus {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.15);
  outline: none;
  background-color: white;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.region-selectors {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
}

.checkbox-input {
  width: 20px;
  height: 20px;
  accent-color: #4CAF50;
  cursor: pointer;
}

.checkbox-label {
  font-size: 15px;
  color: #555;
  cursor: pointer;
}

.checkbox-label:hover {
  color: #4CAF50;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 10px;
}

.form-btn {
  padding: 13px 25px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s;
  border: none;
}

.form-btn.cancel {
  background-color: #f5f5f5;
  color: #555;
}

.form-btn.cancel:hover {
  background-color: #e5e5e5;
}

.form-btn.submit {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 3px 10px rgba(76, 175, 80, 0.2);
}

.form-btn.submit:hover {
  background-color: #43A047;
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
  transform: translateY(-2px);
}

.empty-address {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background-color: #f9f9f9;
  border-radius: 12px;
  border: 1px dashed #ddd;
  text-align: center;
  margin-bottom: 20px;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 15px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0); }
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: #555;
  margin-bottom: 5px;
}

.empty-subtext {
  font-size: 14px;
  color: #888;
}

@media (max-width: 768px) {
  .address-list {
    grid-template-columns: 1fr;
  }
  
  .region-selectors {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    max-width: 100%;
    border-radius: 12px;
  }
}
</style> 