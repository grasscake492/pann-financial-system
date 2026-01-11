import Mock from 'mockjs';
import CryptoJS from 'crypto-js';

// ==================== 新增：全局状态管理 ====================
let currentLoginUser = null; // 存储当前登录用户

// ==================== 通用工具函数 ====================
// 使用固定的随机种子，确保每次生成相同的数据
Mock.Random.extend({
    seed: 'pann2026' // 设置随机种子
});

// 生成固定ID（基于索引）
const getFixedId = (len = 8, seed = '') => {
    if (seed) {
        let id = '';
        for (let i = 0; i < len; i++) {
            const charCode = (seed.charCodeAt(i % seed.length) * (i + 1)) % 10;
            id += charCode;
        }
        return id;
    }
    return Mock.Random.string('number', len);
};

// 生成2026-01-01到2026-01-11之间的固定时间
const getFixedDatetime = (dayOffset = 0) => {
    const baseDate = new Date(2026, 0, 1); // 2026-01-01
    baseDate.setDate(baseDate.getDate() + (dayOffset % 11));

    const pad = (n) => n.toString().padStart(2, '0');
    const hours = pad(dayOffset % 24);
    const minutes = pad((dayOffset * 7) % 60);
    const seconds = pad((dayOffset * 13) % 60);

    return `${baseDate.getFullYear()}-${pad(baseDate.getMonth() + 1)}-${pad(baseDate.getDate())} ${hours}:${minutes}:${seconds}`;
};

// 生成2026-01-01到2026-01-11之间的固定日期
const getFixedDate = (dayOffset = 0) => {
    const baseDate = new Date(2026, 0, 1);
    baseDate.setDate(baseDate.getDate() + (dayOffset % 11));

    const pad = (n) => n.toString().padStart(2, '0');
    return `${baseDate.getFullYear()}-${pad(baseDate.getMonth() + 1)}-${pad(baseDate.getDate())}`;
};

// 生成固定月份（2026-01）
const getFixedMonth = () => {
    return '2026-01';
};

// 生成固定金额（基于索引）
const getFixedAmount = (index) => {
    const baseAmounts = [150.00, 200.00, 250.00, 300.00, 180.00, 220.00, 280.00, 320.00, 190.00, 260.00];
    return baseAmounts[index % baseAmounts.length];
};

// 固定部门
const fixedDepartments = ['新闻部', '编辑部', '运营部'];
// 固定稿件类型
const fixedArticleTypes = ['新闻', '校对', '编辑', '策划', '采访', '排版'];
// 固定反馈状态
const fixedFeedbackStatus = ['pending', 'replied'];

// 固定文章标题列表
const fixedTitles = [
    '校园新闻稿费统计',
    '学术论文校对费用',
    '编辑部策划稿费',
    '采访报道费用结算',
    '校园活动策划稿费',
    '新闻排版费用统计',
    '专题报道稿费计算',
    '学术期刊编辑费用'
];

// 🌟 关键：配置和前端完全一致的AES密钥/向量
const CONFIG = {
    signSecretKey: 'Pann2025Key',
    encryptKeyStr: 'Pann2025EncKey00',
    encryptIVStr: 'Pann2025IV123450',
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
        return encrypted.toString();
    } catch (e) {
        console.error('加密失败：', e);
        return '';
    }
};

// 🌟 替换：AES-CBC解密
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

