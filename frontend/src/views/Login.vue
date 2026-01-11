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
      <div class="form-group" style="position: relative;">
        <input
            :type="showPassword ? 'text' : 'password'"
            placeholder="密码"
            class="input-field"
            v-model="form.password"
            @blur="handleFieldBlur('password')"
            style="padding-right: 30px; /* 给小眼睛留空间，不遮挡密码 */"
        >
        <!-- 自定义小眼睛按钮（固定存在，不受失焦影响，样式极简） -->
        <span
            @click="showPassword = !showPassword"
            style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #999; user-select: none;"
        >
    <!-- 可替换为任意字符/图标，这里用简单符号，无需额外资源 -->
     {{ showPassword ? '&#128065;' : '👁️‍🗨️' }}
  </span>
        <!-- 密码错误提示   -->
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
import {getToken} from "@/utils/auth.js";
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
// 新增：控制密码显示/隐藏的变量（默认隐藏，核心补充）
const showPassword = ref(false);
const isSubmitting = ref(false);

// 字段失焦校验
const handleFieldBlur = (field) => {
  // 学号校验：非空 + 12位纯数字
  if (field === 'student_number') {
    // 先获取去除首尾空格的学号（避免空格干扰长度判断）
    const studentNumber = form.student_number?.trim() || '';
    // 1. 非空校验
    if (!studentNumber) {
      errors.student_number = '学号不能为空';
    }
    // 2. 12位长度 + 纯数字校验（使用正则 ^\d{12}$ 精准匹配12位数字）
    else if (!/^\d{12}$/.test(studentNumber)) {
      errors.student_number = '学号必须为12位纯数字';
    }
    // 3. 校验通过，清空错误提示
    else {
      errors.student_number = '';
    }
  }
  // 密码校验：保留原有非空逻辑
  else if (field === 'password' && !form.password) {
    errors.password = '密码不能为空';
  }
  // 其他字段：清空对应错误提示
  else {
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
    // 6. 调用登录接口
    const axiosRes = await auth.userLogin(requestParams);// 重命名为axiosRes，区分层级
    // 关键修复：取Axios响应的data字段（才是接口原始返回数据）
    const res = axiosRes;
    console.log('后台返回的完整登录数据：', res);
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

//  使用 Store 标准方法写入用户信息
        userStore.setUserInfo({
          ...userInfo,
          token: userInfo.token,
          isLogin: true
        });

//  标记登录验证完成（非常关键）
        userStore.confirmLogin();

//  同步角色到本地缓存（此时 role 已经稳定）
        userStore.updateLocalStorageRole();


        // 校验token
        if (!userInfo.token) {
          ElMessage.error('登录失败：未获取到用户令牌');
          isSubmitting.value = false;
          return;
        } else {
          console.log('要存的token：', userInfo.token);
          storage.setToken(userInfo.token);
          console.log('存完后本地的token：', getToken());
        }

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
/* 隐藏 Chrome/Edge 等 WebKit 内核浏览器的原生密码显隐按钮 */
input[type="password"]::-webkit-reveal-button,
input[type="password"]::-webkit-credentials-auto-fill-button {
  display: none !important;
  visibility: hidden !important;
  pointer-events: none;
  width: 0;
  height: 0;
}

/* 隐藏 Firefox 原生密码图标 */
input[type="password"]::-moz-password-input-revealer {
  display: none !important;
  -moz-appearance: none !important;
}

/* 隐藏 Edge/IE 原生密码图标 */
input::-ms-reveal {
  display: none !important;
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