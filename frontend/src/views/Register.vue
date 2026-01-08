<template>
  <PageBackground>
    <!-- 注册表单容器 -->
    <div class="register-container">
      <h1 class="system-title">PANN财务管理系统</h1>

      <!-- 用户名输入框 + 错误提示 -->
      <div class="form-group">
        <input
            type="text"
            placeholder="请输入真实姓名"
            class="input-field"
            v-model="form.username"
            @blur="validateUsername"
            :disabled="isSubmitting"
        >
        <div class="error-tip" v-if="errors.username">{{ errors.username }}</div>
      </div>

      <!-- 账号输入框 + 错误提示 -->
      <div class="form-group">
        <input
            type="text"
            placeholder="请输入学号"
            class="input-field"
            v-model="form.account"
            @blur="validateAccount"
            :disabled="isSubmitting"
        >
        <div class="error-tip" v-if="errors.account">{{errors.account  }}</div>
      </div>
      <!-- 邮箱输入框 + 错误提示 -->
      <div class="form-group">
        <input
            type="text"
            placeholder="请输入邮箱"
            ref="emailInput"
            class="input-field"
            v-model="form.email"
            @blur="validateEmail"
            :disabled="isSubmitting"
        >
        <div class="error-tip" v-if="errors.email">{{ errors.email }}</div>
      </div>

      <!-- 密码输入框 + 错误提示 -->
      <div class="form-group">
        <input
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            class="input-field"
            v-model="form.password"
            @blur="validatePassword"
            :disabled="isSubmitting"
            style="padding-right: 30px; /* 给小眼睛留空间，不遮挡密码 */"
        >
        <span
            @click="showPassword = !showPassword"
            style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #999; user-select: none;"
        >
          <!-- 可替换为任意字符/图标，这里用简单符号，无需额外资源 -->
          {{ showPassword ? '&#128065;' : '👁️‍🗨️' }}
        </span>
        <div class="error-tip" v-if="errors.password">{{ errors.password }}</div>
      </div>

      <!-- 确认密码输入框 + 错误提示 -->
      <div class="form-group">
        <input
            type="password"
            placeholder="请确认密码"
            class="input-field"
            v-model="form.confirmPwd"
            @blur="validateConfirmPwd"
            :disabled="isSubmitting"
        >
        <span
            @click="showPassword = !showPassword"
            style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #999; user-select: none;"
        >
          <!-- 可替换为任意字符/图标，这里用简单符号，无需额外资源 -->
          {{ showPassword ? '&#128065;' : '👁️‍🗨️' }}
        </span>
        <div class="error-tip" v-if="errors.confirmPwd">{{ errors.confirmPwd }}</div>
      </div>

      <!-- 登录链接 -->
      <div class="login-link">
        <router-link to="/login">已有账号，登录</router-link>
      </div>

      <!-- 注册按钮（添加禁用状态） -->
      <button
          class="register-btn"
          @click="handleRegister"
          :disabled="isSubmitting"
      >
        {{ isSubmitting ? '注册中...' : '注册' }}
      </button>
    </div>
  </PageBackground>
</template>
<script setup>
import { ref, reactive } from 'vue';
import PageBackground from "@/components/PageBackground.vue";
import { useRouter } from 'vue-router'
import { auth } from '@/api'
import md5 from 'js-md5'
import { ElMessage } from 'element-plus'
import {validateRegisterForm} from "@/utils/validate.js";
import { encryptData, decryptData, generateSign } from '@/utils/request.js'
const router = useRouter()
const emailInput = ref(null);
// 新增：控制密码显示/隐藏的变量（默认隐藏，核心补充）
const showPassword = ref(false);
const form = reactive({
  username: '',
  account: '',
  email: '',
  password: '',
  confirmPwd: ''
});

const errors = reactive({
  username: '',
  account: '',
  email: '',
  password: '',
  confirmPwd: ''
});

const isSubmitting = ref(false);

// 1. 验证用户名（非空 + 仅中文校验）
const validateUsername = () => {
  // 先清空原有错误提示
  errors.username = '';
  // 非空校验（
  if (!form.username.trim()) {
    errors.username = '真实姓名不能为空';
  } else {
    // 中文校验（仅在非空的情况下执行）
    // 纯中文正则表达式：^ 开头，$ 结尾，[\u4e00-\u9fa5]+ 匹配至少一个中文
    const chineseOnlyReg = /^[\u4e00-\u9fa5]+$/;
    // 校验输入的用户名是否符合纯中文规则
    if (!chineseOnlyReg.test(form.username.trim())) {
      errors.username = '姓名仅支持输入中文';
    } else {
      // 非空且是纯中文，清空错误提示
      errors.username = '';
    }
  }
};

