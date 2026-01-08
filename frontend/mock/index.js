
import Mock from 'mockjs';
import CryptoJS from 'crypto-js';



// ==================== 通用工具函数 ====================
// 生成随机ID（6-10位数字）
const getRandomId = (len = 8) => Mock.Random.string('number', len);
// 生成随机时间（格式：2025-12-10 11:00:00）
const getRandomDatetime = () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss');
// 生成随机日期（格式：2025-12-10）
const getRandomDate = () => Mock.Random.date('yyyy-MM-dd');
// 生成随机签名（模拟MD5签名）
const getRandomSign = () => Mock.Random.string('hex', 32);
// 生成随机JWT令牌
const getRandomToken = () => Mock.Random.string('alphaNumeric', 36);
// 随机部门
const randomDepartments = ['编辑部', '新闻部', '运营部'];
// 随机稿件类型
const randomArticleTypes = ['新闻', '校对', '编辑', '策划', '采访', '排版'];
// 随机反馈状态
const randomFeedbackStatus = ['pending', 'replied'];
// 🌟 关键：配置和前端完全一致的AES密钥/向量（必须和前端CONFIG里的一模一样）
const CONFIG = {
    signSecretKey: 'Pann2025Key',
    encryptKeyStr: 'Pann2025EncKey00', // 补2个0，凑16位
    encryptIVStr: 'Pann2025IV123450',  // 补1个0，凑16位
};


// 🌟 替换：AES-CBC加密（和前端解密逻辑100%对齐）
const encryptData = (data) => {
    if (!data) return '';
    try {
        const encryptKey = CryptoJS.enc.Utf8.parse(CONFIG.encryptKeyStr);
        const encryptIV = CryptoJS.enc.Utf8.parse(CONFIG.encryptIVStr);
        const jsonStr = JSON.stringify(data);
        const encrypted = CryptoJS.AES.encrypt(
            jsonStr,
            encryptKey,
            { iv: encryptIV, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 }
        );
        const encryptedStr = encrypted.toString();
        return encrypted.toString();
    } catch (e) {
        console.error('加密失败：', e);
        console.error('【加密函数】加密失败：', e);
        return '';
    }
};

