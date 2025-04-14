<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人资料</h1>
    </div>

    <div class="profile-content">
      <!-- 用户基本信息 -->
      <div class="profile-section">
        <h2>基本信息</h2>
        <div class="profile-card">
          <div class="profile-info">
            <div class="info-item">
              <label>用户名</label>
              <div>{{ userStore.userInfo?.username || '-' }}</div>
            </div>
            <div class="info-item">
              <label>昵称</label>
              <div>{{ userStore.userInfo?.nickname || '-' }}</div>
            </div>
            <div class="info-item">
              <label>邮箱</label>
              <div>{{ userStore.userInfo?.email || '-' }}</div>
            </div>
            <div class="info-item">
              <label>手机号</label>
              <div>{{ userStore.userInfo?.phone || '-' }}</div>
            </div>
          </div>
          <div class="profile-actions">
            <button class="edit-profile-btn" @click="editProfile">编辑资料</button>
          </div>
        </div>
      </div>

      <!-- 收货地址管理 -->
      <div class="profile-section">
        <h2>收货地址</h2>
        <div class="address-list-container">
          <AddressSelector />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import AddressSelector from '@/components/AddressSelector.vue'

const userStore = useUserStore()

// 编辑个人资料
const editProfile = () => {
  // 暂未实现，可以显示表单或跳转到编辑页面
  alert('编辑个人资料功能即将上线')
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-size: 28px;
  color: var(--primary-color);
  font-weight: 600;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.profile-section {
  margin-bottom: 20px;
}

.profile-section h2 {
  font-size: 20px;
  margin-bottom: 15px;
  color: var(--text-primary);
  font-weight: 500;
}

.profile-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 25px;
}

.profile-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  margin-bottom: 15px;
}

.info-item label {
  display: block;
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 5px;
}

.info-item div {
  font-size: 16px;
  color: var(--text-primary);
}

.profile-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.edit-profile-btn {
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-profile-btn:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(25, 118, 210, 0.3);
}

.address-list-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 25px;
}

@media (max-width: 768px) {
  .profile-info {
    grid-template-columns: 1fr;
  }
}
</style> 