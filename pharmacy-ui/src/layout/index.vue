<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <h2>乡镇药店管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-icon><User /></el-icon>
            管理员
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="aside">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item
            v-for="route in menuRoutes"
            :key="route.path"
            :index="route.path"
          >
            <el-icon>
              <component :is="route.meta.icon" />
            </el-icon>
            <span>{{ route.meta.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)

const menuRoutes = [
  { path: '/dashboard', meta: { title: '控制台', icon: 'Monitor' } },
  { path: '/customer', meta: { title: '顾客管理', icon: 'User' } },
  { path: '/medicine', meta: { title: '药品管理', icon: 'Box' } },
  { path: '/supplier', meta: { title: '供应商管理', icon: 'OfficeBuilding' } },
  { path: '/user', meta: { title: '用户管理', icon: 'Avatar' } },
  { path: '/purchase-order', meta: { title: '采购订单', icon: 'Document' } },
  { path: '/purchase-item', meta: { title: '采购明细', icon: 'Tickets' } },
  { path: '/sale-order', meta: { title: '销售订单', icon: 'ShoppingCart' } },
  { path: '/sale-item', meta: { title: '销售明细', icon: 'List' } },
  { path: '/stock', meta: { title: '库存管理', icon: 'Goods' } },
  { path: '/stock-check', meta: { title: '盘点管理', icon: 'DataAnalysis' } },
  { path: '/sys-log', meta: { title: '系统日志', icon: 'Document' } }
]
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.aside {
  background-color: #304156;
  overflow-x: hidden;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
