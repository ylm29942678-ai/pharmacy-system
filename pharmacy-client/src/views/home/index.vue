<template>
  <div class="page home-page">
    <section class="hero panel">
      <div>
        <el-tag type="success" size="large">{{ storeInfo.status }}</el-tag>
        <h1>安心乡镇药房</h1>
        <p>面向周边居民的常用药查询、到店购药提醒和药师咨询入口。</p>
        <div class="hero-actions">
          <el-input v-model="keyword" size="large" placeholder="输入药品名称，如 布洛芬" clearable>
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" size="large" :icon="Search" @click="goSearch">查询药品</el-button>
        </div>
      </div>
      <div class="store-card">
        <div class="info-row"><span>营业时间</span><strong>{{ storeInfo.hours }}</strong></div>
        <div class="info-row"><span>联系电话</span><strong>{{ storeInfo.phone }}</strong></div>
        <div class="info-row"><span>药店地址</span><strong>{{ storeInfo.address }}</strong></div>
      </div>
    </section>

    <section class="section grid-2">
      <div class="panel">
        <h2 class="panel-title">常用分类</h2>
        <div class="category-grid">
          <RouterLink v-for="item in categories" :key="item" :to="{ path: '/medicines', query: { category: item } }">
            <el-icon><Collection /></el-icon>
            <span>{{ item }}</span>
          </RouterLink>
        </div>
      </div>
      <div class="panel service-entry">
        <h2 class="panel-title">AI 智能客服</h2>
        <p class="muted">不确定该买哪类药时，可以先描述症状或查询到店购药注意事项。</p>
        <el-button type="primary" :icon="ChatDotRound" @click="$router.push('/chat')">咨询药店智能服务助手</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ChatDotRound } from '@element-plus/icons-vue'
import { categories, storeInfo } from '@/data/mock'

const router = useRouter()
const keyword = ref('')

const goSearch = () => {
  router.push({ path: '/medicines', query: keyword.value ? { keyword: keyword.value } : {} })
}
</script>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.8fr);
  gap: 28px;
  align-items: center;
}

.hero h1 {
  margin: 18px 0 12px;
  font-size: 42px;
  line-height: 1.15;
}

.hero p {
  max-width: 620px;
  margin: 0 0 24px;
  color: var(--client-muted);
  font-size: 17px;
}

.hero-actions {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) auto;
  gap: 12px;
}

.store-card {
  display: grid;
  gap: 12px;
  padding: 20px;
  border-radius: 8px;
  background: var(--client-surface-soft);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.category-grid a {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 92px;
  gap: 8px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  color: var(--client-primary-dark);
  background: var(--client-surface-soft);
  font-weight: 800;
}

.category-grid .el-icon {
  font-size: 24px;
}

.service-entry {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}

@media (max-width: 860px) {
  .hero,
  .hero-actions {
    grid-template-columns: 1fr;
  }

  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
