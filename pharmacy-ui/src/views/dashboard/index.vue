<template>
  <div class="dashboard-page">
    <div class="stat-grid">
      <el-card class="stat-card">
        <div class="stat-body">
          <div>
            <div class="stat-label">当日营业额</div>
            <div class="stat-value">¥{{ statistics.todayTurnover || 0 }}</div>
            <div class="stat-note">今日门店销售额</div>
          </div>
          <div class="stat-icon stat-blue">
            <el-icon><Money /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-body">
          <div>
            <div class="stat-label">订单总数</div>
            <div class="stat-value">{{ statistics.orderCount || 0 }}</div>
            <div class="stat-note">销售订单累计</div>
          </div>
          <div class="stat-icon stat-green">
            <el-icon><Document /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-body">
          <div>
            <div class="stat-label">库存总数</div>
            <div class="stat-value">{{ statistics.stockCount || 0 }}</div>
            <div class="stat-note">当前库存批次</div>
          </div>
          <div class="stat-icon stat-orange">
            <el-icon><Goods /></el-icon>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-body">
          <div>
            <div class="stat-label">近效期药品</div>
            <div class="stat-value">{{ statistics.nearExpireCount || 0 }}</div>
            <div class="stat-note">30天内到期</div>
          </div>
          <div class="stat-icon stat-red">
            <el-icon><Warning /></el-icon>
          </div>
        </div>
      </el-card>
    </div>

    <div class="dashboard-grid">
      <el-card class="dashboard-card quick-card">
        <template #header>
          <div class="card-header">
            <span>快捷操作</span>
          </div>
        </template>
        <div class="quick-grid">
          <button class="quick-item quick-blue" type="button" @click="$router.push('/medicine')">
            <span>
              <strong>新增药品</strong>
              <small>录入药品信息</small>
            </span>
            <el-icon><Plus /></el-icon>
          </button>
          <button class="quick-item quick-green" type="button" @click="$router.push('/purchase-order/form')">
            <span>
              <strong>采购入库</strong>
              <small>添加采购订单</small>
            </span>
            <el-icon><ShoppingCart /></el-icon>
          </button>
          <button class="quick-item quick-purple" type="button" @click="$router.push('/sale-order/form')">
            <span>
              <strong>销售开单</strong>
              <small>快速开具单据</small>
            </span>
            <el-icon><Tickets /></el-icon>
          </button>
          <button class="quick-item quick-orange" type="button" @click="$router.push('/stock-check')">
            <span>
              <strong>库存盘点</strong>
              <small>进行库存盘点</small>
            </span>
            <el-icon><Goods /></el-icon>
          </button>
          <button class="quick-item quick-violet" type="button" @click="$router.push('/customer')">
            <span>
              <strong>顾客查询</strong>
              <small>查找顾客信息</small>
            </span>
            <el-icon><User /></el-icon>
          </button>
          <button class="quick-item quick-cyan" type="button" @click="handleSupplierCheck">
            <span>
              <strong>供应商对账</strong>
              <small>核对账目明细</small>
            </span>
            <el-icon><DocumentCopy /></el-icon>
          </button>
        </div>
      </el-card>

      <el-card class="dashboard-card todo-card">
        <template #header>
          <div class="card-header">
            <span>待办事项</span>
          </div>
        </template>
        <div class="todo-list">
          <button class="todo-item" type="button" @click="$router.push('/stock')">
            <span class="todo-icon todo-orange"><el-icon><Goods /></el-icon></span>
            <span>
              <strong>库存总览</strong>
              <small>共有 {{ statistics.stockCount || 0 }} 条库存批次</small>
            </span>
            <el-icon class="todo-arrow"><ArrowRight /></el-icon>
          </button>
          <button class="todo-item" type="button" @click="$router.push('/stock')">
            <span class="todo-icon todo-red"><el-icon><Warning /></el-icon></span>
            <span>
              <strong>近效期药品</strong>
              <small>有 {{ statistics.nearExpireCount || 0 }} 个药品30天内到期</small>
            </span>
            <el-icon class="todo-arrow"><ArrowRight /></el-icon>
          </button>
          <button class="todo-item" type="button" @click="$router.push('/sale-order')">
            <span class="todo-icon todo-blue"><el-icon><Document /></el-icon></span>
            <span>
              <strong>待处理订单</strong>
              <small>查看最新销售订单</small>
            </span>
            <el-icon class="todo-arrow"><ArrowRight /></el-icon>
          </button>
        </div>
      </el-card>
    </div>

    <div class="dashboard-grid lower-grid">
      <el-card class="dashboard-card">
        <template #header>
          <div class="card-header">
            <span>最新订单</span>
            <el-button text type="primary" @click="$router.push('/sale-order')">查看更多</el-button>
          </div>
        </template>
        <el-table :data="recentOrders" class="soft-table" stripe style="width: 100%;">
          <el-table-column prop="orderId" label="订单号" min-width="130" />
          <el-table-column prop="totalPrice" label="金额" width="120">
            <template #default="{ row }">
              ¥{{ row.totalPrice }}
            </template>
          </el-table-column>
          <el-table-column prop="payType" label="支付方式" width="120" />
          <el-table-column prop="createTime" label="创建时间" min-width="180" />
        </el-table>
      </el-card>

      <el-card class="dashboard-card">
        <template #header>
          <div class="card-header">
            <span>销售趋势</span>
            <el-button text>近7天</el-button>
          </div>
        </template>
        <div class="trend-chart">
          <svg viewBox="0 0 520 260" role="img" aria-label="销售趋势">
            <g class="grid-lines">
              <line x1="36" y1="40" x2="500" y2="40" />
              <line x1="36" y1="90" x2="500" y2="90" />
              <line x1="36" y1="140" x2="500" y2="140" />
              <line x1="36" y1="190" x2="500" y2="190" />
              <line x1="36" y1="230" x2="500" y2="230" />
            </g>
            <polyline class="trend-area" points="36,205 112,182 188,145 264,80 340,162 416,148 492,186 492,230 36,230" />
            <polyline class="trend-line" points="36,205 112,182 188,145 264,80 340,162 416,148 492,186" />
            <g class="trend-dots">
              <circle cx="36" cy="205" r="4" />
              <circle cx="112" cy="182" r="4" />
              <circle cx="188" cy="145" r="4" />
              <circle cx="264" cy="80" r="4" />
              <circle cx="340" cy="162" r="4" />
              <circle cx="416" cy="148" r="4" />
              <circle cx="492" cy="186" r="4" />
            </g>
            <g class="chart-labels">
              <text x="30" y="252">05-02</text>
              <text x="106" y="252">05-03</text>
              <text x="182" y="252">05-04</text>
              <text x="258" y="252">05-05</text>
              <text x="334" y="252">05-06</text>
              <text x="410" y="252">05-07</text>
              <text x="486" y="252">05-08</text>
            </g>
          </svg>
          <div class="chart-legend">
            <span></span>
            营业额（元）
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  ArrowRight, Money, Document, Goods, Warning, ShoppingCart,
  Plus, User, Tickets, DocumentCopy 
} from '@element-plus/icons-vue'
import { getStatistics, getRecentOrders, getRecentLogs } from '@/api/dashboard'