// ==================== 固定用户配置（添加密码字段） ====================
let fixedUsers = {
    "100000000001": {
        user_id: "1",
        student_number: "100000000001",
        real_name: "张三",
        email: "zhangsan@test.com",
        password: "zhangsan123", // 添加密码字段
        token: "fixed_token_super_admin_001",
        permissions: ['read', 'write', 'manage', 'super'],
        is_super_admin: true,
        department_id: null,
        department_name: null,
        admin_id: "1",
        role: "super_admin"
    },
    "100000000002": {
        user_id: "2",
        student_number: "100000000002",
        real_name: "李四",
        email: "lisi@test.com",
        password: "lisi123", // 添加密码字段
        token: "fixed_token_news_admin_002",
        permissions: ['read', 'write', 'manage'],
        is_super_admin: false,
        department_id: "1",
        department_name: "新闻部",
        admin_id: "2",
        role: "dept_admin"
    },
    "100000000003": {
        user_id: "3",
        student_number: "100000000003",
        real_name: "王五",
        email: "wangwu@test.com",
        password: "wangwu123", // 添加密码字段
        token: "fixed_token_edit_admin_003",
        permissions: ['read', 'write', 'manage'],
        is_super_admin: false,
        department_id: "2",
        department_name: "编辑部",
        admin_id: "3",
        role: "dept_admin"
    },
    "100000000004": {
        user_id: "4",
        student_number: "100000000004",
        real_name: "赵六",
        email: "zhaoliu@test.com",
        password: "zhaoliu123", // 添加密码字段
        token: "fixed_token_operate_admin_004",
        permissions: ['read', 'write', 'manage'],
        is_super_admin: false,
        department_id: "3",
        department_name: "运营部",
        admin_id: "4",
        role: "dept_admin"
    },
    "200000000001": {
        user_id: "10",
        student_number: "200000000001",
        real_name: "孙七",
        email: "sunqi@test.com",
        password: "sunqi123", // 添加密码字段
        token: "fixed_token_normal_user_010",
        permissions: ['read'],
        is_super_admin: false,
        department_id: null,
        department_name: null,
        admin_id: null,
        role: "normal_user"
    }
};

// 获取固定用户列表
const getFixedUserList = () => {
    return Object.values(fixedUsers);
};

// 获取固定用户（基于索引）
const getFixedUserByIndex = (index) => {
    const userKeys = Object.keys(fixedUsers);
    const key = userKeys[index % userKeys.length];
    return fixedUsers[key];
};

// ==================== 新增：根据用户ID获取用户 ====================
const getUserById = (userId) => {
    return Object.values(fixedUsers).find(user => user.user_id === userId) || null;
};

// ==================== 新增：更新相关数据中的用户信息 ====================
const updateRelatedDataUserInfo = (oldStudentNumber, newStudentNumber, newRealName, newEmail) => {
    // 更新稿费记录中的用户信息
    FIXED_DATA.royaltyRecords.forEach(record => {
        const userIndex = record.student_numbers.indexOf(oldStudentNumber);
        if (userIndex !== -1) {
            record.student_numbers[userIndex] = newStudentNumber;
            record.real_names[userIndex] = newRealName;
        }
    });

    // 更新反馈记录中的用户信息
    FIXED_DATA.feedbackRecords.forEach(record => {
        if (record.student_number === oldStudentNumber) {
            record.student_number = newStudentNumber;
            record.real_name = newRealName;
            // 如果邮箱也更新了，可以在这里添加
        }
    });

    // 更新代理记录中的用户信息（如果需要）
    // FIXED_DATA.proxyRecords 通常不包含学号信息，但如果有需要可以更新
};

// ==================== 固定数据生成器 ====================
// 生成固定稿费记录
const generateFixedRoyaltyRecords = (count = 10, offset = 0) => {
    const records = [];
    for (let i = 0; i < count; i++) {
        const user = getFixedUserByIndex(i);
        const deptIndex = user.department_id ? parseInt(user.department_id) - 1 : i % 3;

        records.push({
            record_id: `royalty_${1000 + offset + i}`,
            article_title: fixedTitles[i % fixedTitles.length],
            article_type: fixedArticleTypes[i % fixedArticleTypes.length],
            fee_amount: getFixedAmount(i),
            statistical_month: getFixedMonth(),
            department_id: user.department_id || ((i % 3) + 1).toString(),
            user_ids: [user.user_id],
            real_names: [user.real_name],
            student_numbers: [user.student_number],
            created_at: getFixedDatetime(i),
            updated_at: getFixedDatetime(i + 1)
        });
    }
    return records;
};

