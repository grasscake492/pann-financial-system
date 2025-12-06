<template>
  <PageBackground2>
    <div class="title-line"></div>
    <template #page-title>个人信息</template>
    <!-- 内容卡片插槽 -->
    <template #card-content>
      <!-- 加载中提示（请求数据时显示） -->
      <div v-if="isLoading" class="loading-tip">加载中...</div>

      <!-- 无数据/请求失败提示 -->
      <div v-else-if="!userInfo" class="empty-tip">
        获取个人信息失败，请刷新重试
      </div>

      <!-- 信息列表（请求成功后渲染） -->
      <div v-else class="info-list">
        <div class="info-item">
          <span class="info-label">姓名：</span>
          <span class="info-value">{{ userInfo.real_name || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">学号：</span>
          <span class="info-value">{{ userInfo.student_number || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">邮箱：</span>
          <span class="info-value">{{ userInfo.email || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">用户ID：</span>
          <span class="info-value">{{ userInfo.user_id || '未知' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">用户角色：</span>
          <span class="info-value">{{ userInfo.role || '普通用户' }}</span>
        </div>
      </div>

      <!-- 按钮区域 -->
      <div class="button-group">
        <button class="btn" @click="openPwdDialog">修改密码</button>
        <button class="btn" @click="openInfoDialog">修改信息</button>
        <button class="btn" @click="goLogin">退出登录</button>
      </div>

      <!-- 修改密码弹窗 -->
      <ElDialog
          v-model="pwdDialogVisible"
          title="修改密码"
          width="400px"
          @close="resetPwdForm"
      >
        <ElForm
            ref="pwdFormRef"
            :model="pwdForm"
            :rules="pwdRules"
            label-width="100px"
        >
          <ElFormItem label="原密码" prop="oldPassword">
            <ElInput v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />
          </ElFormItem>
          <ElFormItem label="新密码" prop="newPassword">
            <ElInput v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" />
          </ElFormItem>
          <ElFormItem label="确认新密码" prop="confirmNewPwd">
            <ElInput v-model="pwdForm.confirmNewPwd" type="password" placeholder="请确认新密码" />
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton @click="pwdDialogVisible = false">取消</ElButton>
          <ElButton type="primary" @click="submitChangePwd">确认修改</ElButton>
        </template>
      </ElDialog>

      <!-- 修改信息弹窗 -->
      <ElDialog
          v-model="infoDialogVisible"
          title="修改个人信息"
          width="400px"
          @close="resetInfoForm"
      >
        <ElForm
            ref="infoFormRef"
            :model="infoForm"
            :rules="infoRules"
            label-width="100px"
        >
          <ElFormItem label="真实姓名" prop="real_name">
            <ElInput v-model="infoForm.real_name" placeholder="请输入真实姓名" />
          </ElFormItem>
          <ElFormItem label="邮箱" prop="email">
            <ElInput v-model="infoForm.email" placeholder="请输入邮箱" />
          </ElFormItem>
        </ElForm>
        <template #footer>
          <ElButton @click="infoDialogVisible = false">取消</ElButton>
          <ElButton type="primary" @click="submitEditInfo">确认修改</ElButton>
        </template>
      </ElDialog>
    </template>
  </PageBackground2>
</template>

<script setup>
// 1. 核心依赖导入
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import PageBackground2 from '@/components/PageBackground2.vue';
import { getUserInfo, changePassword, editUserInfo } from '@/api/user';
import { getToken, removeToken } from '@/utils/auth.js';
// Element Plus组件导入
import { ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';


import { clearLoginState } from '@/utils/auth.js';
// 加密库（密码加密+sign签名用）
import md5 from 'js-md5';

// 2. 响应式变量定义
const userInfo = ref(null); // 用户信息
const isLoading = ref(false); // 加载状态
const router = useRouter();
// 后端签名秘钥（需替换为真实秘钥）
const SECRET_KEY = 'pannfmis2025';

// 3. 修改密码弹窗相关
const pwdDialogVisible = ref(false); // 弹窗显隐
const pwdForm = reactive({
  oldPassword: '', // 原密码
  newPassword: '', // 新密码
  confirmNewPwd: '' // 确认新密码
});
// 密码表单校验规则
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^(?=.*\d)(?=.*[a-zA-Z]).{8,16}$/, message: '新密码需8-16位，包含字母和数字', trigger: 'blur' }
  ],
  confirmNewPwd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次密码输入不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};
const pwdFormRef = ref(null); // 密码表单ref

// 4. 修改信息弹窗相关
const infoDialogVisible = ref(false); // 弹窗显隐
const infoForm = reactive({
  real_name: '', // 姓名
  email: '' // 邮箱
});
// 信息表单校验规则
const infoRules = {
  real_name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式不正确', trigger: 'blur' }
  ]
};
const infoFormRef = ref(null); // 信息表单ref

// 5. 退出登录方法
const goLogin = () => {
  clearLoginState(); // 一键清空
  router.push({ path: '/login' });
};

// 6. 页面挂载时请求用户信息
onMounted(async () => {
  try {
    // 检查登录态
    const token = getToken();
    if (!token) {
      ElMessage.warning('请先登录');
      router.push({ path: '/login' });
      return;
    }

    isLoading.value = true;
    // 调用用户信息接口
    const res = await getUserInfo();
    const decryptedData = res.data;
    userInfo.value = decryptedData[0];

    // 初始化修改信息表单
    if (userInfo.value) {
      infoForm.real_name = userInfo.value.real_name || '';
      infoForm.email = userInfo.value.email || '';
    }
  } catch (error) {
    console.error('获取用户信息失败：', error);
    userInfo.value = null;
    ElMessage.error('获取个人信息失败，请刷新重试');
  } finally {
    isLoading.value = false;
  }
});

// 7. 修改密码弹窗方法
// 打开弹窗
const openPwdDialog = () => {
  pwdDialogVisible.value = true;
};
// 重置表单
const resetPwdForm = () => {
  pwdFormRef.value?.resetFields();
  pwdForm.oldPassword = '';
  pwdForm.newPassword = '';
  pwdForm.confirmNewPwd = '';
};
// 提交修改密码（补充sign签名+对齐接口字段）
const submitChangePwd = async () => {
  try {
    // 表单校验
    await pwdFormRef.value.validate();

    // 1. 生成sign签名（文档要求：oldPassword + newPassword + 秘钥）
    const signStr = pwdForm.oldPassword + pwdForm.newPassword + SECRET_KEY;
    const sign = md5(signStr);

    // 2. 构造请求参数（对齐接口文档字段名）
    const pwdData = {
      old_password: md5(pwdForm.oldPassword), // 文档字段名：old_password
      new_password: md5(pwdForm.newPassword), // 文档字段名：new_password
      sign: sign // 文档必填sign字段
    };

    // 3. 调用真实修改密码接口
    const res = await changePassword(pwdData);

    // 4. 处理接口返回结果
    if (res.res_code === '0000') {
      ElMessage.success('密码修改成功，请重新登录');
      pwdDialogVisible.value = false;
      // 清空token并跳转登录页
      removeToken();
      localStorage.removeItem('pann_user_role');
      router.push('/login');
    } else {
      ElMessage.error(res.res_msg || '修改密码失败');
    }
  } catch (error) {
    console.error('修改密码失败：', error);
    if (error.response?.data?.res_msg) {
      ElMessage.error(error.response.data.res_msg);
    } else if (error.name === 'ValidationError') {
      ElMessage.error('表单填写有误，请检查');
    } else {
      ElMessage.error('修改密码失败，请稍后重试');
    }
  }
};

// 8. 修改信息弹窗方法
// 打开弹窗
const openInfoDialog = () => {
  infoDialogVisible.value = true;
};
// 重置表单
const resetInfoForm = () => {
  infoFormRef.value?.resetFields();
  if (userInfo.value) {
    infoForm.real_name = userInfo.value.real_name || '';
    infoForm.email = userInfo.value.email || '';
  }
};
// 提交修改信息（补充sign签名+对齐接口字段）
const submitEditInfo = async () => {
  try {
    // 表单校验
    await infoFormRef.value.validate();

    // 1. 生成sign签名（文档要求：real_name + email + 秘钥）
    const signStr = infoForm.real_name + infoForm.email + SECRET_KEY;
    const sign = md5(signStr);

    // 2. 构造请求参数（对齐接口文档字段名）
    const infoData = {
      real_name: infoForm.real_name, // 文档字段名：real_name
      email: infoForm.email, // 文档字段名：email
      sign: sign // 文档必填sign字段
    };

    // 3. 调用真实修改信息接口
    const res = await editUserInfo(infoData);

    // 4. 处理接口返回结果
    if (res.res_code === '0000') {
      ElMessage.success('信息修改成功');
      infoDialogVisible.value = false;
      // 刷新用户信息
      const res = await getUserInfo();
      userInfo.value = res.data[0];
    } else {
      ElMessage.error(res.res_msg || '修改信息失败');
    }
  } catch (error) {
    console.error('修改信息失败：', error);
    if (error.response?.data?.res_msg) {
      ElMessage.error(error.response.data.res_msg);
    } else if (error.name === 'ValidationError') {
      ElMessage.error('表单填写有误，请检查');
    } else {
      ElMessage.error('修改信息失败，请稍后重试');
    }
  }
};
</script>


<style scoped>
/* 统一缩进为2个空格，删除冗余空行 */
/* 信息项样式 */
.info-list {
  margin-top: 30px;
}

.info-item {
  margin-bottom: 40px; /* 信息项间距 */
  font-size: 16px;
  color: #333;
}

.info-label {
  display: inline-block;
  width: 100px; /* 固定标签宽度，对齐整齐 */
  font-weight: 500;
}

/* 按钮区域样式 */
.button-group {
  margin-top: 80px; /* 与信息项拉开距离 */
  display: flex;
  justify-content: center; /* 按钮居中 */
  gap: 60px; /* 按钮间距 */
}

.btn {
  padding: 8px 30px; /* 按钮大小 */
  background-color: #9b8eb4; /* 紫色按钮 */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn:hover {
  background-color: #8a7da8; /* hover深色 */
}

.title-line {
  width: 4px;
  height: 28px;
  background-color: #9b8eb4;
  margin-right: 12px;
}

.loading-tip, .empty-tip {
  text-align: center;
  padding: 30px 0;
  color: #999;
  font-size: 14px;
}
</style>
<style scoped></style>