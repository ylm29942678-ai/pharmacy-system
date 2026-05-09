<template>
  <div class="page" v-if="medicine">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/medicines' }">药品查询</el-breadcrumb-item>
      <el-breadcrumb-item>{{ medicine.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <section class="section grid-2">
      <div class="panel detail-main">
        <div class="title-line">
          <h1>{{ medicine.name }}</h1>
          <el-tag :type="medicine.prescription ? 'warning' : 'success'" size="large">
            {{ medicine.prescription ? '处方药' : '非处方药' }}
          </el-tag>
        </div>
        <p class="muted">{{ medicine.usage }}</p>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="规格">{{ medicine.spec }}</el-descriptions-item>
          <el-descriptions-item label="剂型">{{ medicine.dosage }}</el-descriptions-item>
          <el-descriptions-item label="零售价">¥{{ medicine.price.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="生产厂家">{{ medicine.manufacturer }}</el-descriptions-item>
          <el-descriptions-item label="库存状态">
            <el-tag :type="medicine.stockStatus === '有货' ? 'success' : 'warning'">
              {{ medicine.stockStatus }}，约 {{ medicine.stockCount }} 件
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <aside class="panel">
        <h2 class="panel-title">到店购买提示</h2>
        <div class="info-list">
          <div class="info-row"><span>药店</span><strong>{{ storeInfo.name }}</strong></div>
          <div class="info-row"><span>电话</span><strong>{{ storeInfo.phone }}</strong></div>
          <div class="info-row"><span>地址</span><strong>{{ storeInfo.address }}</strong></div>
          <div class="info-row"><span>营业时间</span><strong>{{ storeInfo.hours }}</strong></div>
        </div>
        <p class="notice">用药请遵医嘱或在药师指导下购买，处方药需凭处方购买。</p>
        <div class="detail-actions">
          <el-button type="primary" :icon="ChatDotRound" @click="$router.push('/chat')">咨询此药</el-button>
          <el-button :icon="Back" @click="$router.push('/medicines')">返回查询</el-button>
        </div>
      </aside>
    </section>
  </div>
  <el-empty v-else description="未找到该药品">
    <el-button type="primary" @click="$router.push('/medicines')">返回药品查询</el-button>
  </el-empty>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Back, ChatDotRound } from '@element-plus/icons-vue'
import { medicines, storeInfo } from '@/data/mock'

const route = useRoute()
const medicine = computed(() => medicines.find((item) => item.id === Number(route.params.id)))
</script>

<style scoped>
.title-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.title-line h1 {
  margin: 0;
  font-size: 30px;
}

.detail-main {
  display: grid;
  gap: 18px;
}

.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 18px;
}
</style>