// 生成固定反馈记录
const generateFixedFeedbackRecords = (count = 5, offset = 0) => {
    const records = [];
    for (let i = 0; i < count; i++) {
        const user = getFixedUserByIndex(i);
        const status = i < 3 ? 'pending' : 'replied';

        records.push({
            feedback_id: `f${100 + offset + i}`,
            user_id: user.user_id,
            student_number: user.student_number,
            real_name: user.real_name,
            content: `这是第${i + 1}条反馈内容，由${user.real_name}提交`,
            reply_content: status === 'replied' ? `已收到您的反馈，我们会尽快处理。` : null,
            status: status,
            replied_at: status === 'replied' ? getFixedDatetime(i + 2) : null,
            created_at: getFixedDatetime(i),
            updated_at: getFixedDatetime(i + 1),
            department_name: user.department_name || '无部门'
        });
    }
    return records;
};

// 生成固定公告记录
const generateFixedAnnouncements = (count = 15, offset = 0) => {
    const announcements = [];
    for (let i = 0; i < count; i++) {
        const user = getFixedUserByIndex(i % 5);
        const publishTime = getFixedDatetime(i);
        const updateTime = getFixedDatetime(i + 1);

        announcements.push({
            announcement_id: `a${100 + offset + i}`,
            title: `公告标题${i + 1}`,
            content: `这是第${i + 1}条公告内容，发布时间为${publishTime}。`,
            publisher_id: user.user_id,
            published_at: publishTime,
            created_at: publishTime,
            updated_at: updateTime
        });
    }
    return announcements;
};

// 生成固定代领记录
const generateFixedProxyRecords = (count = 8, offset = 0) => {
    const records = [];
    for (let i = 0; i < count; i++) {
        const originalUser = getFixedUserByIndex(i % 5);
        const proxyUser = getFixedUserByIndex((i + 1) % 5);

        records.push({
            proxy_id: `proxy_${200 + offset + i}`,
            fee_record_id: `royalty_${1000 + i}`,
            original_user_id: originalUser.user_id,
            proxy_user_id: proxyUser.user_id,
            article_title: fixedTitles[i % fixedTitles.length],
            fee_amount: getFixedAmount(i),
            proxy_month: getFixedMonth(),
            created_at: getFixedDatetime(i)
        });
    }
    return records;
};

// 预先生成固定数据
const FIXED_DATA = {
    royaltyRecords: generateFixedRoyaltyRecords(30, 0),
    feedbackRecords: generateFixedFeedbackRecords(10, 0),
    announcements: generateFixedAnnouncements(15, 0),
    proxyRecords: generateFixedProxyRecords(8, 0)
};

Mock.setup({
    timeout: '200-600',
    responseType: 'json'
});

// ==================== 接口定义 ====================
// 1. 用户注册接口（2.5.1）
Mock.mock(/\/auth\/register\/xxx/, 'post', (options) => {
    const params = decryptData(options.body || '');
    if (!params.student_number || !params.real_name || !params.password || !params.email) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }
    if (params.student_number?.startsWith('2022')) {
        return { res_code: '0006', res_msg: '白名单校验失败！', data: null };
    }

    const newUserId = Object.keys(fixedUsers).length + 1;
    const successData = {
        user_id: newUserId.toString(),
        student_number: params.student_number,
        real_name: params.real_name,
        email: params.email
    };
    return {
        res_code: '0000',
        res_msg: '注册成功',
        data: encryptData(successData)
    };
});

// 2. 用户登录接口（2.5.2）- 新增：密码验证
Mock.mock(/\auth\/login\/xxx/, 'post', (options) => {
    const loginParams = decryptData(options.body || '');
    const inputStudentNumber = loginParams.student_number;
    const inputPassword = loginParams.password;

    if (!inputPassword) {
        return {
            res_code: "0002",
            res_msg: "请输入密码",
            data: null
        };
    }

    // 通过学号查找用户
    const targetUser = Object.values(fixedUsers).find(user => user.student_number === inputStudentNumber);

    if (!targetUser) {
        return {
            res_code: "0004",
            res_msg: "学号或密码错误",
            data: null
        };
    }

    // 验证密码
    if (targetUser.password !== inputPassword) {
        return {
            res_code: "0004",
            res_msg: "学号或密码错误",
            data: null
        };
    }

    // 设置当前登录用户
    currentLoginUser = targetUser;

    const userData = [targetUser];
    return {
        res_code: "0000",
        res_msg: "登录成功",
        data: encryptData(userData)
    };
});