const statistics = ref({})
const recentOrders = ref([])
const recentLogs = ref([])

const fetchStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === 200) {
      statistics.value = res.data
    } else {
      ElMessage.error(res.message || '获取统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

const fetchRecentOrders = async () => {
  try {
    const res = await getRecentOrders()
    if (res.code === 200) {
      recentOrders.value = res.data
    } else {
      ElMessage.error(res.message || '获取订单失败')
    }
  } catch (error) {
    ElMessage.error('获取订单失败')
  }
}

const fetchRecentLogs = async () => {
  try {
    const res = await getRecentLogs()
    if (res.code === 200) {
      recentLogs.value = res.data
    } else {
      ElMessage.error(res.message || '获取日志失败')
    }
  } catch (error) {
    ElMessage.error('获取日志失败')
  }
}

const handleBatchPrice = () => {
  ElMessage.info('批量调价功能开发中...')
}

const handleCategory = () => {
  ElMessage.info('分类管理功能开发中...')
}

const handleMemberSettle = () => {
  ElMessage.info('会员结算功能开发中...')
}

const handleSupplierCheck = () => {
  ElMessage.info('供应商对账功能开发中...')
}

onMounted(() => {
  fetchStatistics()
  fetchRecentOrders()
  fetchRecentLogs()
})
</script>

<style scoped>
.dashboard-page {
  width: 100%;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover);
}

.stat-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 104px;
}

