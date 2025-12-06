<template>
  <PageBackground2>
    <template #card-content>
      <!-- 顶部标签栏（5个按钮，匹配设计图） -->
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
            :class="{ active: currentTopTab === 'manage' }"
            @click="currentTopTab = 'manage'"
        >
          代领设置
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
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'account' }"
            @click="currentTopTab = 'account'"
        >
          做账稿费
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

      <!-- 2. 代领设置 -->
      <div v-if="currentTopTab === 'manage'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>代领设置</h3>
        </div>
        <div class="manage-form">
          <div class="form-row">
            <label>选择月份:</label>
            <select class="form-select">
              <option value="">下拉框</option>
            </select>
          </div>
          <div class="form-row">
            <label>代领人:</label>
            <input type="text" placeholder="弹出目前已有成员信息，只能单选" class="form-input">
          </div>
          <div class="form-row">
            <label>被代领人:</label>
            <input type="text" placeholder="弹出目前已有成员信息，只能单选" class="form-input">
          </div>
          <div class="form-row">
            <label>代领部门以及金额:</label>
            <div class="inline-row">
              <label>选择部门:</label>
              <input type="text" placeholder="弹出新闻部/编辑部，只能单选" class="form-input small-input">
              <label>金额:</label>
              <input type="text" placeholder="输入" class="form-input tiny-input">
            </div>
          </div>
          <button class="confirm-btn">确认代领</button>
        </div>

        <div class="section-title">
          <div class="title-line"></div>
          <h3>当前代领</h3>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>代领人</th>
            <th>被代领人</th>
            <th>部门</th>
            <th>金额</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="i in 5" :key="i">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 3. 人员管理 -->
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

      <!-- 4. 稿费汇总 -->
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

      <!-- 5. 做账稿费 -->
      <div v-if="currentTopTab === 'account'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>代领调整后稿费</h3>
        </div>
        <div class="form-row">
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
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>代领人</th>
            <th>被代领人</th>
            <th>代领部门</th>
            <th>代领金额</th>
            <th>最终金额</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="i in 3" :key="i">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          </tbody>
        </table>

        <div class="dept-section">
          <label>新闻部代领后:</label>
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
        </div>
      </div>
      <div class="dept-section">
        <label>新闻部代领后:</label>
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
      </div>
      <div class="dept-section">
        <label>新闻部代领后:</label>
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

/* 代领设置按钮 */
.confirm-btn {
  padding: 6px 20px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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