// 3. 修改密码接口（2.5.3）- 修改：验证旧密码并更新密码
Mock.mock(/\/auth\/change-password\/\d+/, 'put', (options) => {
    const params = decryptData(options.body || '');
    const { old_password, new_password } = params;

    // 获取当前登录用户
    if (!currentLoginUser) {
        return { res_code: '0004', res_msg: '用户未登录', data: null };
    }

    // 验证旧密码
    if (currentLoginUser.password !== old_password) {
        return { res_code: '0004', res_msg: '旧密码错误', data: null };
    }

    // 更新密码
    currentLoginUser.password = new_password;

    // 更新 fixedUsers 中的密码
    Object.keys(fixedUsers).forEach(key => {
        if (fixedUsers[key].student_number === currentLoginUser.student_number) {
            fixedUsers[key].password = new_password;
        }
    });

    return { res_code: '0000', res_msg: '密码修改成功', data: null };
});

// 4. 退出登录接口（2.5.4）- 新增：清除当前登录用户
Mock.mock(/\/auth\/logout\/xxx/, 'post', () => {
    currentLoginUser = null;
    return { res_code: '0000', res_msg: '退出登录成功', data: null };
});

// 5. 获取个人信息接口（2.5.5）- 修改：返回当前登录用户信息（已更新）
Mock.mock(/\/user\/profile\/xxx/, 'get', (options) => {
    // 获取当前登录用户，如果没有则使用默认用户
    const user = currentLoginUser || fixedUsers["200000000001"];

    const successData = {
        user_id: user.user_id,
        student_number: user.student_number,
        real_name: user.real_name,
        email: user.email,
        role: user.role,
        permissions: user.permissions
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 6. 更新用户信息接口（2.5.6）- 修改：更新当前登录用户信息和fixedUsers
Mock.mock(/\/user\/profile\/\d+/, 'put', (options) => {
    const params = decryptData(options.body || '');

    // 获取当前登录用户
    if (!currentLoginUser) {
        currentLoginUser = fixedUsers["200000000001"];
    }

    const oldStudentNumber = currentLoginUser.student_number;
    const oldRealName = currentLoginUser.real_name;
    const oldEmail = currentLoginUser.email;

    // 更新当前登录用户的信息
    if (params.student_number) {
        currentLoginUser.student_number = params.student_number;
    }
    if (params.real_name) {
        currentLoginUser.real_name = params.real_name;
    }
    if (params.email) {
        currentLoginUser.email = params.email;
    }

    // 更新 fixedUsers 中的对应信息
    // 首先找到旧的键（学号）
    const oldKey = Object.keys(fixedUsers).find(key =>
        fixedUsers[key].user_id === currentLoginUser.user_id
    );

    if (oldKey) {
        // 删除旧的条目
        delete fixedUsers[oldKey];

        // 使用新的学号作为键，添加更新后的用户信息
        fixedUsers[currentLoginUser.student_number] = {
            ...currentLoginUser
        };

        // 更新相关数据中的用户信息
        updateRelatedDataUserInfo(
            oldStudentNumber,
            currentLoginUser.student_number,
            currentLoginUser.real_name,
            currentLoginUser.email
        );
    }

    const successData = {
        user_id: currentLoginUser.user_id,
        student_number: currentLoginUser.student_number,
        real_name: currentLoginUser.real_name,
        email: currentLoginUser.email
    };

    return {
        res_code: '0000',
        res_msg: '更新成功',
        data: encryptData(successData)
    };
});

// 7. 获取用户列表接口（2.5.7）- 保持原有逻辑
Mock.mock(/\/admin\/users\/xxx/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10, keyword = '' } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    // 从固定用户中筛选
    let userList = getFixedUserList();
    if (keyword) {
        userList = userList.filter(user =>
            user.real_name.includes(keyword) ||
            user.student_number.includes(keyword) ||
            user.email.includes(keyword)
        );
    }

    const successData = {
        list: userList.slice(start, end),
        total: userList.length,
        page: Number(page),
        size: Number(size)
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 8. 修改用户角色接口（2.5.8）- 保持原有逻辑
Mock.mock(/\/admin\/users\/role\/xxx/, 'put', (options) => {
    const params = decryptData(options.body || '');
    if (!params.user_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少用户ID', data: null };
    }

    const userKey = Object.keys(fixedUsers).find(key =>
        fixedUsers[key].user_id === params.user_id
    );
    const targetUser = userKey ? fixedUsers[userKey] : fixedUsers["100000000001"];

    const successData = {
        user_id: targetUser.user_id,
        real_name: targetUser.real_name,
        is_super_admin: params.is_super_admin || false,
        department_id: params.department_id || targetUser.department_id
    };
    return {
        res_code: '0000',
        res_msg: '角色修改成功',
        data: encryptData(successData)
    };
});

// 9. 查询个人稿费接口（2.5.9）- 修改：根据当前登录用户筛选
Mock.mock(/\/api\/v1\/royalty\/personal/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    // 获取当前登录用户
    const user = currentLoginUser || fixedUsers["200000000001"];

    // 获取用户稿费记录（根据当前登录用户的学号筛选）
    const userRecords = FIXED_DATA.royaltyRecords.filter(record =>
        record.student_numbers.includes(user.student_number)
    ).slice(start, end);

    const successData = {
        total: userRecords.length,
        list: userRecords,
        page: Number(page),
        size: Number(size)
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 10. 查询部门稿费接口（2.5.10）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty\/department/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    // 筛选部门稿费记录
    const deptRecords = FIXED_DATA.royaltyRecords.filter(record =>
        record.department_id === "1"
    ).slice(start, end);

    const successData = {
        total: deptRecords.length,
        list: deptRecords,
        page: Number(page),
        size: Number(size)
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 11. 查询全部稿费接口（2.5.11）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty\/all/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    const successData = {
        total: FIXED_DATA.royaltyRecords.length,
        list: FIXED_DATA.royaltyRecords.slice(start, end),
        page: Number(page),
        size: Number(size)
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 12. 添加稿费记录接口（2.5.12）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty/, 'post', (options) => {
    const params = decryptData(options.body || '');
    if (!params.user_id || !params.article_title || !params.fee_amount) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }
    const successData = {
        record_id: `royalty_${1000 + FIXED_DATA.royaltyRecords.length}`,
        created_at: getFixedDatetime(FIXED_DATA.royaltyRecords.length)
    };
    return {
        res_code: '0000',
        res_msg: '添加成功',
        data: encryptData(successData)
    };
});

// 13. 修改稿费记录接口（2.5.13）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty\/\d+/, 'put', (options) => {
    const params = decryptData(options.body || '');
    if (!params.article_title || !params.fee_amount) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }
    const successData = {
        updated_at: getFixedDatetime(FIXED_DATA.royaltyRecords.length + 1)
    };
    return {
        res_code: '0000',
        res_msg: '更新成功',
        data: encryptData(successData)
    };
});

// 14. 删除稿费记录接口（2.5.14）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty\/\d+/, 'delete', () => {
    const successData = {
        deleted_at: getFixedDatetime(FIXED_DATA.royaltyRecords.length + 2)
    };
    return {
        res_code: '0000',
        res_msg: '删除成功',
        data: encryptData(successData)
    };
});

// 15. 导出稿费记录接口（2.5.15）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/royalty\/export/, 'get', (options) => {
    const params = options.params || {};
    const { statistical_month, format = 'Excel' } = params;

    const successData = {
        fileUrl: `https://example.com/fee_${statistical_month || '2026-01'}.${format.toLowerCase()}`,
        exportTime: getFixedDatetime(5),
        recordCount: FIXED_DATA.royaltyRecords.length
    };
    return {
        res_code: '0000',
        res_msg: '导出成功',
        data: encryptData(successData)
    };
});

