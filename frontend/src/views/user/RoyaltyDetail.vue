<template>
  <PageBackground2>
    <div class="title-line"></div>
    <template #page-title>我的稿费明细</template>
    <!-- 内容卡片插槽 -->
    <template #card-content>
      <!-- 查询区域 -->
      <div class="query-area">
        <label>月份:</label>
        <select class="month-select">
          <!-- 实际项目中可通过v-for循环渲染月份选项 -->
          <option value="">请选择月份</option>
          <option value=""></option>

          <option value="1">1月</option>
          <option value="1">2月</option>
          <option value="1">3月</option>
          <option value="1">4月</option>
          <option value="1">5月</option>
          <option value="1">6月</option>
          <option value="1">7月</option>
          <option value="1">8月</option>
          <option value="1">9月</option>
          <option value="1">10月</option>
          <option value="1">11月</option>
          <option value="1">12月</option>
        </select>

        <label class="ml-20">任务名称:</label>
        <input
            type="text"
            placeholder="输入任务名称查询"
            class="task-input"
        >
        <button class="query-btn">查询</button>
      </div>

      <!-- 稿费表格 -->
      <table class="fee-table">
        <thead>
        <tr>
          <th>日期</th>
          <th>任务名称</th>
          <th>部门</th>
          <th>金额</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>

        <!-- 实际项目中通过v-for渲染数据 -->
        <tr v-for="(item, index) in feeList" :key="index">
          <td>{{ item.date || '-' }}</td>
          <td>{{ item.taskName || '-' }}</td>
          <td>{{ item.department || '-' }}</td>
          <td>{{ item.amount || '-' }}</td>
          <td>{{ item.status || '-' }}</td>
          <td>
            <button class="feedback-btn" @click="handleFeedback(item)">反馈</button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 总计区域 -->
      <div class="total-area">
        共计: <span class="total-amount">XXX元</span>
      </div>
    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const router = useRouter();
// 模拟稿费数据（实际项目从接口获取）
const feeList = ref([]);

// 跳转登录页
const goLogin = () => {
  router.push({ path: '/login' });
};

// 反馈操作
const handleFeedback = (item) => {
  // 实际项目中可打开反馈弹窗/页面
  console.log('点击反馈:', item);
};
</script>

<style scoped>
/* 标题线样式 */
.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4;
  margin-right: 12px;
}

/* 查询区域样式 */
.query-area {
  margin: 20px 0;
  display: flex;
  align-items: center;
}
.month-select, .task-input {
  padding: 4px 8px;
  margin: 0 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.query-btn {
  padding: 4px 16px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 8px;
}
.ml-20 {
  margin-left: 20px;
}

/* 表格样式 */
.fee-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}
.fee-table th, .fee-table td {
  border: 1px solid #ddd;
  padding: 12px 8px;
}
.fee-table th {
  background-color: #eee;
  font-weight: 500;
}
.feedback-btn {
  border: none;
  background: transparent;
  color: #9b8eb4;
  cursor: pointer;
  padding: 0;
}

/* 总计区域样式 */
.total-area {
  margin-top: 20px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
}
.total-amount {
  color: #333;
}
</style>