<template>
  <PageBackground2>
    <Navbar style="background-color:transparent!important;" />
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
          <span class="info-label">部门：</span>
          <span class="info-value">
            {{ getUserRoleText }}
          </span>
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
<!--          <ElFormItem label="原密码" prop="oldPassword">
            <ElInput v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" />
          </ElFormItem>-->
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
          <ElFormItem label="学号" prop="student_number">
            <ElInput v-model="infoForm.student_number" placeholder="请输入学号" />
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
//================导入模块================
// 1. Vue核心
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

// 2. 组件
import PageBackground2 from '@/components/PageBackground2.vue';
import Navbar from "@/components/Navbar.vue";

// 3. Element Plus组件
import { ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';

// 4. Store
import { useUserStore } from "@/stores/index.js";

// 5. API
import { fetchUserProfile, updateUserProfile } from '@/api/user';
import { changePassword } from "@/api/auth.js";

// 6. 工具函数
import { getToken, clearLoginState } from '@/utils/auth.js';
import { encryptData, generateSign } from "@/utils/request.js";

//================常量定义================
const SECRET_KEY = 'pannfmis2025';

//================响应式变量================
const router = useRouter();
const userStore = useUserStore();

// 用户信息相关
const userInfo = ref(null);
const isLoading = ref(false);

// 修改密码弹窗相关
const pwdDialogVisible = ref(false);
const pwdFormRef = ref(null);
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmNewPwd: ''
});

// 修改信息弹窗相关
const infoDialogVisible = ref(false);
const infoFormRef = ref(null);
const infoForm = reactive({
  real_name: '',
  student_number: '',
  email: ''
});

//================计算属性================
const getUserRoleText = computed(() => {
  const deptName = userStore.userInfo?.department_name || '';
  switch (userStore.userRole) {
    case 'super_admin':
      return `${deptName || '无'}`;
    case 'news_admin':
      return '新闻部';
    case 'editorial_admin':
      return '编辑部';
    case 'operation_admin':
      return '运营部';
    case 'normal_user':
      return '无';
    default:
      return '未知用户';
  }
});


//================表单验证规则================
const pwdRules = {
 /* oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],*/
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
const infoRules = {
  real_name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5]{2,}$/, message: '姓名需至少2位中文', trigger: 'blur' } // 新增：中文+2-4位限制
  ],
  student_number: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^[0-9]{12}$/, message: '学号需12位数字', trigger: 'blur' } // 调整：固定12位数字
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式不正确', trigger: 'blur' }
  ]
};

//================生命周期================
onMounted(async () => {
  try {
    const token = getToken();
    console.log('当前token：', token);
    if (!token) {
      ElMessage.warning('请先登录');
      await router.push({ path: '/login' });
      return;
    }

    isLoading.value = true;

    const userId = userStore.userInfo?.user_id;
    if (!userId) {
      ElMessage.warning('用户信息不完整，请重新登录');
      await router.push('/login');
      isLoading.value = false;
      return;
    }

    const res = await fetchUserProfile(userId);
    // 1. 先判断接口返回是否成功
    if (res.res_code !== '0000') {
      ElMessage.error(res.res_msg || '获取用户信息失败');
      userInfo.value = null;
      isLoading.value = false;
      return;
    }
    // 2. 解密接口返回的加密数据（关键：res.data是加密后的单个对象）
    const decryptedData = res.data; // 注意：此处需确认解密函数，若encryptData是加密，需替换为解密函数（如decryptData）
    // 3. 直接赋值为单个用户对象（而非数组），统一数据结构
    userInfo.value = decryptedData || {};
    userStore.setUserInfo(decryptedData);

    // 4. 初始化修改信息表单（用统一的userInfo.value）
    if (userInfo.value) {
      infoForm.real_name = userInfo.value.real_name || '';
      infoForm.student_number = userInfo.value.student_number || '';
      infoForm.email = userInfo.value.email || '';
    }
  } catch (error) {
    console.error('获取用户信息失败：', error);
    userInfo.value = null;

    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      clearLoginState();
      await router.push('/login');
    } else if (error.response?.status === 404) {
      ElMessage.error('用户信息不存在');
    } else {
      ElMessage.error('获取个人信息失败，请刷新重试');
    }
  } finally {
    isLoading.value = false;
  }
});