// 16. 添加代领记录接口（2.5.16）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/proxy/, 'post', (options) => {
    const params = decryptData(options.body || '');
    if (!params.fee_record_id || !params.proxy_user_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }
    const successData = {
        proxy_id: `proxy_${200 + FIXED_DATA.proxyRecords.length}`
    };
    return {
        res_code: '0000',
        res_msg: '设置成功',
        data: encryptData(successData)
    };
});

// 17. 查询代领记录接口（2.5.17）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/proxy\/list/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    const successData = {
        total: FIXED_DATA.proxyRecords.length,
        list: FIXED_DATA.proxyRecords.slice(start, end),
        page: Number(page),
        size: Number(size)
    };
    return {
        res_code: '0000',
        res_msg: 'success',
        data: encryptData(successData)
    };
});

// 18. 修改代领记录接口（2.5.18）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/proxy\/\d+/, 'put', (options) => {
    const params = decryptData(options.body || '');
    if (params.fee_amount && isNaN(Number(params.fee_amount))) {
        return { res_code: '0002', res_msg: '参数错误：fee_amount 格式不正确', data: null };
    }
    const successData = {
        proxy_id: params.proxy_id || `proxy_${200 + FIXED_DATA.proxyRecords.length}`,
        updated_at: getFixedDatetime(FIXED_DATA.proxyRecords.length + 1)
    };
    return {
        res_code: '0000',
        res_msg: '修改成功',
        data: encryptData(successData)
    };
});

