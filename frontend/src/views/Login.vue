<template>
  <PageBackground>
    <!-- 登录卡片容器 -->
    <div class="login-container">
      <h1 class="system-title">PANN财务管理系统</h1>

      <!-- 账号输入框 -->
      <div class="form-group">
        <input
            type="text"
            placeholder="账号"
            class="input-field"
            v-model="form.account"
            @blur="handleFieldBlur('account')"
        >
        <!-- 账号错误提示 -->
        <div class="error-tip" v-if="errors.account">{{ errors.account }}</div>
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
import { useRouter } from 'vue-router';
import PageBackground from "@/components/PageBackground.vue";
// 导入token操作工具
import { setToken, setRole } from '@/utils/auth.js';
import { loginApi } from "@/api/user.js";
import { useUserStore } from '@/stores/userStore.js'
// 加密库（生成sign签名）
import md5 from 'js-md5';
// Element Plus提示（替换alert，和其他页面统一）
import { ElMessage } from 'element-plus';
// 导入通用登录表单校验函数（核心修改）
import { validateLoginForm } from '@/utils/validate.js';

// 创建路由实例
const router = useRouter();
const route = useRoute();
// 表单数据
const form = reactive({
  account: '',
  password: ''
});

// 错误提示
const errors = reactive({
  account: '',
  password: ''
});

// 提交状态
const isSubmitting = ref(false);

// 后端签名秘钥（替换为真实秘钥）
const SECRET_KEY = 'pannfmis2025';

// 单个字段失焦校验（复用通用校验函数）
const handleFieldBlur = (field) => {
  // 调用通用校验函数，获取所有错误
  const validateErrors = validateLoginForm(form);
  // 只更新当前失焦字段的错误
  errors[field] = validateErrors[field] || '';
};

// 表单整体验证（复用通用校验函数）
const validateForm = () => {
  // 调用通用校验函数
  const validateErrors = validateLoginForm(form);
  // 同步所有错误到页面提示
  Object.assign(errors, validateErrors);
  // 判断是否有错误（无错误则返回true）
  return Object.keys(validateErrors).length === 0;
};

// 登录处理逻辑
const handleLogin = async () => {
  // 第一步：先执行表单验证，不通过直接返回
  if (!validateForm()) {
    return;
  }

  isSubmitting.value = true;
  try {
    /**************************
     * 方式1：真实对接后端接口（默认启用）
     * 测试完后端后，注释这部分，打开方式2的模拟逻辑
     *************************
        // 1. 生成sign签名（按接口文档规则：学号+密码+秘钥）
    const signStr = form.account + form.password + SECRET_KEY;
    const sign = md5(signStr);

    // 2. 构造请求参数（对齐接口文档字段名）
    const loginParams = {
      student_number: form.account, // 文档要求的字段名
      password: form.password,      // 原始密码（request.js会统一加密）
      sign: sign                    // 文档必填的sign字段
    };

    // 3. 调用真实后端登录接口
    const res = await loginApi(loginParams);

    // 4. 处理后端返回结果
    if (res.res_code === '0000') {
      // 存储token和角色
      setToken(res.data.token);
      // 用工具方法存储角色（不再硬编码key）
      setRole(res.data.role || 'user');
      ElMessage.success('登录成功！');
      console.log('真实令牌已存储：', res.data.token);
      console.log('当前用户角色：', res.data.role);
      // 跳转到个人信息页（替换为/home也可以）
     const redirect = route.query.redirect || '/home'; // 优先跳来源页，无则跳首页
     router.push(redirect);
    } else {
      // 按返回码提示错误
      switch (res.res_code) {
        case '0002':
          ElMessage.error('参数错误！缺少必填字段或格式不正确');
          break;
        case '0004':
          ElMessage.error('学号或密码错误，登录失败！');
          break;
        case '0008':
          ElMessage.error('系统内部错误，请稍后重试');
          break;
        default:
          ElMessage.error(res.res_msg || '登录失败，请联系管理员');
      }
    }
*/
    /**************************真实登录与模拟登录分界线****************************/
    /* 方式2：模拟登录逻辑（测试后端后启用）
     * 步骤：注释方式1，取消注释方式2
     **************************/

    // 模拟接口请求（1秒后返回）
    const res = await new Promise(resolve => {
      setTimeout(() => {
        // 👉 可修改role值测试不同角色：admin/operator/visitor/user
        const mockRole = 'user';
        resolve({
          res_code: '0000', // 对齐文档返回码
          data: {
            token: 'pann_token_' + Date.now(), // 随机令牌
            role: mockRole,
            student_number: form.account,
            real_name: '测试用户'
          },
          res_msg: '登录成功'
        });
      }, 1000);
    });

    // 存储令牌 + 角色
    setToken(res.data.token);
    setRole(res.data.role || 'user');
    ElMessage.success(`登录成功！当前角色：${res.data.role}`);
    console.log('模拟令牌已存储：', res.data.token);
    console.log('当前模拟角色：', res.data.role);
    // 跳转到个人信息页
    const redirect = route.query.redirect || '/home'; // 优先跳来源页，无则跳首页
    router.push(redirect);
    /**********************以上都是模拟登录内容***********************************/

  } catch (error) {
    console.error('登录请求异常：', error);
    // 区分网络错误和接口错误
    if (error.response?.data?.res_msg) {
      ElMessage.error(`登录失败：${error.response.data.res_msg}`);
    } else {
      ElMessage.error('网络异常，请检查网络后重试');
    }
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* 样式部分保持不变 */
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