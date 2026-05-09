<template>
  <div class="page">
    <section class="grid-2">
      <div class="panel">
        <h1 class="store-title">{{ storeInfo.name }}</h1>
        <div class="info-list">
          <div class="info-row"><span>营业状态</span><el-tag type="success">{{ storeInfo.status }}</el-tag></div>
          <div class="info-row"><span>营业时间</span><strong>{{ storeInfo.hours }}</strong></div>
          <div class="info-row"><span>联系电话</span><strong>{{ storeInfo.phone }}</strong></div>
          <div class="info-row"><span>详细地址</span><strong>{{ storeInfo.address }}</strong></div>
          <div class="info-row"><span>经营范围</span><strong>{{ storeInfo.scope }}</strong></div>
        </div>
      </div>

      <div class="panel map-panel">
        <h2 class="panel-title">位置示意</h2>
        <div class="map-box">
          <el-icon><LocationFilled /></el-icon>
          <span>安心镇中心街</span>
        </div>
        <p class="muted">路线说明：沿安心镇中心街向东步行约 300 米，靠近镇卫生院和便民超市。</p>
      </div>
    </section>

    <section class="section grid-2">
      <div class="panel">
        <h2 class="panel-title">服务说明</h2>
        <ul class="service-list">
          <li>提供常用药、慢病常备药、外伤消毒用品和基础医疗器械咨询。</li>
          <li>处方药需凭有效处方购买，购药前可电话确认库存。</li>
          <li>老人和慢病顾客可到店咨询药师用药注意事项。</li>
        </ul>
      </div>
      <div class="panel">
        <h2 class="panel-title">药店公告</h2>
        <p class="notice">{{ storeInfo.notice }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { storeInfo } from '@/data/mock'
</script>

<style scoped>
.store-title {
  margin: 0 0 18px;
  font-size: 30px;
}

.map-panel {
  display: grid;
  align-content: start;
  gap: 16px;
}

.map-box {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 260px;
  gap: 10px;
  border: 1px dashed var(--client-primary);
  border-radius: 8px;
  color: var(--client-primary-dark);
  background: var(--client-surface-soft);
  font-size: 20px;
  font-weight: 800;
}

.map-box .el-icon {
  font-size: 30px;
}

.service-list {
  display: grid;
  gap: 12px;
  margin: 0;
  padding-left: 20px;
  color: var(--client-muted);
  line-height: 1.8;
}
</style>
