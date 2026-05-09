<template>
  <div class="page chat-page">
    <section class="panel">
      <div class="chat-layout">
        <aside class="question-list">
          <h1 class="panel-title">常见问题</h1>
          <button v-for="question in questions" :key="question" type="button" @click="draft = question">
            {{ question }}
          </button>
        </aside>

        <div class="chat-window">
          <div class="chat-title">
            <div>
              <h2>药店智能服务助手</h2>
              <p class="muted">静态演示回复，实际问诊请咨询店内药师。</p>
            </div>
            <el-tag type="success">在线</el-tag>
          </div>
          <div class="message-list">
            <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
              <div class="bubble">{{ message.text }}</div>
            </div>
          </div>
          <div class="input-bar">
            <el-input v-model="draft" placeholder="请输入想咨询的问题" clearable />
            <el-button type="primary" :icon="Position" @click="sendMock">发送</el-button>
          </div>
          <p class="notice">本服务由 AI 提供，仅供参考，如需进一步帮助请咨询店内药师。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Position } from '@element-plus/icons-vue'
import { chatMessages } from '@/data/mock'

const questions = [
  '今天药店营业到几点？',
  '处方药购买需要带什么？',
  '布洛芬缓释胶囊还有货吗？',
  '老人慢病用药能否电话咨询？'
]

const messages = ref([...chatMessages])
const draft = ref('')

const sendMock = () => {
  if (!draft.value.trim()) return
  messages.value.push({ role: 'user', text: draft.value.trim() })
  messages.value.push({
    role: 'ai',
    text: '已收到您的问题。当前为静态演示，建议到店或拨打药店电话，由药师结合具体情况为您确认。'
  })
  draft.value = ''
}
</script>

<style scoped>
.chat-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 22px;
}

.question-list {
  display: grid;
  align-content: start;
  gap: 10px;
}

.question-list button {
  width: 100%;
  padding: 13px 14px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  color: var(--client-text);
  background: var(--client-surface-soft);
  cursor: pointer;
  font: inherit;
  text-align: left;
}

.chat-title {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.chat-title h2 {
  margin: 0 0 6px;
}

.message-list {
  display: grid;
  gap: 14px;
  min-height: 360px;
  max-height: 420px;
  overflow: auto;
  padding: 18px;
  border-radius: 8px;
  background: var(--client-surface-soft);
}

.message {
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.bubble {
  max-width: 72%;
  padding: 12px 14px;
  border-radius: 8px;
  line-height: 1.7;
  background: #ffffff;
  border: 1px solid var(--client-border);
}

.message.user .bubble {
  color: #ffffff;
  background: var(--client-primary);
  border-color: var(--client-primary);
}

.input-bar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 860px) {
  .chat-layout,
  .input-bar {
    grid-template-columns: 1fr;
  }

  .bubble {
    max-width: 92%;
  }
}
</style>
