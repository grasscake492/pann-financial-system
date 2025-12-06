<template>
  <PageBackground2>

    <template #card-content>
      <!-- 标签页按钮 -->
      <div class="tab-buttons">
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'query' }"
            @click="currentTab = 'query'"
        >
          任务查询
        </button>
        <button
            class="tab-btn"
            :class="{ active: currentTab === 'manage' }"
            @click="currentTab = 'manage'"
        >
          任务管理
        </button>
      </div>

      <!-- 任务查询区域 -->
      <div v-if="currentTab === 'query'">
        <div class="query-form">
          <label>选择月份:</label>
          <select class="form-select">
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
          <label>任务名称:</label>
          <input type="text" placeholder="输入任务名称查询" class="form-input">
          <button class="query-btn">查询</button>
        </div>

        <table class="task-table">
          <thead>
          <tr>
            <th>日期</th>       <!-- 表头单元格，用<th>而非<td> -->
            <th>任务名称</th>
            <th>参与人以及金额详情</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="i in 8" :key="i">
            <td></td>
            <td></td>
            <td></td>
            <td>
              <button class="op-btn">修改</button>
              <button class="op-btn">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 任务管理区域 -->
      <div v-if="currentTab === 'manage'">
        <div class="manage-title">
          <div class="title-line"></div>
          <h3>新建任务</h3>
        </div>
        <div class="manage-form">
          <div class="form-item">
            <label>任务名称:</label>
            <input type="text" placeholder="输入" class="form-input">
          </div>
          <div class="form-item">
            <label>任务日期:</label>
            <input type="text" placeholder="弹出日期选择框" class="form-input">
          </div>
          <div class="form-item">
            <label>执行人以及对应金额:</label>
            <select class="form-select">
              <option value="">弹出目前已有成员信息</option>
            </select>
            <label>金额:</label>
            <input type="text" placeholder="输入" class="form-input small-input">
            <button class="add-btn">添加执行人</button>
          </div>
          <button class="submit-btn">提交任务</button>
        </div>
      </div>
    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { ref } from 'vue';
const currentTab = ref('query');
</script>

<style scoped>
/* 标签页按钮 */
.tab-buttons {
  margin-bottom: 20px;
}
.tab-btn {
  padding: 8px 20px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
  margin-right: 2px;
}
.tab-btn.active {
  background-color: #c0c0c0;
}

/* 表单&表格样式 */
.query-form {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
}
.form-select, .form-input {
  padding: 6px 8px;
  border: 1px solid #ccc;
}
.query-btn {
  padding: 6px 15px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
}
.task-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ccc;
}
.task-table th, .task-table td {
  padding: 8px 10px;
  border: 1px solid #ccc;
}
.task-table th {
  background-color: #e0e0e0;
}
.op-btn {
  padding: 4px 8px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
  margin-right: 5px;
}
.manage-title {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.manage-title .title-line {
  width: 4px;
  height: 20px;
  background-color: #9b8eb4;
  margin-right: 8px;
}
.manage-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.form-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.small-input {
  width: 100px;
}
.add-btn, .submit-btn {
  padding: 6px 12px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
}
.submit-btn {
  align-self: flex-end;
}
</style>