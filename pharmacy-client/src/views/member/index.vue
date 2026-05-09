<template>
  <div class="page">
    <section class="grid-2">
      <div class="panel member-card">
        <h1>{{ memberInfo.name }}</h1>
        <p class="muted">{{ memberInfo.phone }}</p>
        <div class="member-stats">
          <div>
            <span>会员等级</span>
            <strong>{{ memberInfo.level }}</strong>
          </div>
          <div>
            <span>累计消费</span>
            <strong>¥{{ memberInfo.totalAmount.toFixed(2) }}</strong>
          </div>
          <div>
            <span>账户状态</span>
            <strong>{{ memberInfo.status }}</strong>
          </div>
        </div>
      </div>
      <div class="panel">
        <h2 class="panel-title">温馨提示</h2>
        <p class="notice">当前会员中心为静态展示页面，暂未接入真实登录、积分和消费明细接口。到店购药时可报手机号由店员核对会员信息。</p>
      </div>
    </section>

    <section class="section panel">
      <h2 class="panel-title">消费记录</h2>
      <el-table :data="consumptionRecords" border>
        <el-table-column prop="id" label="单号" min-width="160" />
        <el-table-column prop="date" label="日期" width="130" />
        <el-table-column prop="item" label="内容" min-width="160" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="store" label="门店" min-width="150" />
        <el-table-column label="操作" width="120">
          <template #default>
            <el-button type="primary" text>查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { consumptionRecords, memberInfo } from '@/data/mock'
</script>

<style scoped>
.member-card h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.member-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.member-stats div {
  padding: 16px;
  border-radius: 8px;
  background: var(--client-surface-soft);
}

.member-stats span,
.member-stats strong {
  display: block;
}

.member-stats span {
  color: var(--client-muted);
}

.member-stats strong {
  margin-top: 8px;
  font-size: 18px;
}

@media (max-width: 860px) {
  .member-stats {
    grid-template-columns: 1fr;
  }
}
</style>
