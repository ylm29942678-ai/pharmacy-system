<template>
  <div class="page">
    <section class="panel">
      <h1 class="panel-title">药品查询</h1>
      <el-form :model="filters" class="filter-form" label-width="92px">
        <el-form-item label="药品名称">
          <el-input v-model="filters.keyword" placeholder="输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="filters.type" placeholder="全部" clearable>
            <el-option label="西药" value="西药" />
            <el-option label="抗生素" value="抗生素" />
            <el-option label="消毒用品" value="消毒用品" />
          </el-select>
        </el-form-item>
        <el-form-item label="剂型">
          <el-select v-model="filters.dosage" placeholder="全部" clearable>
            <el-option label="胶囊剂" value="胶囊剂" />
            <el-option label="片剂" value="片剂" />
            <el-option label="散剂" value="散剂" />
            <el-option label="外用液体" value="外用液体" />
          </el-select>
        </el-form-item>
        <el-form-item label="处方药">
          <el-select v-model="filters.prescription" placeholder="全部" clearable>
            <el-option label="非处方药" :value="false" />
            <el-option label="处方药" :value="true" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="filters.stockStatus" placeholder="全部" clearable>
            <el-option label="有货" value="有货" />
            <el-option label="库存较少" value="库存较少" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Refresh" @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="section grid-3">
      <article v-for="item in pagedMedicines" :key="item.id" class="medicine-card">
        <h3>{{ item.name }}</h3>
        <div class="medicine-meta">
          <el-tag>{{ item.spec }}</el-tag>
          <el-tag type="info">{{ item.dosage }}</el-tag>
          <el-tag :type="item.prescription ? 'warning' : 'success'">
            {{ item.prescription ? '处方药' : '非处方药' }}
          </el-tag>
          <el-tag :type="item.stockStatus === '有货' ? 'success' : 'warning'">{{ item.stockStatus }}</el-tag>
        </div>
        <p class="muted">生产厂家：{{ item.manufacturer }}</p>
        <div class="medicine-footer">
          <span class="price">¥{{ item.price.toFixed(2) }}</span>
          <el-button type="primary" text @click="$router.push(`/medicines/${item.id}`)">查看详情</el-button>
        </div>
      </article>
    </section>

    <el-empty v-if="filteredMedicines.length === 0" description="暂无匹配药品" />
    <el-pagination
      v-else
      class="pager"
      layout="prev, pager, next"
      :page-size="pageSize"
      :total="filteredMedicines.length"
      v-model:current-page="currentPage"
    />
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { medicines } from '@/data/mock'

const route = useRoute()
const pageSize = 6
const currentPage = ref(1)
const filters = reactive({
  keyword: route.query.keyword || '',
  category: route.query.category || '',
  type: '',
  dosage: '',
  prescription: '',
  stockStatus: ''
})

const filteredMedicines = computed(() => {
  return medicines.filter((item) => {
    const keywordMatched = !filters.keyword || item.name.includes(filters.keyword)
    const categoryMatched = !filters.category || item.category === filters.category
    const typeMatched = !filters.type || item.type === filters.type
    const dosageMatched = !filters.dosage || item.dosage === filters.dosage
    const prescriptionMatched = filters.prescription === '' || item.prescription === filters.prescription
    const stockMatched = !filters.stockStatus || item.stockStatus === filters.stockStatus
    return keywordMatched && categoryMatched && typeMatched && dosageMatched && prescriptionMatched && stockMatched
  })
})

const pagedMedicines = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredMedicines.value.slice(start, start + pageSize)
})

const resetFilters = () => {
  filters.keyword = ''
  filters.category = ''
  filters.type = ''
  filters.dosage = ''
  filters.prescription = ''
  filters.stockStatus = ''
}

watch(filters, () => {
  currentPage.value = 1
})
</script>

<style scoped>
.filter-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 8px 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.pager {
  justify-content: flex-end;
  margin-top: 22px;
}

@media (max-width: 980px) {
  .filter-form {
    grid-template-columns: 1fr;
  }
}
</style>
