<template>
  <PageBackground2>
    <template #card-content>
      <!-- 顶部标签栏（5个按钮，匹配设计图） -->
      <div class="top-tabs">
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'query' }"
            @click="handleTabChange('query')"
        >
          任务查询
        </button>
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'feedback' }"
            @click="() => { currentTopTab = 'feedback'; fetchAllFeedback() }"
        >
          查看反馈
        </button>

        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'user' }"
            @click="handleTabChange('user')"
        >
          人员管理
        </button>
        <button
            class="top-tab-btn"
            :class="{ active: currentTopTab === 'summary' }"
            @click="handleTabChange('summary')"
        >
          稿费汇总
        </button>

      </div>

      <!-- 1. 任务查询 -->
      <div v-if="currentTopTab === 'query'" class="tab-content">
        <div class="query-form">
          <label>选择月份:</label>
          <select class="form-select" v-model="queryForm.month">
            <option value="">全部</option>
            <option value="01">1月</option>
            <option value="02">2月</option>
            <option value="03">3月</option>
            <option value="04">4月</option>
            <option value="05">5月</option>
            <option value="06">6月</option>
            <option value="07">7月</option>
            <option value="08">8月</option>
            <option value="09">9月</option>
            <option value="10">10月</option>
            <option value="11">11月</option>
            <option value="12">12月</option>
          </select>
          <label>选择部门:</label>
          <select class="form-select" v-model="queryForm.deptId">
            <option value="">全部</option>
            <option value="news">新闻部</option>
            <option value="edit">编辑部</option>
            <option value="operation">运营部</option>
          </select>
          <label>任务名称:</label>
          <input type="text" placeholder="输入任务名称查询" class="form-input" v-model="queryForm.article_title">
          <button class="query-btn" @click="fetchTaskList">查询</button>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>日期</th>
            <th>任务名称</th>
            <th>参与人</th>
            <th>金额详情</th>
            <th>部门</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="taskList.length === 0">
            <td colspan="5" class="empty-tip">暂无任务记录</td>
          </tr>
          <tr v-for="item in taskList" :key="item.record_id">
            <td>{{ item.created_at }}</td>
            <td>{{ item.article_title }}</td>
            <!-- 参与人数组转字符串 -->
            <td>{{ item.real_names?.join(', ') || '-' }}</td>
            <td>{{ item.fee_amount }} </td>
            <td>{{ item.deptName}}</td>
          </tr>
          </tbody>
        </table>
      </div>


      <!-- 反馈查询区域 -->
      <div v-if="currentTopTab === 'feedback'">
        <!-- 反馈筛选栏 -->
        <div class="feedback-filter">
          <label>反馈状态:</label>
          <select class="form-select" v-model="feedbackQueryParams.status">
            <option value="">全部</option>
            <option value="pending">待处理</option>
            <option value="replied">已回复</option>
          </select>
          <label>月份:</label>
          <select class="form-select" v-model="filterMonth">
            <option value="">全部</option>
            <option value="1">1月</option>
            <!-- ... 其他月份选项 -->
          </select>
          <label>用户姓名:</label>
          <input
              type="text"
              placeholder="输入姓名模糊查询"
              class="form-input"
              v-model="filterName"
          >
          <button class="query-btn" @click="handleFrontendFilter">查询</button>
        </div>

        <!-- 反馈条目列表 -->
        <div class="feedback-item-list">
          <div v-if="filteredFeedbackList.length === 0" class="empty-tip">暂无反馈数据</div>
          <div
              class="feedback-item"
              v-for="item in filteredFeedbackList"
              :key="item.feedback_id"
          >
            <div class="feedback-header">
              <span class="feedback-id">反馈ID：{{ item.feedback_id }}</span>
              <span class="status-tag" :class="FEEDBACK_STATUS_CLASS[item.status]">
          {{ FEEDBACK_STATUS_LABEL[item.status] }}
        </span>
            </div>
            <div class="feedback-content">
              <p><span class="label">反馈用户：</span>{{ item.real_name }}</p>
              <p><span class="label">反馈内容：</span>{{ item.content }}</p>
              <p><span class="label">反馈时间：</span>{{ item.created_at }}</p>
              <p><span class="label">回复时间：</span>{{ item.replied_at || '未回复' }}</p>
              <p><span class="label">回复内容：</span>{{ item.reply_content || '暂无回复' }}</p>
            </div>
            <div class="feedback-actions">
              <button class="op-btn reply-btn" @click="openReplyDialog(item.feedback_id)">回复</button>
              <button class="op-btn status-btn" @click="openStatusDialog(item.feedback_id)">改状态</button>
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination" v-if="filteredFeedbackList.length > 0">
          <button
              class="page-btn"
              @click="handlePageChange(currentPage - 1)"
              :disabled="currentPage <= 1"
          >
            上一页
          </button>
          <span>第{{ currentPage }}页 / 共{{ totalItemPages }}页</span>
          <button
              class="page-btn"
              @click="handlePageChange(currentPage + 1)"
              :disabled="currentPage >= totalItemPages"
          >
            下一页
          </button>
        </div>

        <!-- 回复弹窗 -->
        <div class="dialog-mask" v-if="replyDialogVisible">
          <div class="dialog-content">
            <h4>回复反馈（ID: {{ currentFeedbackId }}）</h4>
            <textarea
                placeholder="请输入回复内容"
                class="reply-textarea"
                v-model="replyForm.reply_content"
            ></textarea>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="closeReplyDialog">取消</button>
              <button class="confirm-btn" @click="submitReply">提交回复</button>
            </div>
          </div>
        </div>

        <!-- 改状态弹窗 -->
        <div class="dialog-mask" v-if="statusDialogVisible">
          <div class="dialog-content">
            <h4>更新反馈状态（ID: {{ currentFeedbackId }}）</h4>
            <select class="form-select" v-model="statusForm.status">
              <option value="pending">待处理</option>
              <option value="replied">已回复</option>
            </select>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="closeStatusDialog">取消</button>
              <button class="confirm-btn" @click="submitStatusUpdate">确认更新</button>
            </div>
          </div>
        </div>
      </div>





      <!-- 3. 人员管理 -->
      <div v-if="currentTopTab === 'user'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>目前成员</h3>

        </div>
        <!-- 新增添加成员按钮 -->
        <button class="add-btn" @click="editUser()">新增成员</button>
        <table class="content-table">
          <thead>
          <tr>
<!--            <th>用户ID</th>-->
            <th>真实姓名</th>
            <th>学号</th>
            <th>邮箱</th>
            <th>所属部门</th>
            <th>角色权限</th>
<!--            <th>是否超级管理员</th>-->
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="userList.length === 0">
            <td colspan="8" class="empty-tip">暂无成员记录</td>
          </tr>
          <tr v-for="item in userList" :key="item.user_id">
<!--            <td>{{ item.user_id }}</td>-->
            <td>{{ item.real_name }}</td>
            <td>{{ item.student_number || '-' }}</td>
            <td>{{ item.email || '-' }}</td>
            <td>{{ deptIdMap[item.department_id] || '无' }}</td>
            <td>
              <!-- 多层条件判断：先判断超级管理员 → 再判断部门管理员 → 最后普通用户 -->
              <span v-if="item.is_super_admin" class="role-tag super-admin">系统管理员</span>
              <span v-else-if="item.department_id && item.department_id !== ''" class="role-tag dept-admin">部门管理员</span>
              <span v-else class="role-tag normal-user">普通用户</span>
            </td>
