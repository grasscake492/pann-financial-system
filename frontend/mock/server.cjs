const express = require('express');
const Mock = require('mockjs');
const cors = require('cors');
const app = express();

// 基础配置：解决跨域+解析JSON
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 内存模拟数据库（用户表/公告表/角色表）
const db = {
    users: [
        // 初始管理员账号：用户名admin，密码123456
        {
            id: Mock.mock('@id'),
            username: 'admin',
            password: '123456',
            real_name: '系统管理员',
            role: 'admin',
            token: Mock.mock('@guid'),
            create_time: Mock.mock('@datetime')
        }
    ],
    announcements: [], // 公告表（初始空）
    roles: {
        admin: ['announcement:create', 'announcement:view'],
        user: ['announcement:view']
    }
};

// 工具函数：生成token
const generateToken = () => Mock.mock('@guid');
// 工具函数：校验token
const verifyToken = (token) => db.users.find(u => u.token === token) || null;

// 接口1：用户注册（POST /api/register）
app.post('/api/register', (req, res) => {
    const { username, password, real_name } = req.body;
    if (db.users.find(u => u.username === username)) {
        return res.json({ code: 1, msg: '用户名已存在' });
    }
    const newUser = {
        id: Mock.mock('@id'),
        username,
        password,
        real_name,
        role: 'user',
        token: generateToken(),
        create_time: Mock.mock('@datetime')
    };
    db.users.push(newUser);
    res.json({ code: 0, msg: '注册成功', data: { token: newUser.token, real_name: newUser.real_name, role: newUser.role } });
});

// 接口2：用户登录（POST /api/login）
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    const user = db.users.find(u => u.username === username && u.password === password);
    if (!user) {
        return res.json({ code: 1, msg: '用户名或密码错误' });
    }
    user.token = generateToken(); // 刷新token
    res.json({ code: 0, msg: '登录成功', data: { token: user.token, real_name: user.real_name, role: user.role } });
});

// 接口3：获取用户信息（GET /api/user/profile）
app.get('/api/user/profile', (req, res) => {
    const token = req.query.token || req.headers.authorization?.split(' ')[1];
    const user = verifyToken(token);
    if (!user) {
        return res.json({ code: 1, msg: 'token无效' });
    }
    res.json({ code: 0, data: { real_name: user.real_name, role: user.role } });
});

// 接口4：管理员创建公告（POST /api/announcement/create）
app.post('/api/announcement/create', (req, res) => {
    const token = req.headers.authorization?.split(' ')[1];
    const user = verifyToken(token);
    if (!user) return res.json({ code: 1, msg: '未登录' });
    if (user.role !== 'admin') return res.json({ code: 1, msg: '无权限' });

    const { content } = req.body;
    const newNotice = {
        announcement_id: Mock.mock('@id'),
        content,
        published_at: Mock.mock('@datetime'),
        creator: user.real_name
    };
    db.announcements.unshift(newNotice);
    res.json({ code: 0, msg: '公告创建成功', data: newNotice });
});

// 接口5：获取最新公告（GET /api/announcement/list）
app.get('/api/announcement/list', (req, res) => {
    const token = req.query.token || req.headers.authorization?.split(' ')[1];
    const user = verifyToken(token);
    if (!user) return res.json({ code: 1, msg: '未登录' });

    const list = db.announcements.slice(0, 3); // 只返回最新3条
    res.json({ code: 0, data: { list } });
});

// 启动Mock服务（端口3001）
const PORT = 3001;
app.listen(PORT, () => {
    console.log(`✅ Mock后端服务已启动：http://localhost:${PORT}`);
    console.log(`🔑 初始管理员账号：admin / 密码：123456`);
});