// 19. 撤销代领记录接口（2.5.19）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/proxy\/\d+/, 'delete', () => {
    const successData = {
        proxy_id: `proxy_${200 + FIXED_DATA.proxyRecords.length}`,
        deleted_at: getFixedDatetime(FIXED_DATA.proxyRecords.length + 2)
    };
    return {
        res_code: '0000',
        res_msg: '撤销成功',
        data: encryptData(successData)
    };
});

// 20. 提交问题反馈接口（2.5.20）- 修改：使用当前登录用户信息
Mock.mock(/\/api\/v1\/feedback/, 'post', (options) => {
    const params = decryptData(options.body || '');

    // 获取当前登录用户
    const user = currentLoginUser || fixedUsers["200000000001"];

    if (!params.content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }

    const newFeedback = {
        feedback_id: `f${100 + FIXED_DATA.feedbackRecords.length}`,
        user_id: user.user_id,
        student_number: user.student_number,
        real_name: user.real_name,
        content: params.content,
        reply_content: null,
        status: 'pending',
        replied_at: null,
        created_at: getFixedDatetime(FIXED_DATA.feedbackRecords.length),
        updated_at: getFixedDatetime(FIXED_DATA.feedbackRecords.length + 1),
        department_name: user.department_name || '无部门'
    };

    FIXED_DATA.feedbackRecords.push(newFeedback);

    const successData = {
        feedback_id: newFeedback.feedback_id,
        created_at: newFeedback.created_at
    };
    return {
        res_code: '0000',
        res_msg: '提交成功',
        data: encryptData(successData)
    };
});

