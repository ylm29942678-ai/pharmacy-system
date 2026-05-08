<template>
  <el-container class="layout-container">
    <el-container>
      <el-aside width="220px" class="aside">
        <div class="brand">
          <div class="brand-icon">
            <el-icon><FirstAidKit /></el-icon>
          </div>
          <h1>乡镇药店管理系统</h1>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
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
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-popover
              v-model:visible="toolMenuVisible"
              placement="bottom-start"
              trigger="click"
              :width="260"
              popper-class="tool-menu-popper"
            >
              <template #reference>
                <el-button class="tool-trigger" :icon="Menu" text />
              </template>
              <div class="tool-menu">
                <button class="tool-menu-item" type="button" @click="toggleTheme">
                  <span class="tool-menu-icon">
                    <el-icon>
                      <component :is="isDarkTheme ? Sunny : Moon" />
                    </el-icon>
                  </span>
                  <span class="tool-menu-text">
                    <strong>主题设置</strong>
                    <small>切换浅色/深色模式</small>
                  </span>
                </button>
                <button class="tool-menu-item" type="button" @click="toggleFullscreen">
                  <span class="tool-menu-icon">
                    <el-icon><FullScreen /></el-icon>
                  </span>
                  <span class="tool-menu-text">
                    <strong>{{ isFullscreen ? '退出全屏' : '全屏模式' }}</strong>
                    <small>进入或退出全屏</small>
                  </span>
                </button>
                <button class="tool-menu-item" type="button" @click="refreshCurrentPage">
                  <span class="tool-menu-icon">
                    <el-icon><Refresh /></el-icon>
                  </span>
                  <span class="tool-menu-text">
                    <strong>刷新当前页面</strong>
                    <small>重新加载当前页面</small>
                  </span>
                </button>
              </div>
            </el-popover>
            <h2>{{ currentPageTitle }}</h2>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ userInfo.realName || '用户' }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="main">
          <router-view :key="routerViewKey" />
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onBeforeUnmount, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { FirstAidKit, FullScreen, Menu, Moon, Refresh, Sunny, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = ref({})
const toolMenuVisible = ref(false)
const refreshKey = ref(0)
const isDarkTheme = ref(false)
const isFullscreen = ref(false)
const themeStorageKey = 'pharmacy-theme'

const allMenuRoutes = [
  { path: '/dashboard', meta: { title: '控制台', icon: 'Monitor' } },
  { path: '/customer', meta: { title: '顾客管理', icon: 'User' } },
  { path: '/medicine', meta: { title: '药品管理', icon: 'Box' } },
  { path: '/supplier', meta: { title: '供应商管理', icon: 'OfficeBuilding' } },
  { path: '/user', meta: { title: '用户管理', icon: 'Avatar', roles: ['admin'] } },
  { path: '/purchase-order', meta: { title: '采购订单', icon: 'Document' } },
  { path: '/purchase-item', meta: { title: '采购明细', icon: 'Tickets' } },
  { path: '/sale-order', meta: { title: '销售订单', icon: 'ShoppingCart' } },
  { path: '/sale-item', meta: { title: '销售明细', icon: 'List' } },
  { path: '/stock', meta: { title: '库存管理', icon: 'Goods' } },
  { path: '/stock-check', meta: { title: '盘点管理', icon: 'DataAnalysis' } },
  { path: '/sys-log', meta: { title: '系统日志', icon: 'Document', roles: ['admin'] } }
]

const menuRoutes = computed(() => {
  return allMenuRoutes.filter(route => {
    if (!route.meta.roles) return true
    return route.meta.roles.includes(userInfo.value.role)
  })
})

const activeMenu = computed(() => route.path)
const currentPageTitle = computed(() => route.meta.title || '控制台')
const routerViewKey = computed(() => `${route.fullPath}-${refreshKey.value}`)

const getUserInfo = () => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
}

const applyTheme = (theme) => {
  isDarkTheme.value = theme === 'dark'
  document.documentElement.classList.toggle('dark', isDarkTheme.value)
  localStorage.setItem(themeStorageKey, isDarkTheme.value ? 'dark' : 'light')
}

const initTheme = () => {
  const savedTheme = localStorage.getItem(themeStorageKey)
  applyTheme(savedTheme === 'dark' ? 'dark' : 'light')
}

const toggleTheme = () => {
  applyTheme(isDarkTheme.value ? 'light' : 'dark')
  toolMenuVisible.value = false
}

const syncFullscreenState = () => {
  isFullscreen.value = Boolean(document.fullscreenElement)
}

const toggleFullscreen = async () => {
  try {
    if (!document.fullscreenElement) {
      await document.documentElement.requestFullscreen()
    } else {
      await document.exitFullscreen()
    }
    syncFullscreenState()
  } catch (error) {
    ElMessage.error('当前浏览器不支持或阻止了全屏操作')
  } finally {
    toolMenuVisible.value = false
  }
}

const refreshCurrentPage = () => {
  refreshKey.value += 1
  toolMenuVisible.value = false
  ElMessage.success('页面已刷新')
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('userInfo')
      ElMessage.success('退出成功')
      router.push('/login')
    } catch {
    }
  }
}

onMounted(() => {
  getUserInfo()
  initTheme()
  syncFullscreenState()
  document.addEventListener('fullscreenchange', syncFullscreenState)
})

onBeforeUnmount(() => {
  document.removeEventListener('fullscreenchange', syncFullscreenState)
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: var(--app-bg);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--app-surface);
  border-bottom: 1px solid var(--app-border);
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.06);
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.header-left h2 {
  font-size: 17px;
  font-weight: 700;
  color: var(--app-text);
  margin: 0;
}

.tool-trigger {
  width: 40px;
  height: 40px;
  color: var(--app-text);
  font-size: 22px;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--app-text);
  font-size: 14px;
  outline: none;
}

.aside {
  background-color: var(--app-surface);
  border-right: 1px solid var(--app-border);
  overflow-x: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 64px;
  padding: 0 18px;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 8px;
  color: #fff;
  font-size: 20px;
  background: linear-gradient(135deg, #409eff 0%, #0f62fe 100%);
}

.brand h1 {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  color: var(--app-text);
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 64px);
  padding: 12px;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 46px;
  margin-bottom: 8px;
  border-radius: 8px;
  color: var(--app-text);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: #1677ff;
  background: var(--app-hover);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: linear-gradient(135deg, #1677ff 0%, #0f62fe 100%);
}

.main {
  background-color: var(--app-bg);
  padding: 20px;
  overflow-y: auto;
}
</style>
