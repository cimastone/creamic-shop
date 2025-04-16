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
      <p>您还没有收货地址，请添加</p>
    </div>
    
    <div class="address-actions-bar">
      <button class="add-address-btn" @click="showAddressForm = true">添加新地址</button>
    </div>
    
    <!-- 添加/编辑地址表单 -->
    <div class="address-form-modal" v-if="showAddressForm">
      <div class="modal-content">
        <h3>{{ isEditing ? '编辑地址' : '添加新地址' }}</h3>
        <div class="form-group">
          <label>收货人</label>
          <input type="text" v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </div>
        <div class="form-group">
          <label>手机号码</label>
          <input type="tel" v-model="addressForm.receiverPhone" placeholder="请输入手机号码" />
        </div>
        <div class="form-group">
          <label>省份</label>
          <input type="text" v-model="addressForm.province" placeholder="请输入省份" />
        </div>
        <div class="form-group">
          <label>城市</label>
          <input type="text" v-model="addressForm.city" placeholder="请输入城市" />
        </div>
        <div class="form-group">
          <label>区/县</label>
          <input type="text" v-model="addressForm.district" placeholder="请输入区/县" />
        </div>
        <div class="form-group">
          <label>详细地址</label>
          <textarea 
            v-model="addressForm.detailAddress" 
            placeholder="请输入详细地址，如街道、门牌号等"
          ></textarea>
        </div>
        <div class="form-group checkbox">
          <input type="checkbox" id="defaultAddress" v-model="addressForm.isDefault" />
          <label for="defaultAddress">设为默认地址</label>
        </div>
        <div class="form-actions">
          <button class="cancel-btn" @click="cancelForm">取消</button>
          <button class="save-btn" @click="saveAddress">保存</button>
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
  margin-bottom: 20px;
  position: relative;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 5px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.03);
  background-color: #fff;
}

.address-item:hover {
  border-color: #40a9ff;
  box-shadow: 0 4px 10px rgba(24, 144, 255, 0.1);
  transform: translateY(-2px);
}

.address-item.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
  box-shadow: 0 4px 10px rgba(24, 144, 255, 0.15);
}

.address-info {
  flex: 1;
}

.recipient-info {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.name {
  font-weight: bold;
  margin-right: 12px;
  font-size: 15px;
}

.phone {
  color: #666;
  font-size: 14px;
}

.default-tag {
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 10px;
  display: inline-block;
}

.address-detail {
  color: #555;
  font-size: 14px;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.edit-btn, .delete-btn {
  padding: 5px 12px;
  font-size: 13px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn {
  background-color: #f0f0f0;
  color: #333;
}

.edit-btn:hover {
  background-color: #e0e0e0;
}

.delete-btn {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.delete-btn:hover {
  background-color: #ffccc7;
}

.address-actions-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.add-address-btn {
  padding: 10px 20px;
  background: linear-gradient(to right, #1890ff, #40a9ff);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
  transition: all 0.3s;
}

.add-address-btn:hover {
  background: linear-gradient(to right, #0c80f0, #1890ff);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  transform: translateY(-2px);
}

.empty-address {
  padding: 40px 20px;
  text-align: center;
  color: #999;
  border: 1px dashed #ddd;
  border-radius: 8px;
  background-color: #fafafa;
  margin-bottom: 20px;
}

.address-form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background-color: white;
  padding: 25px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #444;
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  outline: none;
}

.form-group textarea {
  height: 80px;
  resize: vertical;
}

.form-group.checkbox {
  display: flex;
  align-items: center;
}

.form-group.checkbox input {
  width: auto;
  margin-right: 10px;
}

.form-group.checkbox label {
  margin-bottom: 0;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 25px;
}

.cancel-btn,
.save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.save-btn {
  background: linear-gradient(to right, #1890ff, #40a9ff);
  color: white;
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

.save-btn:hover {
  background: linear-gradient(to right, #0c80f0, #1890ff);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}
</style> 