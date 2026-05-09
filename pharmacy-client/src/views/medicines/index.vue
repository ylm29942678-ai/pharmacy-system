<template>
  <div class="page">
    <section class="panel">
      <div class="page-heading">
        <div>
          <h1 class="panel-title">药品查询</h1>
          <p class="muted">数据来自后端用户端药品接口，仅展示顾客可见信息。</p>
        </div>
        <el-button :icon="Refresh" @click="loadMedicines">刷新</el-button>
      </div>

      <el-form :model="filters" class="filter-form" label-width="92px">
        <el-form-item label="药品名称">
          <el-input v-model="filters.keyword" placeholder="输入药品名称或别名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="filters.type" placeholder="全部" clearable>
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
            <el-option label="器械" value="器械" />
          </el-select>
        </el-form-item>
        <el-form-item label="剂型">
          <el-select v-model="filters.dosageForm" placeholder="全部" clearable>
            <el-option v-for="item in dosageOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="处方药">
          <el-select v-model="filters.isRx" placeholder="全部" clearable>
            <el-option label="非处方药" :value="0" />
            <el-option label="处方药" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="filters.stockStatus" placeholder="全部" clearable>
            <el-option label="有货" value="有货" />
            <el-option label="库存较少" value="库存较少" />
            <el-option label="无货" value="无货" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <div class="filter-actions">
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button :icon="Refresh" @click="resetFilters">重置</el-button>
          </div>
        </el-form-item>
      </el-form>
    </section>

    <section v-loading="loading" class="section grid-3">
      <article v-for="item in medicines" :key="item.id" class="medicine-card">
        <h3>{{ item.name }}</h3>
        <div class="medicine-meta">
          <el-tag>{{ item.spec || '暂无规格' }}</el-tag>
          <el-tag type="info">{{ item.dosageForm || '暂无剂型' }}</el-tag>
          <el-tag :type="item.isRx === 1 ? 'warning' : 'success'">
            {{ item.isRx === 1 ? '处方药' : '非处方药' }}
          </el-tag>
          <el-tag :type="stockTagType(item.stockStatus)">{{ item.stockStatus }}</el-tag>
        </div>
        <p class="muted">生产厂家：{{ item.manufacturer || '暂无' }}</p>
        <p class="muted">库存数量：{{ item.stockCount ?? 0 }} {{ item.unit || '件' }}</p>
        <div class="medicine-footer">
          <span class="price">¥{{ formatPrice(item.retailPrice) }}</span>
          <el-button type="primary" text @click="$router.push(`/medicines/${item.id}`)">查看详情</el-button>
        </div>
      </article>
    </section>

    <el-empty v-if="!loading && medicines.length === 0" description="暂无匹配药品" />
    <el-pagination
      v-else
      class="pager"
      layout="prev, pager, next, total"
      :page-size="page.size"
      :total="page.total"
      v-model:current-page="page.current"
      @current-change="loadMedicines"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getClientMedicines } from '@/api/client-medicine'

const route = useRoute()
const loading = ref(false)
const medicines = ref([])
const page = reactive({
  current: 1,
  size: 6,
  total: 0
})
const filters = reactive({
  keyword: route.query.keyword || '',
  type: '',
  dosageForm: '',
  isRx: '',
  stockStatus: ''
})
const dosageOptions = ['片剂', '胶囊', '颗粒', '针剂', '膏剂', '散剂', '外用液体']

const buildParams = () => ({
  current: page.current,
  size: page.size,
  keyword: filters.keyword || undefined,
  type: filters.type || undefined,
  dosageForm: filters.dosageForm || undefined,
  isRx: filters.isRx === '' ? undefined : filters.isRx,
  stockStatus: filters.stockStatus || undefined
})

const loadMedicines = async () => {
  loading.value = true
  try {
    const { data } = await getClientMedicines(buildParams())
    if (data.code !== 200) {
      throw new Error(data.message || '药品查询失败')
    }
    const result = data.data || {}
    medicines.value = result.records || []
    page.total = Number(result.total || 0)
    page.current = Number(result.current || page.current)
    page.size = Number(result.size || page.size)
  } catch (error) {
    ElMessage.error('药品查询失败，请确认后端服务已启动')
    medicines.value = []
    page.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.current = 1
  loadMedicines()
}

const resetFilters = () => {
  filters.keyword = ''
  filters.type = ''
  filters.dosageForm = ''
  filters.isRx = ''
  filters.stockStatus = ''
  handleSearch()
}

const stockTagType = (status) => {
  if (status === '有货') return 'success'
  if (status === '库存较少') return 'warning'
  return 'danger'
}

const formatPrice = (price) => Number(price || 0).toFixed(2)

watch(
  () => route.query.keyword,
  (keyword) => {
    filters.keyword = keyword || ''
    handleSearch()
  }
)

onMounted(loadMedicines)
</script>

<style scoped>
.page-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 16px;
}

.page-heading p {
  margin: 6px 0 0;
}

.filter-form {
  display: grid;
  grid-template-columns: repeat(3, minmax(240px, 1fr));
  gap: 8px 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 10px;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.pager {
  justify-content: flex-end;
  margin-top: 22px;
}

@media (max-width: 980px) {
  .page-heading,
  .filter-actions {
    flex-direction: column;
  }

  .filter-form {
    grid-template-columns: 1fr;
  }
}
</style>