<!--            <td>
        <span class="status-tag" :class="item.is_super_admin ? 'success' : 'default'">
          {{ item.is_super_admin ? '是' : '否' }}
        </span>
            </td>-->
            <td>
              <div class="op-btn-group">
                <button class="op-btn primary" @click="openRoleEditDialog(item)">修改角色</button>
                <button class="op-btn danger" @click="deleteUser(item.user_id)">移除</button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
        <!-- 人员基础信息编辑弹窗（新增注册字段） -->
        <div class="dialog-mask" v-if="userEditDialogVisible">
          <div class="dialog-content">
            <h4>{{ isAddUser ? '新增成员' : '编辑成员' }}</h4>
            <div class="form-row">
              <label class="justify-label">成员姓名:</label>
              <input type="text" class="form-input" v-model="userEditForm.real_name" placeholder="请输入真实姓名">
            </div>
            <div class="form-row">
              <label class="justify-label">学号:</label>
              <input
                  type="text"
                  class="form-input"
                  v-model="userEditForm.student_number"
                  placeholder="请输入12位纯数字学号"
                  maxlength="12"
              >
            </div>
            <div class="form-row">
              <label class="justify-label">邮箱:</label>
              <input type="email" class="form-input" v-model="userEditForm.email" placeholder="请输入有效邮箱">
            </div>
            <!-- 新增：仅新增时显示密码相关字段 -->
            <div v-if="isAddUser" class="form-row">
              <label class="justify-label">密码:</label>
              <input
                  type="password"
                  class="form-input"
                  v-model="userEditForm.password"
                  placeholder="请设置密码"
              >
            </div>
            <div v-if="isAddUser" class="form-row">
              <label class="justify-label">确认密码:</label>
              <input
                  type="password"
                  class="form-input"
                  v-model="userEditForm.confirmPassword"
                  placeholder="请再次输入密码"
              >
            </div>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="userEditDialogVisible = false">取消</button>
              <button class="confirm-btn" @click="submitUserEdit">确认</button>
            </div>
          </div>
        </div>

        <!-- 新增：角色权限编辑弹窗 -->
        <div class="dialog-mask" v-if="roleEditDialogVisible">
          <div class="dialog-content">
            <h4>修改用户角色权限</h4>
            <div class="form-row">
              <label>用户姓名:</label>
              <input type="text" class="form-input" v-model="roleEditForm.real_name" disabled>
            </div>
<!--            <div class="form-row">
              <label>是否超级管理员:</label>
              <select class="form-input" v-model="roleEditForm.is_super_admin">
                <option :value="false">否</option>
                <option :value="true">是</option>
              </select>
            </div>-->
            <div class="form-row">
              <label>所属部门:</label>
              <select class="form-input" v-model="roleEditForm.department_id">
                <option value="">请选择部门</option>
                <option value="1">新闻部</option>
                <option value="2">编辑部</option>
                <option value="3">运营部</option>
              </select>
            </div>
            <div class="form-row">
              <label>用户角色:</label>
              <select class="form-input" v-model="roleEditForm.role">
<!--                <option value="普通成员">普通成员</option>-->
                <option value="部门管理员">部门管理员</option>
                <option value="系统管理员">系统管理员</option>
              </select>
            </div>
            <div class="dialog-btns">
              <button class="cancel-btn" @click="roleEditDialogVisible = false">取消</button>
              <button class="confirm-btn" @click="submitRoleEdit">确认修改</button>
            </div>
          </div>
        </div>
      </div>
      <!-- 4. 稿费汇总 -->
      <div v-if="currentTopTab === 'summary'" class="tab-content">
        <div class="section-title">
          <div class="title-line"></div>
          <h3>稿费汇总与导出</h3>
        </div>
        <!-- 月份+部门+导出格式：三个必填项放在同一行，严格匹配接口要求 -->
        <div class="form-row" style="align-items: center; gap: 20px; margin-bottom: 20px; flex-wrap: wrap;">
          <div style="display: flex; align-items: center; gap: 10px;">
            <label>统计月份:</label>
            <!-- 年份下拉选择（支持自定义年份范围） -->
            <select class="form-select" v-model="selectedYear" @change="updateMonthOptions">
              <option value="">请选择年份</option>
              <!-- （可自定义范围） -->
              <option v-for="year in generateYearOptions()" :key="year" :value="year">
                {{ year }}年
              </option>
            </select>
            <!-- 月份下拉选择（根据选中的年份动态生成） -->
            <select class="form-select" v-model="selectedMonth" @change="setStatisticalMonth">
              <option value="">请选择月份</option>
              <option v-for="month in monthOptions" :key="month.value" :value="month.value">
                {{ month.label }}
              </option>
            </select>
          </div>

          <div style="display: flex; align-items: center;">
<!--            <label>部门:</label>
            &lt;!&ndash; 部门选择：必填，绑定department_id（bigint类型） &ndash;&gt;
            <select class="form-select" v-model="summaryForm.department_id">
              <option value="">请选择部门</option>
              <option value="1">新闻部</option>
              <option value="2">编辑部</option>
              <option value="3">运营部</option>
            </select>-->
          </div>

<!--          <div style="display: flex; align-items: center;">
            <label>导出格式:</label>
            &lt;!&ndash; 导出格式：必填，绑定format（仅Excel/PDF，匹配接口要求） &ndash;&gt;
            <select class="form-select" v-model="summaryForm.format">
              <option value="">请选择格式</option>
              <option value="Excel">Excel</option>
              <option value="PDF">PDF</option>
            </select>
          </div>-->

