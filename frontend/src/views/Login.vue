<template>
  <PageBackground>
    <!-- 登录卡片容器 -->
    <div class="login-container">
      <h1 class="system-title">PANN财务管理系统</h1>

      <!-- 账号输入框 -->
      <div class="form-group">
        <input
            type="text"
            placeholder="学号"
            class="input-field"
            v-model="form.student_number"
            @blur="handleFieldBlur('student_number')"
        >
        <!-- 账号错误提示 -->
        <div class="error-tip" v-if="errors.student_number">{{ errors.student_number }}</div>
      </div>

      <!-- 密码输入框 -->
      <div class="form-group">
        <input
            type="password"
            placeholder="密码"
            class="input-field"
            v-model="form.password"
            @blur="handleFieldBlur('password')"
        >
        <!-- 密码错误提示 -->
        <div class="error-tip" v-if="errors.password">{{ errors.password }}</div>
      </div>

      <!-- 注册链接 -->
      <div class="register-link">
        <router-link to="/register">没有账号，注册</router-link>
      </div>

      <!-- 登录按钮 -->
      <button class="login-btn" @click="handleLogin" :disabled="isSubmitting">
        {{ isSubmitting ? '登录中...' : '登录' }}
      </button>
    </div>
  </PageBackground>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import PageBackground from "@/components/PageBackground.vue";
import {  storage} from '@/utils'
import { auth } from '@/api'
import { useUserStore } from '@/stores'
//导入ElMessage（用了ElMessage.success/error，得导入）
import { ElMessage } from 'element-plus'
//导入md5（你用了md5加密sign，得导入）
import md5 from 'js-md5'
import {validateLoginForm} from "@/utils/validate.js";
import {decryptData} from "@/utils/request.js";
// 创建路由实例
const router = useRouter();
const route = useRoute();

// 表单数据（保留原有account/password字段，映射文档的student_number）
const form = reactive({
  student_number: '',
  password: ''
});

const errors = reactive({
  student_number: '',
  password: ''
});

const isSubmitting = ref(false);

// 字段失焦校验
const handleFieldBlur = (field) => {
  if (field === 'student_number' && !form.student_number) {
    errors.student_number = '学号不能为空';
  } else if (field === 'password' && !form.password) {
    errors.password = '密码不能为空';
  } else {
    errors[field] = '';
  }
};

// 表单验证

const validateForm = () => {
  const validateErrors = validateLoginForm(form);
  Object.assign(errors, validateErrors);
  return Object.keys(validateErrors).length === 0;
};

// 登录处理逻辑（核心修改）
const handleLogin = async () => {
  if (!validateForm()) {
    return;
  }
  isSubmitting.value = true;
  try {
    // 5. 按文档构造请求参数：student_number + password + sign（Mock暂填固定sign）
    const APP_SECRET = import.meta.env.VITE_APP_API_SECRET || 'default_secret';
    const signStr = `${form.student_number}${form.password}${APP_SECRET}`;
    const requestParams = {
      student_number: form.student_number,
      password: form.password,
      sign: md5(signStr)
    };
    console.log('请求参数：', requestParams); // 新增打印
    console.log('开始调用userLogin'); // 新增打印
    // 6. 调用登录接口
    const axiosRes = await auth.userLogin(requestParams);// 重命名为axiosRes，区分层级
    // 关键修复：取Axios响应的data字段（才是接口原始返回数据）
    const res = axiosRes;

    if (!res) {
      ElMessage.error('登录失败：无响应数据');
      isSubmitting.value = false; // 必须重置提交状态，否则按钮一直加载
      return;
    }
    const userInfo = (res.data && res.data[0]) || {};

    // 7. 对齐文档返回码：res_code=0000为成功
    switch (res.res_code) {
      case "0000":
        const userStore = useUserStore();
        // 完整赋值userStore的userInfo（包含所有权限字段）
        userStore.userInfo = {
          // 保留Store原有默认值，避免覆盖必要字段
          ...userStore.userInfo,
          // 用解密后的完整用户信息覆盖（包含department_name/is_super_admin等）
          ...userInfo,
          // 确保登录状态正确
          department_name: userInfo.department_name || '',
          isLogin: !!userInfo.token
        };

        // 校验token
        if (!userInfo.token) {
          ElMessage.error('登录失败：未获取到用户令牌');
          isSubmitting.value = false;
          return;
        } else {
          console.log('要存的token：', userInfo.token);
          storage.setToken(userInfo.token);
          console.log('存完后本地的token：', localStorage.getItem('pann_financial_token'));
        }

        // 存储完整信息，且角色用Store计算好的
        storage.setToken(userInfo.token);
        storage.setRole(userStore.userRole);
        storage.setUserInfo(userStore.userInfo); // 存完整的userInfo

        ElMessage.success('登录成功！');
        const redirect = route.query.redirect || '/home';
        await router.push(redirect);
        break;
      case "0004":
        ElMessage.error(res.res_msg || '学号或密码错误');
        break;
      case "0003":
        ElMessage.error(res.res_msg || '权限不足，无法登录');
        break;
      default:
        ElMessage.error(res.res_msg || '登录失败，请重试');
    }
  } catch (error) {
    console.error('登录请求异常：', error);
    ElMessage.error('网络异常，请检查网络连接');
  }finally {
    isSubmitting.value = false;
  }
};
</script>
<style scoped>
/* 完全保留你原有的所有样式，一行未改 */
.login-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  width: 350px;
  padding: 40px 30px;
  box-sizing: border-box;
}

.system-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 40px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.input-field {
  width: 100%;
  height: 45px;
  padding: 0 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
  background-color: rgba(255, 255, 255, 0.8);
}

.input-field::placeholder {
  color: #999;
}

.error-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}

.register-link {
  margin-bottom: 25px;
  text-align: center;
}

.register-link a {
  color: #666;
  font-size: 14px;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 45px;
  background-color: #A59EB2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #8d839a;
}

.login-btn:disabled {
  background-color: #c0b9c7;
  cursor: not-allowed;
}
</style>