// 🌟 替换：AES-CBC解密（可选，Mock内部校验用，和前端解密逻辑一致）
const decryptData = (encryptStr) => {
    if (!encryptStr) return {};
    try {
        const encryptKey = CryptoJS.enc.Utf8.parse(CONFIG.encryptKeyStr);
        const encryptIV = CryptoJS.enc.Utf8.parse(CONFIG.encryptIVStr);
        const decrypted = CryptoJS.AES.decrypt(encryptStr, encryptKey, {
            iv: encryptIV, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7
        });
        const plainText = decrypted.toString(CryptoJS.enc.Utf8);
        return JSON.parse(plainText || '{}');
    } catch (e) {
        console.error('Mock解密失败：', e);
        return {};
    }
};
Mock.setup({
    timeout: '200-600',
    responseType: 'json'
});
// ==================== 1. 用户注册接口（2.5.1） ====================
Mock.mock(/\/auth\/register\/xxx/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体，如需校验参数可注释以下逻辑
    // const params = JSON.parse(options.body || '{}');
    // 如需模拟参数校验，可从加密字符串解密（可选）
    const params = decryptData(options.body || '');
    // 模拟必填参数校验
    if (!params.student_number || !params.real_name || !params.password || !params.email) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }
    // 模拟白名单校验失败
    if (params.student_number?.startsWith('2022')) {
        return { res_code: '0006', res_msg: '白名单校验失败！', data: null };
    }

    // 成功返回：data改为加密字符串
    const successData = {
        user_id: getRandomId(),
        student_number: params.student_number,
        real_name: params.real_name,
        email: params.email
    };
    return {
        res_code: '0000',
        res_msg: '注册成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});
// ==================== 2. 用户登录接口（2.5.2） ====================
Mock.mock(/\auth\/login\/xxx/, 'post', (options) => {
    // 1. 解析前端传入的登录参数（解密请求体）
    const loginParams = decryptData(options.body || '');
    const inputStudentNumber = loginParams.student_number; // 获取前端输入的学号

    // 2. 配置固定5个人员身份（1系统管理员+3部门管理员+1普通用户）
    const fixedUsers = {
        // 系统管理员-张三
        "100000000001": {
            user_id: "1",
            student_number: "100000000001",
            real_name: "张三",
            email: "zhangsan@test.com",
            token: getRandomToken(), // 复用工具函数生成唯一token
            permissions: ['read', 'write', 'manage', 'super'],
            is_super_admin: true,
            department_id: null,
            department_name: null,
            admin_id: "1"
        },
        // 新闻部管理员-李四
        "100000000002": {
            user_id: "2",
            student_number: "100000000002",
            real_name: "李四",
            email: "lisi@test.com",
            token: getRandomToken(),
            permissions: ['read', 'write', 'manage'],
            is_super_admin: false,
            department_id: "1",
            department_name: "新闻部",
            admin_id: "2"
        },
        // 编辑部管理员-王五
        "100000000003": {
            user_id: "3",
            student_number: "100000000003",
            real_name: "王五",
            email: "wangwu@test.com",
            token: getRandomToken(),
            permissions: ['read', 'write', 'manage'],
            is_super_admin: false,
            department_id: "2",
            department_name: "编辑部",
            admin_id: "3"
        },
        // 运营部管理员-赵六
        "100000000004": {
            user_id: "4",
            student_number: "100000000004",
            real_name: "赵六",
            email: "zhaoliu@test.com",
            token: getRandomToken(),
            permissions: ['read', 'write', 'manage'],
            is_super_admin: false,
            department_id: "3",
            department_name: "运营部",
            admin_id: "4"
        },
        // 普通用户-孙七
        "200000000001": {
            user_id: "10",
            student_number: "200000000001",
            real_name: "孙七",
            email: "sunqi@test.com",
            token: getRandomToken(),
            permissions: ['read'],
            is_super_admin: false,
            department_id: null,
            department_name: null,
            admin_id: null
        }
    };

    // 3. 匹配学号，判断是否存在该固定用户
    const targetUser = fixedUsers[inputStudentNumber];
    if (!targetUser) {
        // 匹配失败：返回0004错误码
        return {
            res_code: "0004",
            res_msg: "学号或密码错误",
            data: null
        };
    }

    // 4. 匹配成功：构造用户数据（保持数组格式，与后端一致）
    const userData = [targetUser];
    // 5. 加密用户信息（复用原有加密逻辑）
    const encryptedData = encryptData(userData);
    // 6. 返回登录成功数据
    return {
        res_code: "0000",
        res_msg: "登录成功",
        data: encryptedData // data字段是加密后的用户信息数组
    };
});
// ==================== 3. 修改密码接口（2.5.3） ====================
Mock.mock(/\/auth\/change-password\/\d+/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    // 模拟旧密码错误
    if (params.old_password !== 'e10adc3949ba59abbe56e057f20f883e') {
        return { res_code: '0001', res_msg: '旧密码错误，修改失败', data: null };
    }

    // 成功返回：data为null无需加密
    return { res_code: '0000', res_msg: '密码修改成功', data: null };
});