<!--          <button class="export-btn" @click="exportSummaryData">导出数据</button>-->
        </div>

        <!-- 以下表格部分保留你原有结构，移除重复的稿费汇总表格 -->
        <div class="dept-section">
          <label>新闻部:</label>
          <table class="content-table">
            <thead>
            <tr>
              <th>日期</th>
              <th>任务名称</th>
              <th>拍摄人员</th>
              <th>任务总金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="newsDeptSummary.length === 0">
              <td colspan="4" class="empty-tip">暂无新闻部稿费记录</td>
            </tr>
            <!-- 修正2：渲染新闻部原始数据newsDeptSummary，而非人员汇总totalSummary -->
            <!-- 修正3：key用接口唯一标识record_id，避免重复 -->
            <tr v-for="item in newsDeptSummary" :key="item.record_id">
              <!-- 修正4：日期格式化 + 空值兜底 -->
              <td>{{ item.created_at || '-' }}</td>
              <!-- 修正5：任务名称空值兜底 -->
              <td>{{ item.article_title || '-' }}</td>
              <!-- 修正6：拍摄人员数组拼接 + 空值兜底 -->
              <td>{{ item.real_names || '-' }}</td>
              <!-- 修正7：金额格式化 + 空值兜底 -->
              <td>{{ formatMoney(item.fee_amount) }}元</td>
            </tr>
            </tbody>
          </table>
          <div class="total-row bold">
            <label>新闻部总稿费:</label>
            <span>{{ newsDeptTotal }}元</span>
          </div>
        </div>
        <div class="dept-section">
          <label>编辑部:</label>
          <table class="content-table">
            <thead>
            <tr>
              <th>日期</th>
              <th>任务名称</th>
              <th>拍摄人员</th>
              <th>任务总金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="editDeptSummary.length === 0">
              <td colspan="4" class="empty-tip">暂无编辑部稿费记录</td>
            </tr>
            <tr v-for="item in editDeptSummary" :key="item.projectId">
              <td>{{ item.created_at}}元</td>
              <td>{{ item.article_title }}</td>
              <td>{{ item.real_names }}</td>
              <td>{{ item.fee_amount }}元</td>
            </tr>
            </tbody>
          </table>
          <div class="total-row">
            <label>编辑部总:</label>
            <span>{{ editDeptTotal }}元</span>
          </div>
        </div>
        <div class="dept-section">
          <label>稿费汇总:</label>
          <table class="content-table">
            <thead>
            <tr>
              <th>姓名</th>
              <th>新闻部稿费</th>
              <th>编辑部稿费</th>
              <th>大图稿费</th>
              <th>总金额</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="totalSummary.length === 0">
              <td colspan="5" class="empty-tip">暂无汇总数据</td>
            </tr>
            <tr v-for="item in totalSummary" :key="item.userId">
              <td>{{ item.real_name }}</td>
              <td>{{ item.newsAmount }}元</td>
              <td>{{ item.editAmount }}元</td>
              <td>{{ item.bigImgAmount }}元</td>
              <td>{{ item.totalAmount }}元</td>
            </tr>
            </tbody>
          </table>
          <div class="total-row">
            <label>新闻部总:</label>
            <span>{{ newsDeptTotal }}元</span>
          </div>
          <div class="total-row">
            <label>编辑部总:</label>
            <span>{{ editDeptTotal }}元</span>
          </div>
          <div class="total-row bold">
            <label>平台总稿费:</label>
            <span>{{ platformTotal }}元</span>
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
          <select class="form-select" v-model="accountForm.month" @change="fetchAllAccountData">
            <option value="">请选择</option>
            <option value="01">1月</option>
            <option value="02">2月</option>
            <option value="03">3月</option>
            <option value="04">4月</option>
            <option value="05">5月</option>
            <option value="06">6月</option>
            <option value="07">7月</option>
            <option value="08">8月</option>
            <option value="09">9月</option>
            <option value="10">10月</option>
            <option value="11">11月</option>
            <option value="12">12月</option>
          </select>
          <!-- 新增导出按钮 -->
          <button class="export-btn" @click="exportAccountData" :disabled="!accountForm.month">
            导出本月做账数据
          </button>
        </div>

        <!-- 1. 代领人基础信息表 -->
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
          <tr v-if="proxyList.length === 0">
            <td colspan="5" class="empty-tip">暂无代领记录数据</td>
          </tr>
          <tr v-for="item in proxyList" :key="item.proxy_id">
            <td>{{ item.proxy_user_name }}</td>
            <td>{{ item.original_user_name }}</td>
            <td>{{ item.department_name }}</td>
            <td>{{ formatMoney(item.fee_amount) }}</td>
            <td>{{ formatMoney(item.final_fee_amount) }}</td>
          </tr>
          </tbody>
        </table>

        <!-- 2. 新闻部代领后明细表格 -->
        <div class="section-title mt-20">
          <div class="title-line"></div>
          <h3>新闻部代领后</h3>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>日期</th>
            <th>任务名称</th>
            <th>拍摄人员</th>
            <th>任务金额</th>
            <th>总金额</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="newsDeptList.length === 0">
            <td colspan="5" class="empty-tip">暂无新闻部代领后数据</td>
          </tr>
          <tr v-for="item in newsDeptList" :key="item.record_id">
            <td>{{ formatDate(item.created_at) }}</td>
            <td>{{ item.article_title }}</td>
            <td>{{ item.real_names.join(', ') }}</td>
            <td>{{ formatMoney(item.fee_amount) }}</td>
            <td>{{ formatMoney(newsDeptTotal) }}</td>
          </tr>
          </tbody>
          <tfoot v-if="newsDeptList.length > 0">
          <tr class="total-row">
            <td colspan="4" align="right">新闻部总计：</td>
            <td>{{ formatMoney(newsDeptTotal) }}</td>
          </tr>
          </tfoot>
        </table>

        <!-- 3. 编辑部代领后明细表格 -->
        <div class="section-title mt-20">
          <div class="title-line"></div>
          <h3>编辑部代领后</h3>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>项目</th>
            <th>供图人</th>
            <th>金额详情</th>
            <th>总金额</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="editDeptList.length === 0">
            <td colspan="4" class="empty-tip">暂无编辑部代领后数据</td>
          </tr>
          <tr v-for="item in editDeptList" :key="item.record_id">
            <td>{{ item.article_title }}</td>
            <td>{{ item.real_names.join(', ') }}</td>
            <td>{{ formatMoney(item.fee_amount) }}</td>
            <td>{{ formatMoney(editDeptTotal) }}</td>
          </tr>
          </tbody>
          <tfoot v-if="editDeptList.length > 0">
          <tr class="total-row">
            <td colspan="3" align="right">编辑部总计：</td>
            <td>{{ formatMoney(editDeptTotal) }}</td>
          </tr>
          </tfoot>
        </table>

        <!-- 4. 稿费代领后汇总表 -->
        <div class="section-title mt-20">
          <div class="title-line"></div>
          <h3>稿费代领后汇总</h3>
        </div>
        <table class="content-table">
          <thead>
          <tr>
            <th>姓名</th>
            <th>新闻部稿费</th>
            <th>编辑部稿费</th>
            <th>大图稿费</th>
            <th>总金额</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="summaryList.length === 0">
            <td colspan="5" class="empty-tip">暂无稿费汇总数据</td>
          </tr>
          <tr v-for="item in summaryList" :key="item.user_name">
            <td>{{ item.user_name }}</td>
            <td>{{ formatMoney(item.news_fee) }}</td>
            <td>{{ formatMoney(item.edit_fee) }}</td>
            <td>{{ formatMoney(item.big_img_fee) }}</td>
            <td>{{ formatMoney(item.total_fee) }}</td>
          </tr>
          </tbody>
          <tfoot v-if="summaryList.length > 0">
          <tr class="total-row">
            <td colspan="1" align="right">平台总计：</td>
            <td>{{ formatMoney(totalNewsFee) }}</td>
            <td>{{ formatMoney(totalEditFee) }}</td>
            <td>{{ formatMoney(totalBigImgFee) }}</td>
            <td>{{ formatMoney(totalAllFee) }}</td>
          </tr>
          </tfoot>
        </table>
      </div>

      <!-- 任务编辑弹窗（新增/编辑） -->
      <div class="dialog-mask" v-if="taskEditDialogVisible">
        <div class="dialog-content">
          <h4>{{ isAddTask ? '新增任务' : '编辑任务' }}</h4>
          <div class="form-row">
            <label>日期:</label>
            <input type="date" class="form-input" v-model="taskEditForm.created_at">
          </div>
          <div class="form-row">
            <label>任务名称:</label>
            <input type="text" class="form-input" v-model="taskEditForm.article_title">
          </div>
          <div class="form-row">
            <label>参与人及金额:</label>
            <input type="text" class="form-input" v-model="taskEditForm.userAmountDetail" placeholder="如：张三(100),李四(200)">
          </div>
          <div class="form-row">
            <label>部门:</label>
            <select class="form-input" v-model="taskEditForm.deptId">
              <option value="news">新闻部</option>
              <option value="edit">编辑部</option>
            </select>
          </div>
          <div class="dialog-btns">
            <button class="cancel-btn" @click="taskEditDialogVisible = false">取消</button>
            <button class="confirm-btn" @click="submitTaskEdit">确认</button>
          </div>
        </div>
      </div>

    </template>
  </PageBackground2>
</template>

<script setup>
import PageBackground2 from '@/components/PageBackground2.vue';
import {ref, reactive, computed, onMounted} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
// 引入接口方法（需在api目录下创建对应文件）
// 引入格式化工具（按你的实际路径调整）
import { formatDateTime, formatMoney, formatDepartment } from '@/utils/format';

import {getUserList, updateUserProfile} from "@/api/user.js";
import {
  addRoyaltyRecord,
  deleteRoyaltyRecord,
  exportRoyaltyRecord,
  getAllRoyalty,
  getDepartmentRoyalty
} from "@/api/royalty.js";
import {addProxyRecord, cancelProxyRecord, getProxyList, updateProxyRecord} from "@/api/proxy.js";
import {getAllFeedback, getFeedbackDetail, replyFeedback, updateFeedbackStatus} from "@/api/feedback.js";
import {FEEDBACK_STATUS_CLASS, FEEDBACK_STATUS_LABEL} from "@/utils/feedbackStatus.js"
import { updateUserRole } from "@/api/user.js";
import {userRegister} from "@/api/auth.js";
// 控制顶部标签切换
const currentTopTab = ref('query');
// 部门ID与名称映射表（统一维护）
const deptIdMap = {
  1: '新闻部',
  2: '编辑部',
  3: '运营部',
  'news': 1,
  'edit': 2,
  'operation': 3,
};
// 标签切换时的初始化逻辑
const handleTabChange = async (tab) => {
  currentTopTab.value = tab;
  switch (tab) {
    case 'query':
      await fetchTaskList();
      break;
    case 'manage':
      await loadUserList();
      await fetchProxyList();
      break;
    case 'user':
      await fetchUserList();
      break;
    case 'summary':
      break;
    case 'account':
      await loadUserList();
      break;
    case 'feedback':  // 添加反馈标签的处理
      await fetchAllFeedback();
      break;
  }
};

