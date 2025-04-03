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
    const response = await getUserAddresses();
    // 适配新的响应格式
    if (response.data) {
      addresses.value = response.data || [];
    } else {
      addresses.value = response || [];
    }
  } catch (error) {
    console.error('获取地址列表出错:', error);
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
    // 适配新的响应格式
    if ((response.data && response.data.code === 0) || response.code === 0) {
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
    } else {
      alert('删除地址失败');
    }
  } catch (error) {
    console.error('删除地址出错:', error);
    alert('删除地址出错');
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
    
    // 适配新的响应格式
    if ((response.data && response.data.code === 0) || response.code === 0) {
      await loadAddresses();
      
      // 如果设为默认地址或是首个地址，自动选中它
      let savedAddress;
      if (response.data && response.data.data) {
        savedAddress = response.data.data;
      } else if (response.data) {
        savedAddress = response.data;
      } else {
        savedAddress = addresses.value.find(a => a.isDefault) || addresses.value[0];
      }
      
      if (savedAddress && (addressForm.isDefault || addresses.value.length === 1)) {
        selectedAddressId.value = savedAddress.id;
        emit('update:modelValue', savedAddress.id);
      }
      
      showAddressForm.value = false;
      resetForm();
    } else {
      alert(isEditing.value ? '更新地址失败' : '添加地址失败');
    }
  } catch (error) {
    console.error(isEditing.value ? '更新地址出错:' : '添加地址出错:', error);
    alert(isEditing.value ? '更新地址出错' : '添加地址出错');
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
  gap: 10px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #40a9ff;
}

.address-item.active {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.recipient-info {
  margin-bottom: 5px;
}

.name {
  font-weight: bold;
  margin-right: 10px;
}

.phone {
  color: #666;
}

.default-tag {
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 10px;
}

.address-detail {
  color: #333;
  font-size: 14px;
}

.address-actions {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.edit-btn, .delete-btn {
  padding: 5px 10px;
  font-size: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-btn {
  background-color: #f0f0f0;
  color: #333;
}

.delete-btn {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.address-actions-bar {
  margin-top: 15px;
  text-align: center;
}

.add-address-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.empty-address {
  padding: 30px;
  text-align: center;
  color: #999;
  border: 1px dashed #ddd;
  border-radius: 8px;
}

.address-form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
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
  margin-right: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn,
.save-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.save-btn {
  background-color: #1890ff;
  color: white;
}
</style> 