//================用户操作================
const goLogin = () => {
  //  清空 Pinia 用户状态
  userStore.clearUserInfo();

  //  清空 token / localStorage
  clearLoginState();

  //  强制回登录页（避免历史路由残留）
  router.replace({ path: '/login' });
};

//================修改密码功能================
const openPwdDialog = () => {
  pwdDialogVisible.value = true;
};

const resetPwdForm = () => {
  pwdFormRef.value?.resetFields();
  pwdForm.oldPassword = '';
  pwdForm.newPassword = '';
  pwdForm.confirmNewPwd = '';
};

const submitChangePwd = async () => {
  try {
    await pwdFormRef.value.validate();

    const userId = userStore.userInfo.user_id;
    if (!userId) {
      ElMessage.error('用户身份失效，请重新登录');
      return;
    }

    const signStr = (pwdForm.oldPassword || '') + (pwdForm.newPassword || '') + SECRET_KEY;
    const sign = generateSign(signStr);

    const pwdData = {
      new_password: pwdForm.newPassword,
      sign: sign
    };

    const res = await changePassword(userId, pwdData);

    if (res.res_code === '0000') {
      ElMessage.success('密码修改成功，请重新登录');
      pwdDialogVisible.value = false;
      clearLoginState();
      await router.push('/login');
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

//================修改信息功能================
const openInfoDialog = () => {
  infoDialogVisible.value = true;
};

const resetInfoForm = () => {
  infoFormRef.value?.resetFields();
  if (userInfo.value) {
    infoForm.real_name = userInfo.value?.real_name || '';
    infoForm.student_number = userInfo.value?.student_number || '';
    infoForm.email = userInfo.value?.email || '';
  }
};

const submitEditInfo = async () => {
  try {
    await infoFormRef.value.validate();

    const signStr = (infoForm.real_name || '') + (infoForm.student_number || '') + (infoForm.email || '') + SECRET_KEY;
    const sign = generateSign(signStr);

    const infoData = {
      real_name: infoForm.real_name,
      student_number: infoForm.student_number,
      email: infoForm.email,
      sign: sign
    };



    const currentUserId = userStore.userInfo.user_id;
    if (!currentUserId) {
      ElMessage.error('用户ID为空，无法修改信息');
      await router.push('/login');
      return;
    }

    const editRes = await updateUserProfile(currentUserId, infoData);
    if (editRes.res_code === '0000') {
      ElMessage.success('信息修改成功');
      infoDialogVisible.value = false;
/*
      if (!currentUserId) {
        ElMessage.warning('用户身份失效，请重新登录');
        await router.push('/login');
        return;
      }*/

      const userRes = await fetchUserProfile(currentUserId);

      const decryptedData = userRes.data || [];
     /* const userData = decryptedData[0] || {};*/

      userInfo.value = decryptedData;
      userStore.setUserInfo(decryptedData);

      infoForm.real_name = userInfo.value.real_name || '';
      infoForm.student_number = userInfo.value.student_number || '';
      infoForm.email = userInfo.value.email || '';
    } else {
      ElMessage.error(editRes.res_msg || '修改信息失败');
    }
  } catch (error) {
    console.error('修改信息失败：', error);
    if (error.response?.data?.res_msg) {
      ElMessage.error(error.response.data.res_msg);
    } else if (error.name === 'ValidationError') {
      ElMessage.error('表单填写有误，请检查：学号为12位数字、邮箱格式正确');
    } else if (error.message.includes('404')) {
      ElMessage.error('用户信息接口不存在，请检查接口路径');
    } else if (error.message.includes('401')) {
      ElMessage.error('登录已过期，请重新登录');
      await router.push('/login');
    } else {
      ElMessage.error('修改信息失败，请稍后重试');
    }
  }
};
</script>

<style scoped>
/* 信息项样式 */
.info-list {
  margin-top: 30px;
}

.info-item {
  margin-bottom: 40px;
  font-size: 16px;
  color: #333;
}

.info-label {
  display: inline-block;
  width: 100px;
  font-weight: 500;
}

/* 按钮区域样式 */
.button-group {
  margin-top: 80px;
  display: flex;
  justify-content: center;
  gap: 60px;
}

.btn {
  padding: 8px 30px;
  background-color: #9b8eb4;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn:hover {
  background-color: #8a7da8;
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