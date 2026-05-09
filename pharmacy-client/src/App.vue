<template>
  <el-container class="client-shell">
    <el-header class="client-header">
      <div class="header-inner">
        <RouterLink class="brand" to="/">
          <el-icon><FirstAidKit /></el-icon>
          <span>安心乡镇药房</span>
        </RouterLink>
        <nav class="main-nav">
          <RouterLink v-for="item in navItems" :key="item.path" :to="item.path">
            {{ item.label }}
          </RouterLink>
        </nav>
      </div>
    </el-header>
    <el-main>
      <RouterView />
    </el-main>
  </el-container>
</template>

<script setup>
const navItems = [
  { label: '首页', path: '/' },
  { label: '药品查询', path: '/medicines' },
  { label: 'AI客服', path: '/chat' },
  { label: '会员中心', path: '/member' },
  { label: '药店信息', path: '/store' }
]
</script>
