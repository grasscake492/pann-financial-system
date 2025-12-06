<template>
  <PageBackground2>
    <template #card-content>
      <!-- 顶部标签栏（仅保留3个按钮） -->
      <div class="top-tabs">
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'query' }"
            @click="currentTopTab = 'query'"
        >
          任务查询
        </button>
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'user' }"
            @click="currentTopTab = 'user'"
        >
          人员管理
        </button>
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'summary' }"
            @click="currentTopTab = 'summary'"
        >
          稿费汇总
        </button>
      </div>

      <!-- 1. 任务查询 -->
      <div v-if="currentTopTab === 'query'" class="tab-content">
        <div class="query-form">
          <label>选择月份:</label>
          <select class="form-select">
            <option value="">下拉框</option>
            <option value="1">1月</option>
          </select>
          <label>选择部门:</label>
          <select class="form-select">
            <option value="">下拉框</option>
            <option value="news">新闻部</option>
          </select>
          <label>任务名称:</label>
          <input type="text" placeholder="输入任务名称查询" class="form-input">
          <button class="query-btn">查询</button>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>日期</th>
            <th>任务名称</th>
            <th>参与人以及金额详情</th>
            <th>部门</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="i in 6" :key="i">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 2. 人员管理 -->
      <div v-if="currentTopTab === 'user'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>目前成员</h3>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>成员名称</th>
            <th>加入时间</th>
            <th>部门</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="i in 6" :key="i">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 3. 稿费汇总 -->
      <div v-if="currentTopTab === 'summary'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>稿费汇总与导出</h3>
        </div>
        <div class="form-row">
          <label>选择月份:</label>
          <select class="form-select">
            <option value=""></option>
            <option value="1">1月</option>
            <option value="2">2月</option>
            <option value="3">3月</option>
            <option value="4">4月</option>
            <option value="5">5月</option>
            <option value="6">6月</option>
            <option value="7">7月</option>
            <option value="8">8月</option>
            <option value="9">9月</option>
            <option value="10">10月</option>
            <option value="11">11月</option>
            <option value="12">12月</option>
          </select>
        </div>
        <div class="dept-section">
          <label>新闻部:</label>
          <table class="content-table">
            <thead>
            <tr>
              <th>日期</th>
              <th>任务名称</th>
              <th>拍摄人员</th>
              <th>金额详情</th>
              <th>任务总金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in 6" :key="i">
              <td></td>
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
            </tbody>
          </table>
          <div class="total-row">
            <label>新闻部总:</label>
            <span>XXX元</span>
          </div>
        </div>
        <div class="dept-section">
          <label>编辑部:</label>
          <table class="content-table">
            <thead>
            <tr>
              <th>项目</th>
              <th>供图人</th>
              <th>金额详情</th>
              <th>任务总金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="i in 6" :key="i">
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            </tr>
            </tbody>
          </table>
          <div class="total-row">
            <label>编辑部总:</label>
            <span>XXX元</span>
          </div>
          <div class="dept-section">
            <label>稿费汇总:</label>
            <table class="content-table">
              <thead>
              <tr>
                <th>姓名新闻部稿费</th>
                <th>编辑部稿费</th>
                <th>新闻部稿费</th>
                <th>大图稿费</th>
                <th>总金额</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="i in 6" :key="i">
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
              </tr>
              </tbody>
            </table>
            <div class="total-row">
              <label>新闻部总:</label>
              <span>XXX元</span>
            </div>
            <div class="total-row">
              <label>编辑部总:</label>
              <span>XXX元</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import { ref } from 'vue';
// 控制顶部标签切换
const currentTopTab = ref('query'); // 默认显示“任务查询”
</script>

<style scoped>
/* 顶部标签栏（匹配设计图激活态） */
.top-tabs {
  margin-bottom: 20px;
}
.top-tab-btn {
  padding: 8px 20px;
  background-color: #e0e0e0;
  border: none;
  cursor: pointer;
  margin-right: 2px;
  font-size: 14px;
}
.top-tab-btn.active {
  background-color: #9b8eb4;
  color: #fff;
}

/* 通用标签内容容器 */
.tab-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

/* 通用表单样式 */
.form-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}
.form-select, .form-input {
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 2px;
  font-size: 14px;
}
.form-select {
  width: 120px;
}
.form-input {
  width: 200px;
}
.small-input {
  width: 180px;
}
.tiny-input {
  width: 100px;
}
.inline-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 任务查询表单 */
.query-form {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}
.query-btn {
  padding: 6px 15px;
  background-color: #ccc;
  border: none;
  cursor: pointer;
}

/* 通用表格样式 */
.content-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ccc;
  margin: 10px 0;
}
.content-table th, .content-table td {
  padding: 8px 10px;
  border: 1px solid #ccc;
  text-align: left;
  font-size: 14px;
}
.content-table th {
  background-color: #e0e0e0;
}

/* 标题区域（带紫色竖线） */
.section-title {
  display: flex;
  align-items: center;
  margin: 15px 0;
  font-size: 16px;
}
.title-line {
  width: 4px;
  height: 20px;
  background-color: #9b8eb4;
  margin-right: 8px;
}

/* 稿费汇总-部门总计行 */
.total-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-top: none;
}
</style>