// 2. 验证学号（非空 + 12位长度 + 纯数字校验）
const validateAccount = () => {
  errors.account = '';
  const account = form.account.trim();

  //  非空校验
  if (!account) {
    errors.account = '学号不能为空';
    return; // 非空校验失败，直接返回，不执行后续校验
  }

  //  12位长度校验
  if (account.length !== 12) {
    errors.account = '学号必须为12位';
    return; // 长度校验失败，直接返回
  }

  // 纯数字校验
  const numberReg = /^\d{12}$/; // 匹配恰好12位数字
  if (!numberReg.test(account)) {
    errors.account = '学号必须为12位纯数字';
  } else {
    errors.account = '';
  }
};

// 3. 验证邮箱（非空+格式）
const validateEmail = () => {
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!form.email.trim()) {
    errors.email = '邮箱不能为空';
  } else if (!emailReg.test(form.email.trim())) {
    errors.email = '请输入有效的邮箱地址（如：xxx@qq.com）';
  } else {
    errors.email = '';
  }
};

// 4. 验证密码（非空+8-16位）
const validatePassword = () => {
  if (!form.password.trim()) {
    errors.password = '密码不能为空';
  } else if (form.password.length < 8 || form.password.length > 16) {
    errors.password = '密码需8-16位';
  } else {
    errors.password = '';
  }
};

// 5. 验证确认密码
const validateConfirmPwd = () => {
  if (!form.confirmPwd.trim()) {
    errors.confirmPwd = '请确认密码';
  } else if (form.confirmPwd !== form.password) {
    errors.confirmPwd = '两次密码输入不一致';
  } else {
    errors.confirmPwd = '';
  }
};

// 6. 整体表单验证
// （复用validate.js的校验函数）
const validateForm = () => {
  const validateErrors = validateRegisterForm(form);
  Object.assign(errors, validateErrors); // 同步错误到组件
  return Object.keys(validateErrors).length === 0; // 无错误返回true
};

// 7. 注册处理逻辑
const handleRegister = async () => {
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;

  try {
    // 步骤1：构造业务参数（仅接口要求的字段）
    const businessParams = {
      student_number: form.account,
      real_name: form.username,
      email: form.email,
      password: form.password
    };

    // 步骤2：直接调用已定义的generateSign生成签名（无多余拼接）
    const sign = generateSign(businessParams);
    // 步骤3：整合最终参数（业务字段 + sign）
    const reqParams = {
      ...businessParams,
      sign: sign
    };

   /* // 步骤4：直接调用已定义的encryptData加密参数（无多余逻辑）
    const encryptedData = encryptData(reqParams);
*///统一在request加密
    // 步骤6：调用注册接口（传用户ID + 加密后的数据）
    const axiosRes = await auth.userRegister(reqParams);
    const res = axiosRes;
    console.log('【注册接口返回结果】', res);
    // 处理返回结果（对齐接口文档返回码）
    switch (res.res_code) {
      case '0000':
        ElMessage.success(res.res_msg || '注册成功！即将跳转到登录页');
        Object.keys(form).forEach(key => form[key] = '');
        Object.keys(errors).forEach(key => errors[key] = '');
        setTimeout(() => router.push('/login'), 1500);
        break;
      case '0005':
        ElMessage.error(res.res_msg || '该邮箱已被注册，请更换邮箱');
        form.password = '';
        form.confirmPwd = '';
        errors.confirmPwd = '';
        emailInput.value?.focus();
        break;
        // 其他返回码处理...
      default:
        ElMessage.error(res.res_msg || '注册失败，请联系管理员');
        form.password = '';
        form.confirmPwd = '';
    }
  } catch (error) {
    console.error('【注册请求失败 - 完整错误信息】', {
      message: error.message,
      response: error.response ? {
        status: error.response.status,
        data: error.response.data
      } : '无响应数据',
      request: error.request ? '请求已发送但无响应' : '请求未发送'
    });
    form.password = '';
    form.confirmPwd = '';
    ElMessage.error(error.response?.data?.res_msg || '网络异常，注册失败！');
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* 原有样式 + 新增错误提示样式 */
.register-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  width: 350px;
  padding: 40px 30px;
}

.system-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 40px;
  font-weight: 600;
}

.form-group {
  position: relative;
  margin-bottom: 20px;
  text-align: left; /* 错误提示左对齐 */
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
  color: #666;
}

/* 错误提示样式（和登录页统一） */
.error-tip {
  color: #f56c6c; /* 红色提示 */
  font-size: 12px;
  margin-top: 5px;
}

.login-link {
  margin-bottom: 25px;
  text-align: center;
}

.login-link a {
  color: #666;
  font-size: 14px;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

.register-btn {
  width: 100%;
  height: 45px;
  background-color: #9286a0;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover {
  background-color: #7d738a;
}

/* 禁用状态样式 */
.register-btn:disabled {
  background-color: #c0b9c7;
  cursor: not-allowed;
}
</style>