// ====================== 1. 任务查询相关 ======================
// 查询表单
const queryForm = reactive({
  month: '',
  deptId: '',
  article_title: ''
});
// 任务列表
const taskList = ref([]);
// 任务编辑弹窗
const taskEditDialogVisible = ref(false);
const isAddTask = ref(false);
const taskEditForm = reactive({
  record_id: '',
  created_at: '',
  article_title: '',
  real_names: [], // 替换userAmountDetail，存人员数组
  fee_amount: 0,  // 拆分金额单独存储
  deptId: ''
});
// 获取任务列表
const fetchTaskList = async () => {
  try {
    const currentYear = new Date().getFullYear();
    // 构造接口请求参数
    const requestParams = {
      statistical_month: queryForm.month ? `${currentYear}-${queryForm.month.padStart(2, '0')}` : '',
      department_id: deptIdMap[queryForm.deptId] || '',
      article_title: queryForm.article_title
    };
    const res = await getDepartmentRoyalty(requestParams);
    if (res.res_code === '0000') {
      // 兜底res.data为空的情况
      const list = res.data?.list || [];
      taskList.value = list.map(item => {
        const userNames = item.real_names ? item.real_names.join(',') : '未知人员';
        const amount = item.fee_amount || 0;
        return {
          ...item,
          userAmountDetail: `${userNames}(${amount}元)`,
          deptName: deptIdMap[item.department_id] || '未知部门'
        };
      });
    } else {
      ElMessage.error(`获取任务列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，获取任务列表失败');
  }
};
// 打开任务编辑弹窗
const openTaskEditDialog = (item = {}) => {
  isAddTask.value = !item.record_id;
  taskEditForm.record_id = item.record_id || '';
  taskEditForm.created_at = item.created_at || '';
  taskEditForm.article_title = item.article_title || '';
  taskEditForm.real_names = item.real_names || []; // 存人员数组
  taskEditForm.fee_amount = item.fee_amount || 0;  // 存金额数字
  taskEditForm.deptId = item.department_id ? (item.department_id === 1 ? 'news' : 'edit') : 'news';
  taskEditDialogVisible.value = true;
};
// 提交任务编辑
const submitTaskEdit = async () => {

  // 新增任务不校验created_at，编辑可保留；校验核心字段
  if (!taskEditForm.article_title || !taskEditForm.deptId || taskEditForm.fee_amount <= 0) {
    ElMessage.warning('请填写任务名称、选择部门并输入有效金额');
    return;
  }
  try {
    // 构造接口要求的参数
    const submitParams = {
      record_id: taskEditForm.record_id,
      article_title: taskEditForm.article_title,
      real_names: taskEditForm.real_names,
      fee_amount: taskEditForm.fee_amount,
      department_id: deptIdMap[taskEditForm.deptId]
    };
    const res = await addRoyaltyRecord(submitParams);
    if (res.res_code === '0000') {
      ElMessage.success(isAddTask.value ? '新增任务成功' : '编辑任务成功');
      taskEditDialogVisible.value = false;
      await fetchTaskList();
    } else {
      ElMessage.error(`${isAddTask.value ? '新增' : '编辑'}任务失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error(`网络异常，${isAddTask.value ? '新增' : '编辑'}任务失败`);
  }
};
// 删除任务
const deleteTask = async (record_id) => {
  if (!record_id) return; // 空值校验
  try {
    const res = await deleteRoyaltyRecord(record_id);
    if (res.res_code === '0000') {
      ElMessage.success('删除任务成功');
      await fetchTaskList();
    } else {
      ElMessage.error(`删除任务失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，删除任务失败');
  }
};
//=====================反馈处理==============================================
// 基础变量定义
const feedbackList = ref([]); // 原始反馈列表（接口返回）
const totalFeedback = ref(0);
const currentPage = ref(1); // 条目分页的当前页
const pageSize = ref(2); // 每页显示2-3个条目（这里设为2）
const filterMonth = ref(''); // 筛选月份
const filterName = ref(''); // 筛选姓名

// 反馈查询参数（接口用）
const feedbackQueryParams = reactive({
  page: 1,
  size: 100, // 接口一次性多查点数据，前端做分页
  status: ''
});

// 状态弹窗/回复弹窗变量
const replyDialogVisible = ref(false);
const currentFeedbackId = ref('');
const replyForm = reactive({ reply_content: '' });
const statusDialogVisible = ref(false);
const statusForm = reactive({ status: 'pending' });
// 筛选后的列表（按月份+姓名模糊匹配）
const filteredFeedbackList = computed(() => {
  let list = [...feedbackList.value];

  // 月份筛选
  if (filterMonth.value) {
    list = list.filter(item => {
      const createMonth = item.created_at?.split('-')[1];
      return createMonth === filterMonth.value.padStart(2, '0');
    });
  }

  // 姓名模糊筛选
  if (filterName.value) {
    const keyword = filterName.value.trim().toLowerCase();
    list = list.filter(item => item.real_name?.toLowerCase().includes(keyword));
  }

  // 前端分页（按当前页+每页条数截取）
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return list.slice(start, end);
});

// 条目分页的总页数
const totalItemPages = computed(() => {
  let list = [...feedbackList.value];

  // 先做筛选
  if (filterMonth.value) {
    list = list.filter(item => {
      const createMonth = item.created_at?.split('-')[1];
      return createMonth === filterMonth.value.padStart(2, '0');
    });
  }
  if (filterName.value) {
    const keyword = filterName.value.trim().toLowerCase();
    list = list.filter(item => item.real_name?.toLowerCase().includes(keyword));
  }

  return Math.ceil(list.length / pageSize.value) || 1;
});
// 获取接口数据
const fetchAllFeedback = async () => {
  try {
    const res = await getAllFeedback(feedbackQueryParams);
    if (res.res_code === '0000') {
      feedbackList.value = res.data.list || [];
      totalFeedback.value = res.data.total || 0;
      currentPage.value = 1; // 重置页码
    } else {
      ElMessage.error(`查询失败：${res.res_msg}`);
      feedbackList.value = [];
    }
  } catch (err) {
    ElMessage.error('网络异常，查询反馈失败');
    feedbackList.value = [];
  }
};

// 前端筛选按钮点击
const handleFrontendFilter = () => {
  currentPage.value = 1; // 筛选后重置页码
};

// 条目分页切换
const handlePageChange = (page) => {
  if (page < 1 || page > totalItemPages.value) return;
  currentPage.value = page;
};

// 回复弹窗相关
const openReplyDialog = (feedbackId) => {
  currentFeedbackId.value = feedbackId;
  replyForm.reply_content = '';
  replyDialogVisible.value = true;
};
const closeReplyDialog = () => {
  replyDialogVisible.value = false;
  currentFeedbackId.value = '';
  replyForm.reply_content = '';
};
const submitReply = async () => {
  if (!replyForm.reply_content.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  try {
    const res = await replyFeedback(currentFeedbackId.value, { reply_content: replyForm.reply_content });
    if (res.res_code === '0000') {
      ElMessage.success('回复成功');
      closeReplyDialog();
      await updateFeedbackStatus(currentFeedbackId.value, { status: 'replied' });
      await fetchAllFeedback();
    } else {
      ElMessage.error(`回复失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，回复失败');
  }
};

// 状态弹窗相关
const openStatusDialog = (feedbackId) => {
  currentFeedbackId.value = feedbackId;
  const currentItem = feedbackList.value.find(item => item.feedback_id === feedbackId);
  if (currentItem) statusForm.status = currentItem.status || 'pending';
  statusDialogVisible.value = true;
};
const closeStatusDialog = () => {
  statusDialogVisible.value = false;
  currentFeedbackId.value = '';
  statusForm.status = 'pending';
};
const submitStatusUpdate = async () => {
  try {
    const res = await updateFeedbackStatus(currentFeedbackId.value, { status: statusForm.status });
    if (res.res_code === '0000') {
      ElMessage.success('状态更新成功');
      closeStatusDialog();
      await fetchAllFeedback();
    } else {
      ElMessage.error(`状态更新失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，状态更新失败');
  }
};

// 查看详情
const viewFeedbackDetail = async (feedbackId) => {
  try {
    const res = await getFeedbackDetail(feedbackId);
    if (res.res_code === '0000') {
      ElMessage.info(`已查询反馈详情，ID：${feedbackId}`);
      console.log('反馈详情：', res.data.feedbackInfo);
    } else {
      ElMessage.error(`查询详情失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，查询详情失败');
  }
};






// ====================== 2. 代领设置相关 ======================
// 代领表单
const proxyForm = reactive({
  month: '',
  feeRecordId: '',
  proxyUserId: '', // 代领人ID
  beProxyUserId: '', // 被代领人ID
  deptId: '',
  amount: 0
});
// 代领列表
const proxyList = ref([]);
// 人员列表
const userList = ref([]);
// 稿费记录列表
const feeRecordList = ref([]);
// 加载所有成员列表（复用人员管理的fetchUserList逻辑，减少冗余）
const loadUserList = async () => {
  try {
    const res = await getUserList({ page: 1, size: 999 }); // 加大size覆盖多数场景
    if (res.res_code === '0000') {
      userList.value = res.data?.list || [];
      if (userList.value.length > 0) {
      }
    } else {
      ElMessage.error(`加载成员列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，加载成员列表失败');
  }
};
// 加载部门稿费记录
const loadFeeRecordList = async () => {
  try {
    // 调用接口：可根据需求传参（如月份、部门等）
    const res = await getDepartmentRoyalty({
      page: 1,
      size: 100, // 加载足够多的记录，无需分页
      // 可添加月份筛选：startDate/endDate 或 month参数（根据接口实际支持）
    });
    if (res.res_code === '0000') {
      // 假设接口返回的列表在res.data.list，需根据实际接口调整
      feeRecordList.value = res.data?.list || [];
    } else {
      ElMessage.error(`加载稿费记录失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，加载稿费记录失败');
  }
};
// 获取代领列表
const fetchProxyList = async () => {
  try {
    const res = await getProxyList();
    if (res.res_code === '0000') {
      const list = res.data?.list || [];

      // 转换ID为名称 - 统一使用 user 作为参数名
      proxyList.value = list.map(item => {
        // 匹配代领人姓名
        const proxyUser = userList.value.find(user => user.user_id == item.proxy_user_id)?.real_name || '未知用户';
        // 匹配被代领人姓名
        const beProxyUser = userList.value.find(user => user.user_id == item.original_user_id)?.real_name || '未知用户';
        // 匹配部门名称
        const deptName = deptIdMap[item.department_id] || '未知部门';

        return {
          ...item,
          proxyUser,
          beProxyUser,
          deptName
        };
      });
    } else {
      ElMessage.error(`获取代领列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，获取代领列表失败');
  }
};
// 提交代领设置
const submitProxySetting = async () => {
  if (!proxyForm.feeRecordId) {
    ElMessage.warning('请选择稿费记录');
    return;
  }
  if (!proxyForm.month) {
    ElMessage.warning('请选择代领月份');
    return;
  }
  if (!proxyForm.proxyUserId) {
    ElMessage.warning('请选择代领人');
    return;
  }
  if (!proxyForm.beProxyUserId) {
    ElMessage.warning('请选择被代领人');
    return;
  }
  if (!proxyForm.deptId) {
    ElMessage.warning('请选择代领部门');
    return;
  }
  const amount = Number(proxyForm.amount);
  if (isNaN(amount) || amount <= 0) {
    ElMessage.warning('请输入有效的正数金额');
    return;
  }
  try {
    const submitParams = {
      fee_record_id: proxyForm.feeRecordId,
      proxy_month: proxyForm.month,
      proxy_user_id: proxyForm.proxyUserId,
      original_user_id: proxyForm.beProxyUserId,
      department_id: deptIdMap[proxyForm.deptId], // 转数字ID传给接口
      fee_amount: proxyForm.amount
    };
    const res = await addProxyRecord(submitParams);
    if (res.res_code === '0000') {
      ElMessage.success('代领设置成功');
      // 清空表单
      proxyForm.month = '';
      proxyForm.proxyUserId = '';
      proxyForm.beProxyUserId = '';
      proxyForm.deptId = '';
      proxyForm.amount = 0;
      await fetchProxyList();
    } else {
      ElMessage.error(`代领设置失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，代领设置失败');
  }
};
// 代领编辑弹窗相关数据
const proxyEditDialogVisible = ref(false);
const proxyEditForm = reactive({
  proxy_id: '',
  proxy_user_id: '',      // 代领人ID
  original_user_id: '',   // 被代领人ID
  department_id: '',      // 部门ID
  proxy_month: '',        // 代领月份
  fee_record_id: '',      // 稿费记录ID
  article_title: '',      // 稿件标题
  fee_amount: 0,          // 金额
  description: ''         // 备注
});

// 打开修改代领弹窗
const openEditProxyDialog = (proxyItem) => {
  console.log('打开修改代领弹窗:', proxyItem);

  // 填充表单数据
  proxyEditForm.proxy_id = proxyItem.proxy_id;
  proxyEditForm.proxy_user_id = proxyItem.proxy_user_id;
  proxyEditForm.original_user_id = proxyItem.original_user_id;
  proxyEditForm.department_id = proxyItem.department_id;
  proxyEditForm.proxy_month = proxyItem.proxy_month;
  proxyEditForm.fee_record_id = proxyItem.fee_record_id;
  proxyEditForm.article_title = proxyItem.article_title || '';
  proxyEditForm.fee_amount = proxyItem.fee_amount || 0;
  proxyEditForm.description = proxyItem.description || '';

  proxyEditDialogVisible.value = true;
};
// 提交代领修改
const submitProxyEdit = async () => {
  try {
    // 准备请求参数（只提交有值的字段）
    const submitParams = {};

    // 可选字段，有值才提交
    if (proxyEditForm.proxy_user_id) submitParams.proxy_user_id = proxyEditForm.proxy_user_id;
    if (proxyEditForm.original_user_id) submitParams.original_user_id = proxyEditForm.original_user_id;
    if (proxyEditForm.department_id) submitParams.department_id = proxyEditForm.department_id;
    if (proxyEditForm.proxy_month) submitParams.proxy_month = proxyEditForm.proxy_month;
    if (proxyEditForm.fee_record_id) submitParams.fee_record_id = proxyEditForm.fee_record_id;
    if (proxyEditForm.article_title) submitParams.article_title = proxyEditForm.article_title;
    if (proxyEditForm.fee_amount && proxyEditForm.fee_amount > 0) {
      submitParams.fee_amount = proxyEditForm.fee_amount;
    }
    if (proxyEditForm.description) submitParams.description = proxyEditForm.description;

    console.log('提交修改代领参数:', {
      proxyId: proxyEditForm.proxy_id,
      params: submitParams
    });

    const res = await updateProxyRecord(proxyEditForm.proxy_id, submitParams);

    if (res.res_code === '0000') {
      ElMessage.success('修改代领记录成功');
      proxyEditDialogVisible.value = false;

      // 刷新代领列表
      await fetchProxyList();
    } else {
      ElMessage.error(`修改失败：${res.res_msg}`);
    }
  } catch (err) {
    console.error('修改代领记录异常:', err);
    ElMessage.error('网络异常，修改代领记录失败');
  }
};
// 确认撤销代领记录
const confirmDeleteProxy = (proxyItem) => {
  ElMessageBox.confirm(
      `确认要撤销这条代领记录吗？<br>
    代领人：${proxyItem.proxyUser}<br>
    被代领人：${proxyItem.beProxyUser}<br>
    金额：${proxyItem.fee_amount}元`,
      '确认撤销',
      {
        confirmButtonText: '确认撤销',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
  ).then(async () => {
    await deleteProxyRecord(proxyItem.proxy_id);
  }).catch(() => {
    // 用户取消
  });
};

// 执行撤销代领记录
const deleteProxyRecord = async (proxyId) => {
  try {
    console.log('撤销代领记录:', proxyId);

    const res = await cancelProxyRecord(proxyId);

    if (res.res_code === '0000') {
      ElMessage.success('撤销代领记录成功');

      // 刷新代领列表
      await fetchProxyList();
    } else {
      ElMessage.error(`撤销失败：${res.res_msg}`);
    }
  } catch (err) {
    console.error('撤销代领记录异常:', err);
    ElMessage.error('网络异常，撤销代领记录失败');
  }
};
const initData = async () => {

  await loadUserList();

  await loadFeeRecordList();
  await fetchProxyList();
};
// 页面挂载时加载稿费记录
onMounted(() => {
  console.log('进入系统管理员页面，组件挂载，开始初始化数据');
  initData();
  updateMonthOptions(); // 页面加载就生成月份
})

// ====================== 3. 人员管理相关 ======================
// 人员编辑弹窗
const userEditDialogVisible = ref(false);
const isAddUser = ref(false);
const userEditForm = reactive({
  userId: '',
  real_name: '',
  student_number: '', // 学号（12位纯数字）
  email: '', // 邮箱
  password: '', // 密码
  confirmPassword: '', //确认密码
  created_at: '',
  deptId: ''
});

// 角色权限编辑弹窗
const roleEditDialogVisible = ref(false);
const roleEditForm = reactive({
  user_id: '',
  real_name: '',
  is_super_admin: false, // 是否超级管理员
  department_id: '', // 部门ID
  role: '普通成员' // 角色
});

// 获取人员列表
const fetchUserList = async () => {
  try {
    const res = await getUserList();
    if (res.res_code === '0000') {
      userList.value = res.data.list || [];
    } else {
      ElMessage.error(`获取人员列表失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，获取人员列表失败');
  }
};

// 打开人员基础信息编辑弹窗
const editUser = (item = {}) => {
  isAddUser.value = !item.user_id;
  userEditForm.userId = item.user_id || '';
  userEditForm.real_name = item.real_name || '';
  userEditForm.student_number = item.student_number || ''; // 新增
  userEditForm.email = item.email || ''; // 新增
  userEditForm.created_at = item.created_at || '';
  userEditForm.deptId = item.department_id || '1';
  userEditDialogVisible.value = true;
};

// 提交人员基础信息编辑
const submitUserEdit = async () => {
  // 1. 新增成员：注册接口校验规则
  if (isAddUser.value) {
    // 校验真实姓名（不少于两个字的纯中文）
    const realName = userEditForm.real_name.trim();
// 纯中文正则：匹配2个及以上中文字符（排除空格、数字、字母、符号）
    const chineseReg = /^[\u4e00-\u9fa5]{2,}$/;

    if (!realName) {
      ElMessage.warning('请输入真实姓名');
      return;
    }
    if (!chineseReg.test(realName)) {
      ElMessage.warning('真实姓名需为不少于两个字的纯中文');
      return;
    }
    // 校验学号（12位纯数字）
    const studentNumberReg = /^\d{12}$/;
    if (!userEditForm.student_number || !studentNumberReg.test(userEditForm.student_number)) {
      ElMessage.warning('请输入12位纯数字的学号');
      return;
    }
    // 校验邮箱格式
    const emailReg = /^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!userEditForm.email || !emailReg.test(userEditForm.email)) {
      ElMessage.warning('请输入有效的邮箱地址');
      return;
    }
    // 校验密码
    if (!userEditForm.password.trim()) {
      ElMessage.warning('请设置密码');
      return;
    }
    // 校验确认密码
    if (userEditForm.password !== userEditForm.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致');
      return;
    }

    try {
      // 构造注册接口参数
      const registerParams = {
        sign: 'admin_sign_123456', // 签名（根据实际业务规则生成）
        student_number: userEditForm.student_number,
        real_name: userEditForm.real_name,
        password: userEditForm.password,
        email: userEditForm.email
      };
      const res = await userRegister(registerParams);
      if (res.res_code === '0000') {
        ElMessage.success('新增成员（注册）成功');
        userEditDialogVisible.value = false;
        await fetchUserList(); // 刷新人员列表
        // 清空表单
        Object.assign(userEditForm, {
          userId: '',
          real_name: '',
          student_number: '',
          email: '',
          password: '',
          confirmPassword: ''
        });
      } else {
        ElMessage.error(`新增成员失败：${res.res_msg}`);
      }
    } catch (err) {
      ElMessage.error('网络异常，新增成员失败');
      console.error('注册接口调用失败：', err);
    }
    return;
  }

  // 2. 编辑成员：保留原有逻辑
  if (!userEditForm.real_name || !userEditForm.created_at || !userEditForm.deptId) {
    ElMessage.warning('请填写所有必填字段');
    return;
  }
  try {
    const res = await updateUserProfile({
      user_id: userEditForm.userId,
      real_name: userEditForm.real_name,
      student_number: userEditForm.student_number,
      email: userEditForm.email,
      department_id: userEditForm.deptId,
      created_at: userEditForm.created_at
    });
    if (res.res_code === '0000') {
      ElMessage.success('编辑成员成功');
      userEditDialogVisible.value = false;
      await fetchUserList();
    } else {
      ElMessage.error(`编辑成员失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，编辑成员失败');
  }
};

// 打开角色权限编辑弹窗
const openRoleEditDialog = (item) => {
  roleEditForm.user_id = item.user_id;
  roleEditForm.real_name = item.real_name;
  roleEditForm.is_super_admin = item.is_super_admin || false;
  roleEditForm.department_id = item.department_id || '';
  roleEditForm.role = item.role || '普通成员';
  roleEditDialogVisible.value = true;
};

// 提交角色权限修改
const submitRoleEdit = async () => {
  // 校验必填项
  if (!roleEditForm.user_id || roleEditForm.department_id === '') {
    ElMessage.warning('请选择用户所属部门');
    return;
  }

  try {
    // 构造2.5.8接口要求的参数
    const submitParams = {
      sign: 'admin_sign_123456', // 签名需根据实际业务规则生成，此处为示例
      user_id: roleEditForm.user_id,
      is_super_admin: roleEditForm.is_super_admin,
      department_id: roleEditForm.department_id
    };

    const res = await updateUserRole(submitParams);
    if (res.res_code === '0000') {
      ElMessage.success('角色权限修改成功');
      roleEditDialogVisible.value = false;
      // 同步更新用户列表中的角色和权限信息
      const updateIndex = userList.value.findIndex(u => u.user_id === roleEditForm.user_id);
      if (updateIndex > -1) {
        // 关键：系统管理员强制清空部门ID，避免触发部门管理员判断
        const syncData = {
          ...userList.value[updateIndex],
          is_super_admin: roleEditForm.is_super_admin,
          department_id: roleEditForm.is_super_admin ? '' : roleEditForm.department_id,
          role: roleEditForm.is_super_admin ? '系统管理员' : roleEditForm.role
        };
        userList.value[updateIndex] = syncData;
      }
    }else {
      ElMessage.error(`角色修改失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，角色修改失败');
    console.error('修改用户角色报错：', err);
  }
};

// 删除人员
const deleteUser = async (userId) => {
  try {
    // 此处需替换为实际删除用户的接口，当前暂用cancelProxyRecord示例
    const res = await cancelProxyRecord(userId);
    if (res.res_code === '0000') {
      ElMessage.success('移除成员成功');
      await fetchUserList();
    } else {
      ElMessage.error(`移除成员失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.error('网络异常，移除成员失败');
  }
};
// ====================== 4. 稿费汇总相关 ======================
// 汇总表单：严格匹配接口的三个必填参数，一个都不少！
// 选中的年份
const selectedYear = ref('');
// 选中的月份（值为MM格式，如01）
const selectedMonth = ref('');
// 月份选项列表（根据年份动态生成）
const monthOptions = ref([]);
// 汇总表单的statistical_month（最终拼接成YYYY-MM）
const summaryForm = reactive({
  statistical_month: '',
  department_id: '',
  format: ''
});

// 生成年份选项
const generateYearOptions = () => {
  const currentYear = new Date().getFullYear();
  const startYear = currentYear;
  const endYear = currentYear + 3;
  const years = [];
  for (let y = startYear; y <= endYear; y++) {
    years.push(y);
  }
  return years;
};

// 切换年份时，更新月份选项
const updateMonthOptions = () => {
  selectedMonth.value = ''; // 重置月份
  // 生成1-12月的选项（格式：01=1月）
  monthOptions.value = Array.from({ length: 12 }, (_, i) => {
    const monthNum = i + 1;
    const monthStr = String(monthNum).padStart(2, '0');
    return {
      value: monthStr,
      label: `${monthNum}月`
    };
  });
};

// 选中月份后，拼接成YYYY-MM格式赋值给statistical_month
const setStatisticalMonth = () => {
  if (selectedYear.value && selectedMonth.value) {
    summaryForm.statistical_month = `${selectedYear.value}-${selectedMonth.value}`;
    fetchSummaryData(); // 自动触发数据获取
  }
};

// 新闻部汇总数据
const newsDeptSummary = ref([]);
// 编辑部汇总数据
const editDeptSummary = ref([]);
// 总汇总数据
const totalSummary = ref([]);

// 新闻部总金额（格式化）
const newsDeptTotal = computed(() => {
  // 修正：直接累加接口原始fee_amount（数字），而非格式化后的totalAmount（字符串）
  return formatMoney(newsDeptSummary.value.reduce((sum, item) => sum + Number(item.fee_amount || 0), 0));
});
// 编辑部总金额（格式化）
const editDeptTotal = computed(() => {
  return formatMoney(editDeptSummary.value.reduce((sum, item) => sum + Number(item.totalAmount), 0));
});

// 平台总金额（格式化）
const platformTotal = computed(() => {
  return formatMoney(Number(newsDeptTotal.value) + Number(editDeptTotal.value));
});

// 获取汇总数据：基于接口的statistical_month（YYYY-MM）生成时间范围
// 获取汇总数据：适配2.5.10 部门稿费查询接口
const fetchSummaryData = async () => {
  if (!summaryForm.statistical_month) {
    ElMessage.warning('请选择统计月份（YYYY-MM）');
    return;
  }

  try {
    const [year, month] = summaryForm.statistical_month.split('-');
    const startDate = `${year}-${month}-01`;
    const endDate = new Date(Number(year), Number(month), 0).toISOString().split('T')[0];

    // 修正1：调用正确的部门稿费接口getDepartmentRoyalty
    // 修正2：department_id不传0，新闻部固定传1（部门管理员默认查本部门）
    const res = await getDepartmentRoyalty ({
      startDate,
      endDate,
      page: 1,
      size: 100, // 一次性加载所有数据，避免分页
      department_id: 1 // 新闻部固定ID
    });

    if (res.res_code === '0000') {
      const allData = res.data.list || [];
      // 处理新闻部数据：仅保留必要映射，移除无用字段
      newsDeptSummary.value = allData.map(item => ({
        ...item,
        // 修正3：real_names数组拼接，避免显示["张三"]格式
        real_names: Array.isArray(item.real_names) ? item.real_names.join(', ') : item.real_names || '-'
      }));
      // 编辑部数据需单独调用接口（department_id=2）
      const editRes = await getDepartmentRoyalty ({
        startDate,
        endDate,
        page: 1,
        size: 100,
        department_id: 2 // 编辑部ID
      });
      if (editRes.res_code === '0000') {
        editDeptSummary.value = editRes.data.list.map(item => ({
          ...item,
          real_names: Array.isArray(item.real_names) ? item.real_names.join(', ') : item.real_names || '-'
        }));
      }
      // 生成人员维度总汇总
      generateTotalSummary([...allData, ...(editRes.data?.list || [])]);
    } else {
      ElMessage.error(`获取汇总数据失败：${res.res_msg}`);
    }
  } catch (err) {
    console.error('获取汇总数据失败:', err);
    ElMessage.error('网络异常，获取汇总数据失败');
  }
};
// 生成总汇总数据（修正：先求和再格式化，避免字符串拼接）
const generateTotalSummary = (allData) => {
  const summaryMap = new Map();
  allData.forEach(item => {
    const userCount = item.user_ids?.length || 0;
    // 容错：避免除以0
    if (userCount === 0) return;
    for (let i = 0; i < userCount; i++) {
      const userId = item.user_ids[i];
      const realName = item.real_names[i] || '未知';
      if (!summaryMap.has(userId)) {
        summaryMap.set(userId, {
          userId, real_name: realName, newsAmount: 0, editAmount: 0, bigImgAmount: 0, totalAmount: 0
        });
      }
      const userSummary = summaryMap.get(userId);
      const perUserAmount = Number(item.fee_amount) / userCount;
      if (item.department_id === 1) userSummary.newsAmount += perUserAmount;
      if (item.department_id === 2) userSummary.editAmount += perUserAmount;
    }
  });
  // 修正：先保留数字求和，渲染时再格式化
  totalSummary.value = Array.from(summaryMap.values()).map(item => ({
    ...item,
    totalAmount: item.newsAmount + item.editAmount + item.bigImgAmount
  }));
};
// 导出汇总数据：严格按接口文档校验所有必填项，参数1:1匹配
const exportSummaryData = async () => {
  // 1. 校验必填项
  if (!summaryForm.statistical_month) {
    ElMessage.warning('请选择统计月份（YYYY-MM）');
    return;
  }
  if (!summaryForm.department_id) {
    ElMessage.warning('请选择部门ID');
    return;
  }
 /* if (!summaryForm.format) {
    ElMessage.warning('请选择导出格式（Excel/PDF）');
    return;
  }*/

  try {
    // 2. 构建参数：将department_id转为普通数字，避免BigInt序列化错误
    const params = {
      statistical_month: summaryForm.statistical_month,
      department_id: Number(summaryForm.department_id), // 关键修复：用Number替代BigInt
      format: summaryForm.format
    };

    console.log('导出接口请求参数：', params);
    const res = await exportRoyaltyRecord(params);
    console.log(res);
    // 3. 处理返回结果
    if (res.res_code === '0000') {
      const { fileUrl, recordCount, exportTime } = res.data;
      ElMessage.success(`导出成功！${exportTime}，共${recordCount}条记录`);
      if (fileUrl) {
        const a = document.createElement('a');
        a.href = fileUrl;
        a.download = `稿费汇总_${summaryForm.statistical_month}.${summaryForm.format.toLowerCase()}`;
        a.click();
      }
    } else {
      ElMessage.error(`导出失败：${res.res_msg}`);
    }
  } catch (err) {
    console.error('导出接口调用失败：', err);
    ElMessage.error('导出失败：' + (err.response?.data?.res_msg || '网络异常'));
  }
};
// ====================== 5. 做账稿费相关 ======================
// 做账表单
const accountForm = reactive({
  month: ''
});
// 做账列表
const accountList = ref([]);

// 获取做账数据
const fetchAccountData = async () => {
  if (!accountForm.month) {
    ElMessage.warning('请选择月份');
    return;
  }
  // 构造YYYY-MM格式的月份参数
  const currentYear = new Date().getFullYear();
  const statisticalMonth = `${currentYear}-${accountForm.month}`;

  try {
    ElMessage.loading({ message: '加载中...', duration: 0 });
    // 传入正确的参数名（proxy_month），匹配接口要求
    const res = await getProxyList({ proxy_month: statisticalMonth });
    ElMessage.closeAll();

    if (res.res_code === '0000') {
      const rawList = res.data.list || [];
      // 格式化数据：ID转姓名、部门ID转名称、金额格式化
      accountList.value = rawList.map(item => ({
        ...item,
        // 代领人姓名（从userList匹配）
        proxy_user_name: userList.value.find(u => u.user_id === item.proxy_user_id)?.real_name || '未知用户',
        // 被代领人姓名
        original_user_name: userList.value.find(u => u.user_id === item.original_user_id)?.real_name || '未知用户',
        // 部门名称
        department_name: deptIdMap[item.department_id] || '未知部门',
        // 最终金额（若无则用代领金额）
        final_fee_amount: item.final_fee_amount || item.proxy_fee_amount
      }));
    } else {
      ElMessage.error(`获取做账数据失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.closeAll();
    ElMessage.error('网络异常，获取做账数据失败');
    console.error('做账数据请求失败：', err);
  }
};

// 导出做账数据
const exportAccountData = async () => {
  if (!accountForm.month) {
    ElMessage.warning('请选择月份');
    return;
  }
  const currentYear = new Date().getFullYear();
  const statisticalMonth = `${currentYear}-${accountForm.month}`;

  try {
    ElMessage.loading({ message: '导出中...', duration: 0 });
    const res = await exportRoyaltyRecord({
      statistical_month: statisticalMonth,
      type: 'proxy' // 标记为代领调整后稿费导出
    });
    ElMessage.closeAll();

    if (res.res_code === '0000') {
      // 下载文件
      const link = document.createElement('a');
      link.href = res.data.download_url;
      link.download = `代领调整后稿费_${statisticalMonth}.xlsx`;
      link.click();
      ElMessage.success('导出成功');
    } else {
      ElMessage.error(`导出失败：${res.res_msg}`);
    }
  } catch (err) {
    ElMessage.closeAll();
    ElMessage.error('网络异常，导出失败');
    console.error('做账数据导出失败：', err);
  }
};
// 初始化任务查询
fetchTaskList();
loadUserList(); // 初始化加载成员列表
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
  align-items: center; /* 垂直居中 */
  margin-bottom: 15px;
  width: 100%;
}
/* 核心：实现字符两端对齐的label样式 */
.justify-label {
  display: inline-block;
  width: 70px; /* 固定宽度（根据最长文本调整，比如"确认密码："需要100px） */
  text-align: justify; /* 字符两端对齐 */
  text-justify: distribute-all-lines; /* IE兼容，让空格均匀分布 */
  line-height: 1; /* 消除行高影响 */
  margin-right: 10px; /* 与输入框的间距 */
  font-size: 14px;
}
/* 关键：给label添加一个隐藏的伪元素，让text-align: justify生效 */
.justify-label::after {
  content: '';
  display: inline-block;
  width: 100%; /* 占满label宽度，触发两端对齐 */
}
/*!* label样式 - 核心：固定宽度+右对齐 *!
.form-row label {
  display: inline-block;
  width: 80px; !* 固定宽度，可根据最长文本调整（比如"确认密码:"最长，80px足够） *!
  text-align: right; !* 文本右对齐，实现"两端对齐"的视觉效果 *!
  margin-right: 10px; !* label和input之间的间距 *!
  font-size: 14px; !* 可选：统一字体大小 *!
}*/
/*!* 输入框样式 - 占满剩余宽度 *!
.form-row .form-input {
  flex: 1; !* 自动占满剩余宽度 *!
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box; !* 避免padding导致宽度溢出 *!
}*/
.form-select, .form-input {
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 2px;
  font-size: 14px;
}
.form-select {
  width: 120px;
}
/* 输入框样式 */
.form-input {
  flex: 1; /* 占满剩余宽度 */
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}
/* 可选：输入框聚焦样式 */
.form-input:focus {
  outline: none;
  border-color: #9b8eb4;
  box-shadow: 0 0 0 2px rgba(155, 142, 180, 0.2);
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
/* 反馈弹窗样式补充 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.dialog-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.dialog-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}
.cancel-btn {
  padding: 8px 16px;
  background-color: #e0e0e0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.confirm-btn {
  padding: 8px 16px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.reply-textarea {
  width: 100%;
  height: 120px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}
/* 代领设置操作按钮 */
.op-btn {
  padding: 6px 12px;
  background-color: #A59EB2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
  font-size: 13px;
}
.op-btn.danger {
  background-color:#A59EB2;
}
.op-btn:hover {
  opacity: 0.9;
}
/* 空数据提示 */
.empty-tip {
  text-align: center;
  padding: 30px;
  color: #999;
  font-style: italic;
}

/* 分页控件 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}
.page-btn {
  padding: 6px 12px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 部门区块样式 */
.dept-section {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 6px;
  background-color: #fafafa;
}
.dept-section label {
  font-weight: bold;
  margin-bottom: 10px;
  display: block;
  color: #333;
}

/* 粗体总计行 */
.total-row.bold {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}
.export-btn {
  padding: 8px 20px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 15px;
}
.export-btn:hover {
  background-color: #73d13d;
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

/* 代领设置表单行的特殊样式（仅覆盖需要修改的属性） */
.manage-form .form-row {
  margin-bottom: 16px; /* 仅修改行间距 */
}

/* 代领设置区域的表单控件样式（作用域限定） */
.manage-form .form-select,
.manage-form .form-input {
  flex: 1; /* 占满剩余空间 */
  min-width: 200px; /* 最小宽度，防止太窄 */
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: auto; /* 取消通用的固定宽度 */
}
.manage-form .form-select {
  width: auto; /* 取消通用的 120px 宽度 */
}
/* 小控件单独调整宽度（如部门选择） */
.manage-form .small-input {
  flex: none; /* 取消自适应 */
  width: 120px; /* 固定小宽度 */
}
/* 超小控件（如金额输入） */
.manage-form .tiny-input {
  flex: none;
  width: 100px;
}

/* 反馈区域新样式 */
.feedback-filter {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}
.feedback-item-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.feedback-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  background-color: #f9f9f9;
}
.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: bold;
}
.feedback-id {
  color: #666;
}
.feedback-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  line-height: 1.6;
}
.feedback-content .label {
  color: #999;
  font-weight: 500;
}
.feedback-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-tag.pending {
  background-color: #9b8eb4;
  color: #ff3c3c;
}
.status-tag.replied {
  background-color: #9b8eb4;
  color: #40a9ff;
}
/* 新增：状态标签样式 */
.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
.status-tag.success {
  background-color: #f0f9ff;
  color: #13ce66;
}
.status-tag.default {
  background-color: #f5f5f5;
  color: #666;
}

/* 新增：按钮样式 */
.add-btn {
  padding: 6px 12px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  /* 移除左外边距，添加右对齐相关样式 */
 /* margin-left: 0;*/
  margin-right: 60px;
  margin-top: -40px;
  /* 核心：设置按钮靠右 */
  display: inline-block;
  float: right;
  /* 可选：hover效果，保持和其他按钮一致 */
  transition: background-color 0.2s ease;
}

.add-btn:hover {
  background-color: #8a7aa8;
}
.op-btn.primary {
  background-color: #9b8eb4;
  color: #fff;
  margin: 0 5px;
  width:80px;
  height: 40px;
}
.op-btn.danger {
  background-color: #9b8eb4;
  color: #fff;
}

/* 原有样式保持不变 */
.section-title {
  display: flex;
  align-items: center;
  margin: 20px 0;
}
.op-btn-group {
  display: flex; /* 弹性布局让按钮并排 */
  gap: 8px; /* 按钮之间的间距，可根据需求调整（如6px/10px） */
}
.query-btn{
  padding: 6px 12px;
  background-color: #9b8eb4;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  /* 移除左外边距，添加右对齐相关样式 */
  /* margin-left: 0;*/
  /*margin-right: 60px;
  margin-top: -40px;*/
  /* 核心：设置按钮靠右 */
  display: inline-block;
 /* float: right;*/
  /* 可选：hover效果，保持和其他按钮一致 */
  transition: background-color 0.2s ease;

}
</style>