.stat-label {
  margin-bottom: 12px;
  color: var(--app-text);
  font-size: 15px;
  font-weight: 700;
}

.stat-value {
  margin-bottom: 10px;
  color: var(--app-text-strong);
  font-size: 30px;
  line-height: 1;
  font-weight: 800;
}

.stat-note {
  color: var(--app-text-muted);
  font-size: 13px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-blue {
  color: #0f62fe;
  background: rgba(15, 98, 254, 0.12);
}

.stat-green {
  color: #16a34a;
  background: rgba(22, 163, 74, 0.13);
}

.stat-orange {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.14);
}

.stat-red {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.13);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(360px, 0.95fr);
  gap: 20px;
  margin-bottom: 20px;
}

.lower-grid {
  grid-template-columns: minmax(0, 1.1fr) minmax(420px, 0.9fr);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--app-text);
  font-size: 16px;
  font-weight: 800;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.quick-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 86px;
  padding: 18px 20px;
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background: var(--app-surface-soft);
  cursor: pointer;
  text-align: left;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.quick-item:hover {
  transform: translateY(-2px);
  border-color: rgba(15, 98, 254, 0.32);
  box-shadow: 0 12px 24px rgba(15, 98, 254, 0.08);
}

.quick-item strong {
  display: block;
  margin-bottom: 10px;
  color: var(--app-text);
  font-size: 15px;
}

.quick-item small {
  color: var(--app-text-muted);
  font-size: 13px;
}

.quick-item .el-icon {
  font-size: 34px;
}

.quick-blue {
  background: linear-gradient(135deg, rgba(15, 98, 254, 0.08), rgba(15, 98, 254, 0.02));
}

.quick-blue .el-icon,
.quick-cyan .el-icon {
  color: #0f62fe;
}

.quick-green {
  background: linear-gradient(135deg, rgba(22, 163, 74, 0.08), rgba(22, 163, 74, 0.02));
}

.quick-green .el-icon {
  color: #16a34a;
}

.quick-purple,
.quick-violet {
  background: linear-gradient(135deg, rgba(147, 51, 234, 0.08), rgba(147, 51, 234, 0.02));
}

.quick-purple .el-icon,
.quick-violet .el-icon {
  color: #9333ea;
}

.quick-orange {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(245, 158, 11, 0.02));
}

.quick-orange .el-icon {
  color: #f59e0b;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.todo-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) 20px;
  align-items: center;
  gap: 14px;
  width: 100%;
  min-height: 68px;
  padding: 12px 14px;
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background: var(--app-surface);
  color: var(--app-text);
  cursor: pointer;
  text-align: left;
}

.todo-item:hover {
  background: var(--app-hover);
}

.todo-item strong {
  display: block;
  margin-bottom: 6px;
  font-size: 15px;
}

.todo-item small {
  color: var(--app-text-muted);
  font-size: 13px;
}

.todo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: #fff;
  font-size: 20px;
}

.todo-orange {
  background: #f59e0b;
}

.todo-red {
  background: #ef4444;
}

.todo-blue {
  background: #0f62fe;
}

.todo-arrow {
  color: var(--app-text-muted);
}

.trend-chart {
  min-height: 290px;
}

.trend-chart svg {
  width: 100%;
  height: 260px;
}

.grid-lines line {
  stroke: var(--app-border);
  stroke-dasharray: 4 5;
}

.trend-area {
  fill: rgba(15, 98, 254, 0.08);
  stroke: none;
}

.trend-line {
  fill: none;
  stroke: #0f62fe;
  stroke-width: 2.5;
}

.trend-dots circle {
  fill: #0f62fe;
  stroke: var(--app-surface);
  stroke-width: 2;
}

.chart-labels text {
  fill: var(--app-text-muted);
  font-size: 12px;
}

.chart-legend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--app-text-muted);
  font-size: 13px;
}

.chart-legend span {
  width: 8px;
  height: 8px;
  background: #0f62fe;
  border-radius: 2px;
}

@media (max-width: 1280px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-grid,
  .lower-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .stat-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