// 21. 用户查询反馈接口（2.5.21）- 修改：根据当前登录用户筛选
Mock.mock(/\/api\/v1\/feedback\/my/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    // 获取当前登录用户
    const user = currentLoginUser || fixedUsers["200000000001"];

    // 查询当前用户的反馈
    const userFeedbacks = FIXED_DATA.feedbackRecords.filter(fb =>
        fb.student_number === user.student_number
    ).slice(start, end);

    const successData = {
        total: userFeedbacks.length,
        page: Number(page),
        size: Number(size),
        list: userFeedbacks
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 22. 查询反馈详情接口（2.5.22）- 保持原有逻辑
Mock.mock(/\/api\/v1\/feedback\/f\d+/, 'get', () => {
    const feedback = FIXED_DATA.feedbackRecords[0];
    const successData = {
        feedbackInfo: feedback
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 23. 查询待处理反馈接口（2.5.23）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/feedback\/pending/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    const pendingFeedbacks = FIXED_DATA.feedbackRecords.filter(fb =>
        fb.status === 'pending'
    ).slice(start, end);

    const successData = {
        total: pendingFeedbacks.length,
        page: Number(page),
        size: Number(size),
        list: pendingFeedbacks
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 24. 查询所有反馈接口（2.5.24）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/feedback\/all/, 'get', (options) => {
    const urlParams = options.url.split('?')[1] || '';
    const params = {};
    urlParams.split('&').forEach(item => {
        const [key, val] = item.split('=');
        if (key) params[key] = val;
    });
    const { page = 1, size = 10 } = params;
    const start = (page - 1) * size;
    const end = start + parseInt(size);

    const successData = {
        total: FIXED_DATA.feedbackRecords.length,
        page: Number(page),
        size: Number(size),
        list: FIXED_DATA.feedbackRecords.slice(start, end)
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 25. 回复用户反馈接口（2.5.25）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/feedback\/f\d+\/reply/, 'post', (options) => {
    const params = decryptData(options.body || '');
    if (!params.reply_content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }
    const successData = {
        message: '回复成功',
        replied_at: getFixedDatetime(FIXED_DATA.feedbackRecords.length + 1)
    };
    return {
        res_code: '0000',
        res_msg: '回复成功',
        data: encryptData(successData)
    };
});

// 26. 更新反馈状态接口（2.5.26）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/feedback\/f\d+\/status/, 'put', (options) => {
    const params = decryptData(options.body || '');
    if (!params.status) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }
    const successData = {
        message: '状态更新成功',
        updated_at: getFixedDatetime(FIXED_DATA.feedbackRecords.length + 2)
    };
    return {
        res_code: '0000',
        res_msg: '状态更新成功',
        data: encryptData(successData)
    };
});

// 27. 获取公告详情接口（2.5.27）- 保持原有逻辑
Mock.mock(/\/api\/v1\/announcements\/a\d+/, 'get', () => {
    const announcement = FIXED_DATA.announcements[0];
    const successData = {
        announcementInfo: announcement
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// 28. 管理员发布公告接口（2.5.28）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/announcements/, 'post', (options) => {
    const params = decryptData(options.body || '');
    if (!params.title || !params.content || !params.publisher_id) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段', data: null };
    }

    const successData = {
        announcement_id: `a${100 + FIXED_DATA.announcements.length}`,
        message: '发布成功',
        published_at: getFixedDatetime(FIXED_DATA.announcements.length)
    };
    return {
        res_code: '0000',
        res_msg: '发布成功',
        data: encryptData(successData)
    };
});

// 29. 管理员修改公告接口（2.5.29）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/announcements\/\d+/, 'put', (options) => {
    const params = decryptData(options.body || '');
    if (!params.title || !params.content) {
        return { res_code: '0002', res_msg: '参数错误！缺少必填字段或格式不正确', data: null };
    }
    const successData = {
        message: '修改成功',
        updated_at: getFixedDatetime(FIXED_DATA.announcements.length + 1)
    };
    return {
        res_code: '0000',
        res_msg: '修改成功',
        data: encryptData(successData)
    };
});

// 30. 管理员删除公告接口（2.5.30）- 保持原有逻辑
Mock.mock(/\/api\/v1\/admin\/announcements\/\d+/, 'delete', () => {
    const successData = {
        message: '删除成功',
        deleted_at: getFixedDatetime(FIXED_DATA.announcements.length + 2)
    };
    return {
        res_code: '0000',
        res_msg: '删除成功',
        data: encryptData(successData)
    };
});

// 31. 获取所有公告接口（2.5.31）- 修改：首页添加当前用户信息（已更新）
Mock.mock(/\/api\/v1\/announcements/, 'get', (options) => {
    const urlParams = new URLSearchParams(options.url.split('?')[1] || '');
    const params = {
        page: parseInt(urlParams.get('page')) || 1,
        size: parseInt(urlParams.get('size')) || 10,
        publisher_id: urlParams.get('publisher_id') || '',
        keyword: urlParams.get('keyword') || '',
        order_by: urlParams.get('order_by') || 'published_at',
        sort: urlParams.get('sort') || 'desc'
    };

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

    let filteredAnnouncements = FIXED_DATA.announcements;
    if (params.keyword) {
        filteredAnnouncements = filteredAnnouncements.filter(item =>
            item.title.includes(params.keyword)
        );
    }
    if (params.publisher_id) {
        filteredAnnouncements = filteredAnnouncements.filter(item =>
            item.publisher_id.toString() === params.publisher_id.toString()
        );
    }

    filteredAnnouncements.sort((a, b) => {
        const sortFieldA = a[params.order_by];
        const sortFieldB = b[params.order_by];
        if (params.sort === 'desc') {
            return new Date(sortFieldB) - new Date(sortFieldA);
        } else {
            return new Date(sortFieldA) - new Date(sortFieldB);
        }
    });

    const total = filteredAnnouncements.length;
    const start = (params.page - 1) * params.size;
    const end = start + params.size;
    const paginatedAnnouncements = filteredAnnouncements.slice(start, end);

    // 获取当前登录用户信息，用于首页显示（已更新）
    const user = currentLoginUser || fixedUsers["200000000001"];

    const successData = {
        total: total,
        page: params.page,
        size: params.size,
        list: paginatedAnnouncements,
        // 新增：首页需要的当前用户信息
        current_user: {
            name: user.real_name,
            role: user.role,
            department_name: user.department_name
        }
    };
    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

// ==================== 新增：首页接口（可选） ====================
Mock.mock(/\/api\/v1\/home\/dashboard/, 'get', (options) => {
    // 获取当前登录用户（已更新）
    const user = currentLoginUser || fixedUsers["200000000001"];

    // 计算简单的统计数据
    const totalRoyalty = FIXED_DATA.royaltyRecords
        .filter(record => record.student_numbers.includes(user.student_number))
        .reduce((sum, item) => sum + item.fee_amount, 0);

    const userFeedbacks = FIXED_DATA.feedbackRecords.filter(fb => fb.student_number === user.student_number);
    const pendingFeedback = userFeedbacks.filter(fb => fb.status === 'pending').length;

    const successData = {
        user_info: {
            real_name: user.real_name,
            student_number: user.student_number,
            role: user.role,
            department_name: user.department_name || '无部门',
            email: user.email
        },
        statistics: {
            total_royalty: totalRoyalty,
            total_records: FIXED_DATA.royaltyRecords.filter(r => r.student_numbers.includes(user.student_number)).length,
            pending_feedback: pendingFeedback,
            recent_month: getFixedMonth()
        },
        system_time: getFixedDatetime(0)
    };

    return {
        res_code: '0000',
        res_msg: '查询成功',
        data: encryptData(successData)
    };
});

console.log('✅ PANN财务系统 - 固定数据Mock服务已启动（仅开发环境）');
console.log('📅 数据时间范围：2026-01-01 至 2026-01-11');
console.log('👥 固定用户：5个（1系统管理员+3部门管理员+1普通用户）');
console.log('🔐 登录密码验证已启用');
console.log('📋 用户对应密码：');
console.log('   学号 100000000001 密码：zhangsan123 (系统管理员)');
console.log('   学号 100000000002 密码：lisi123 (新闻部管理员)');
console.log('   学号 100000000003 密码：wangwu123 (编辑部管理员)');
console.log('   学号 100000000004 密码：zhaoliu123 (运营部管理员)');
console.log('   学号 200000000001 密码：sunqi123 (普通用户)');
console.log('🔗 个人信息页面和首页数据已关联到当前登录用户');
console.log('🔄 个人信息修改功能已启用，修改后会同步到首页和账户页面');
console.log('🎓 学号修改功能已启用，修改后相关数据中的用户信息也会同步更新');