// ==================== 4. 退出登录接口（2.5.4） ====================
Mock.mock(/\/auth\/logout\/xxx/, 'post', () => {
    return { res_code: '0000', res_msg: '退出登录成功', data: null };
});
// ==================== 5. 获取个人信息接口（2.5.5） ====================
Mock.mock(/\/user\/profile\/xxx/, 'get', (options) => {
    // 1. 复用固定用户配置（不变）
    const fixedUsers = {
        "100000000001": {
            user_id: "1",
            student_number: "100000000001",
            real_name: "张三",
            email: "zhangsan@test.com",
            role: "super_admin",
            permissions: ['read', 'write', 'manage', 'super']
        },
        "100000000002": {
            user_id: "2",
            student_number: "100000000002",
            real_name: "李四",
            email: "lisi@test.com",
            role: "dept_admin",
            permissions: ['read', 'write', 'manage']
        },
        "100000000003": {
            user_id: "3",
            student_number: "100000000003",
            real_name: "王五",
            email: "wangwu@test.com",
            role: "dept_admin",
            permissions: ['read', 'write', 'manage']
        },
        "100000000004": {
            user_id: "4",
            student_number: "100000000004",
            real_name: "赵六",
            email: "zhaoliu@test.com",
            role: "dept_admin",
            permissions: ['read', 'write', 'manage']
        },
        "200000000001": {
            user_id: "10",
            student_number: "200000000001",
            real_name: "孙七",
            email: "sunqi@test.com",
            role: "normal_user",
            permissions: ['read']
        }
    };

    // ========== 修改1：从前端请求参数中解析token/学号（核心修复） ==========
    // options是Mock接收的前端请求对象，解析url中的参数（如?token=xxx&sign=xxx）
    const urlParams = new URLSearchParams(options.url.split('?')[1] || '');
    // 读取前端传的学号（优先），无则兜底孙七
    const loginStudentNumber = urlParams.get('student_number') || "200000000001";

    // 直接匹配用户（无需映射表）
    const targetUser = fixedUsers[loginStudentNumber] || fixedUsers["200000000001"];

    // 3. 构造个人信息数据（不变）
    const successData = {
        user_id: targetUser.user_id,
        student_number: targetUser.student_number,
        real_name: targetUser.real_name,
        email: targetUser.email,
        role: targetUser.role,
        permissions: targetUser.permissions
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});
// ==================== 6. 更新用户信息接口（2.5.6） ====================
Mock.mock(/\/user\/profile\/\d+/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    // 成功返回更新后信息
    const successData = {
        user_id: getRandomId(),
        student_number: Mock.Random.string('number', 8),
        real_name: params.real_name || Mock.Random.cname(),
        email: params.email || Mock.Random.email()
    };

    return {
        res_code: '0000',
        res_msg: '更新成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 7. 获取用户列表接口（2.5.7） ====================
Mock.mock(/\/admin\/users\/xxx/, 'get', (options) => {
    // 解析URL参数（非请求体，无需加密）
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10, keyword = '' } = params;

    // 生成模拟用户列表
    const mockList = Mock.mock({
        [`list|${size}`]: [{
            user_id: () => getRandomId(),
            student_number: () => Mock.Random.string('number', 8),
            real_name: () => Mock.Random.cname(),
            email: () => Mock.Random.email(),
            role: () => ['user', 'dept_admin'][Math.floor(Math.random() * 2)]
        }]
    });

    const successData = {
        list: mockList.list,
        total: 50,
        page: Number(page),
        size: Number(size)
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 8. 修改用户角色接口（2.5.8） ====================
Mock.mock(/\/admin\/users\/role\/xxx/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    // 模拟缺少用户ID
    if (!params.user_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少用户ID', data: null };
    }

    const successData = {
        user_id: params.user_id,
        real_name: Mock.Random.cname(),
        is_super_admin: params.is_super_admin || false,
        department_id: params.department_id || getRandomId()
    };

    return {
        res_code: '0000',
        res_msg: '角色修改成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 9. 查询个人稿费接口（2.5.9） ====================
Mock.mock(/\/api\/v1\/royalty\/personal/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            record_id: () => getRandomId(6),
            article_title: () => Mock.Random.ctitle(5, 15),
            article_type: () => randomArticleTypes[Math.floor(Math.random() * randomArticleTypes.length)],
            fee_amount: () => Mock.Random.float(50, 500, 2, 2),
            statistical_month: () => Mock.Random.date('yyyy-MM'),
            department_id: () => Math.floor(Math.random() * 3) + 1,
            created_at: () => getRandomDatetime(),
            updated_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 30,
        list: mockList.list,
        page: Number(page),
        size: Number(size)
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 10. 查询部门稿费接口（2.5.10） ====================
Mock.mock(/\/api\/v1\/admin\/royalty\/department/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            record_id: () => getRandomId(6),
            user_ids: () => [getRandomId(6), getRandomId(6)],
            real_names: () => [Mock.Random.cname(), Mock.Random.cname()],
            student_numbers: () => [Mock.Random.string('number', 8), Mock.Random.string('number', 8)],
            article_title: () => Mock.Random.ctitle(5, 15),
            article_type: () => randomArticleTypes[Math.floor(Math.random() * randomArticleTypes.length)],
            fee_amount: () => Mock.Random.float(50, 500, 2, 2),
            statistical_month: () => Mock.Random.date('yyyy-MM'),
            department_id: () => Math.floor(Math.random() * 3) + 1,
            created_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 45,
        list: mockList.list,
        page: Number(page),
        size: Number(size)
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 11. 查询全部稿费接口（2.5.11） ====================
Mock.mock(/\/api\/v1\/admin\/royalty\/all/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            record_id: () => getRandomId(6),
            user_ids: () => [getRandomId(6)],
            real_names: () => [Mock.Random.cname()],
            student_numbers: () => [Mock.Random.string('number', 8)],
            article_title: () => Mock.Random.ctitle(5, 15),
            article_type: () => randomArticleTypes[Math.floor(Math.random() * randomArticleTypes.length)],
            fee_amount: () => Mock.Random.float(50, 500, 2, 2),
            statistical_month: () => Mock.Random.date('yyyy-MM'),
            department_id: () => Math.floor(Math.random() * 3) + 1,
            created_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 120,
        list: mockList.list,
        page: Number(page),
        size: Number(size)
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 12. 添加稿费记录接口（2.5.12） ====================
Mock.mock(/\/api\/v1\/admin\/royalty/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    // 模拟必填参数校验
    if (!params.user_id || !params.article_title || !params.fee_amount) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }

    const successData = { record_id: getRandomId(6) };
    return {
        res_code: '0000',
        res_msg: '添加成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 13. 修改稿费记录接口（2.5.13） ====================
Mock.mock(/\/api\/v1\/admin\/royalty\/\d+/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.article_title || !params.fee_amount) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }

    const successData = { updated_at: getRandomDatetime() };
    return {
        res_code: '0000',
        res_msg: '更新成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 14. 删除稿费记录接口（2.5.14） ====================
Mock.mock(/\/api\/v1\/admin\/royalty\/\d+/, 'delete', () => {
    const successData = { deleted_at: getRandomDatetime() };
    return {
        res_code: '0000',
        res_msg: '删除成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 15. 导出稿费记录接口（2.5.15） ====================
Mock.mock(/\/api\/v1\/admin\/royalty\/export/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { statistical_month, format = 'Excel' } = params;

    if (!statistical_month) {
        return { res_code: '0002', res_msg: '参数错误！缺少统计月份', data: null };
    }

    const successData = {
        fileUrl: `https://example.com/fee_${statistical_month}.${format.toLowerCase()}`,
        exportTime: getRandomDatetime(),
        recordCount: Mock.Random.integer(20, 100)
    };

    return {
        res_code: '0000',
        res_msg: '导出成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 16. 添加代领记录接口（2.5.16） ====================
Mock.mock(/\/api\/v1\/admin\/proxy/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.fee_record_id || !params.proxy_user_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }

    const successData = { proxy_id: getRandomId(4) };
    return {
        res_code: '0000',
        res_msg: '设置成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 17. 查询代领记录接口（2.5.17） ====================
Mock.mock(/\/api\/v1\/admin\/proxy\/list/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            proxy_id: () => getRandomId(4),
            fee_record_id: () => getRandomId(6),
            original_user_id: () => getRandomId(6),
            proxy_user_id: () => getRandomId(6),
            article_title: () => Mock.Random.ctitle(5, 15),
            fee_amount: () => Mock.Random.float(50, 500, 2, 2),
            proxy_month: () => Mock.Random.date('yyyy-MM'),
            created_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 28,
        list: mockList.list,
        page: Number(page),
        size: Number(size)
    };

    return {
        res_code: '0000',
        res_msg: 'success',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 18. 修改代领记录接口（2.5.18） ====================
Mock.mock(/\/api\/v1\/admin\/proxy\/\d+/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (params.fee_amount && isNaN(Number(params.fee_amount))) {
        return { res_code: '0002', res_msg: '参数错误：fee_amount 格式不正确', data: null };
    }

    const successData = {
        proxy_id: params.proxy_id || getRandomId(4),
        updated_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '修改成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 19. 撤销代领记录接口（2.5.19） ====================
Mock.mock(/\/api\/v1\/admin\/proxy\/\d+/, 'delete', () => {
    // 随机返回成功或权限不足
    if (Math.random() > 0.7) {
        return { res_code: '0003', res_msg: '权限不足：无法删除代领记录', data: null };
    }

    const successData = {
        proxy_id: getRandomId(4),
        deleted_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '撤销成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 20. 提交问题反馈接口（2.5.20） ====================
Mock.mock(/\/api\/v1\/feedback/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.user_id || !params.content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }

    const successData = {
        feedback_id: `f${getRandomId(6)}`,
        created_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '提交成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 21. 用户查询反馈接口（2.5.21） ====================
Mock.mock(/\/api\/v1\/feedback\/my/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            feedback_id: () => `f${getRandomId(6)}`,
            content: () => Mock.Random.cparagraph(1, 3),
            reply_content: () => Math.random() > 0.5 ? Mock.Random.cparagraph(1, 2) : null,
            created_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 5,
        page: Number(page),
        size: Number(size),
        list: mockList.list
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 22. 查询反馈详情接口（2.5.22） ====================
Mock.mock(/\/api\/v1\/feedback\/f\d+/, 'get', () => {
    const successData = {
        feedbackInfo: {
            feedback_id: `f${getRandomId(6)}`,
            user_id: getRandomId(),
            content: Mock.Random.cparagraph(1, 3),
            reply_content: Math.random() > 0.5 ? Mock.Random.cparagraph(1, 2) : null,
            replied_at: Math.random() > 0.5 ? getRandomDatetime() : null,
            created_at: getRandomDatetime(),
            updated_at: getRandomDatetime()
        }
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 23. 查询待处理反馈接口（2.5.23） ====================
Mock.mock(/\/api\/v1\/admin\/feedback\/pending/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    // 随机返回权限不足
    if (Math.random() > 0.8) {
        return { res_code: '0003', res_msg: '权限不足！', data: null };
    }

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            feedback_id: () => `f${getRandomId(6)}`,
            user_id: () => getRandomId(),
            student_number: () => Mock.Random.string('number', 8),
            real_name: () => Mock.Random.cname(),
            content: () => Mock.Random.cparagraph(1, 3),
            reply_content: null,
            replied_at: null,
            created_at: () => getRandomDatetime(),
            department_name: () => randomDepartments[Math.floor(Math.random() * randomDepartments.length)]
        }]
    });

    const successData = {
        total: 3,
        page: Number(page),
        size: Number(size),
        list: mockList.list
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 24. 查询所有反馈接口（2.5.24） ====================
Mock.mock(/\/api\/v1\/admin\/feedback\/all/, 'get', (options) => {
    // 解析URL参数
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;

    if (Math.random() > 0.8) {
        return { res_code: '0003', res_msg: '权限不足！', data: null };
    }

    const mockList = Mock.mock({
        [`list|${size}`]: [{
            feedback_id: () => `f${getRandomId(6)}`,
            user_id: () => getRandomId(),
            student_number: () => Mock.Random.string('number', 8),
            real_name: () => Mock.Random.cname(),
            content: () => Mock.Random.cparagraph(1, 3),
            status: () => randomFeedbackStatus[Math.floor(Math.random() * randomFeedbackStatus.length)],
            replied_at: () => Math.random() > 0.5 ? getRandomDatetime() : null,
            created_at: () => getRandomDatetime(),
            updated_at: () => getRandomDatetime()
        }]
    });

    const successData = {
        total: 10,
        page: Number(page),
        size: Number(size),
        list: mockList.list
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 25. 回复用户反馈接口（2.5.25） ====================
Mock.mock(/\/api\/v1\/admin\/feedback\/f\d+\/reply/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.reply_content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }

    const successData = {
        message: '回复成功',
        replied_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '回复成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 26. 更新反馈状态接口（2.5.26） ====================
Mock.mock(/\/api\/v1\/admin\/feedback\/f\d+\/status/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.status) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }

    const successData = {
        message: '状态更新成功',
        updated_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '状态更新成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 27. 获取公告详情接口（2.5.27） ====================
Mock.mock(/\/api\/v1\/announcements\/a\d+/, 'get', () => {
    const successData = {
        announcementInfo: {
            announcement_id: `a${getRandomId(6)}`,
            title: Mock.Random.ctitle(5, 15),
            content: Mock.Random.cparagraph(2, 5),
            publisher_id: getRandomId(),
            published_at: getRandomDatetime(),
            created_at: getRandomDatetime(),
            updated_at: getRandomDatetime()
        }
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 28. 管理员发布公告接口（2.5.28） ====================
Mock.mock(/\/api\/v1\/admin\/announcements/, 'post', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.title || !params.content || !params.publisher_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }

    // 随机返回权限不足
    if (Math.random() > 0.8) {
        return { res_code: '0003', res_msg: '权限不足！', data: null };
    }

    const successData = {
        announcement_id: `a${getRandomId(6)}`,
        message: '发布成功',
        published_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '发布成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 29. 管理员修改公告接口（2.5.29） ====================
Mock.mock(/\/api\/v1\/admin\/announcements\/\d+/, 'put', (options) => {
    // 🌟 修改：不再解析加密的请求体
    // const params = JSON.parse(options.body || '{}');
    const params = decryptData(options.body || '');

    if (!params.title || !params.content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }

    const successData = {
        message: '修改成功',
        updated_at: getRandomDatetime()
    };

    return {
        res_code: '0000',
        res_msg: '修改成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 30. 管理员删除公告接口（2.5.30） ====================
Mock.mock(/\/api\/v1\/admin\/announcements\/\d+/, 'delete', () => {
    if (Math.random() > 0.8) {
        return { res_code: '0003', res_msg: '权限不足！', data: null };
    }

    const successData = { message: '删除成功' };
    return {
        res_code: '0000',
        res_msg: '删除成功',
        data: encryptData(successData) // 🌟 加密返回
    };
});

// ==================== 公告相关 - 获取所有公告（2.5.31） ====================
Mock.mock(/\/api\/v1\/announcements/, 'get', (options) => {
    // 1. 解析请求参数（从URL中提取query参数）
    const urlParams = new URLSearchParams(options.url.split('?')[1] || '');
    const params = {
        page: parseInt(urlParams.get('page')) || 1,
        size: parseInt(urlParams.get('size')) || 10,
        publisher_id: urlParams.get('publisher_id') || '',
        keyword: urlParams.get('keyword') || '',
        order_by: urlParams.get('order_by') || 'published_at',
        sort: urlParams.get('sort') || 'desc'
    };

    // 2. 参数校验（模拟接口的参数错误返回）
    if (!params.page || params.page < 1) {
        return {
            res_code: '0002',
            res_msg: '参数错误！页码格式不正确或小于1',
            data: null
        };
    }
    if (!params.size || params.size < 1 || params.size > 50) {

        return {
            res_code: '0002',
            res_msg: '参数错误！每页数量必须在1-50之间',
            data: null
        };
    }
// 3. 生成模拟公告数据（总共有15条模拟数据，用于分页）
    const totalAnnouncements = 15;
    const mockAnnouncements = [];
    for (let i = 0; i < totalAnnouncements; i++) {
        const publishTime = getRandomDatetime(); // 复用工具函数生成发布时间
        // 生成更新时间（确保晚于发布时间）
        const updateTime = Mock.Random.datetime('yyyy-MM-dd HH:mm:ss', new Date(publishTime));
        mockAnnouncements.push({
            announcement_id: getRandomId(), // 复用工具函数生成公告ID
            title: Mock.Random.ctitle(5, 20), // 改用Mock.Random，解决未定义问题
            content: Mock.Random.cparagraph(1, 3), // 改用Mock.Random
            publisher_id: Mock.Random.integer(100, 999), // 改用Mock.Random
            published_at: publishTime, // 发布时间
            created_at: publishTime, // 创建时间（与发布时间一致）
            updated_at: updateTime // 更新时间（晚于发布时间）
        });
    }

    // 4. 处理筛选（关键词模糊匹配标题）
    let filteredAnnouncements = mockAnnouncements;
    if (params.keyword) {
        filteredAnnouncements = filteredAnnouncements.filter(item =>
            item.title.includes(params.keyword)
        );
    }
    // 可选：按发布者ID筛选
    if (params.publisher_id) {
        filteredAnnouncements = filteredAnnouncements.filter(item =>
            item.publisher_id.toString() === params.publisher_id.toString()
        );
    }

    // 5. 处理排序（默认按published_at降序）
    filteredAnnouncements.sort((a, b) => {
        const sortFieldA = a[params.order_by];
        const sortFieldB = b[params.order_by];
        if (params.sort === 'desc') {
            return new Date(sortFieldB) - new Date(sortFieldA); // 降序
        } else {
            return new Date(sortFieldA) - new Date(sortFieldB); // 升序
        }
    });

    // 6. 处理分页
    const total = filteredAnnouncements.length;
    const start = (params.page - 1) * params.size;
    const end = start + params.size;
    const paginatedAnnouncements = filteredAnnouncements.slice(start, end);

    // 7. 构造返回数据（模拟接口成功响应）
    const successData = {
        total: total, // 符合条件的总记录数
        page: params.page, // 当前页码
        size: params.size, // 每页数量
        list: paginatedAnnouncements // 分页后的公告列表
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData) // 加密返回（复用项目加密逻辑）
    };


});

// ==================== Mock全局配置 ====================
Mock.setup({ timeout: '200-500' }); // 模拟网络延迟
console.log('✅ PANN财务系统 - 所有接口Mock服务已启动（仅开发环境）');