// 数据格式化工具：处理项目中高频使用的格式转换（时间、金额、部门等）
// 统一格式规则，避免页面中重复写格式化逻辑
import md5 from 'md5'


/**
 * 生成接口签名（严格按文档2.2节规则）
 * @param {Object} params - 参与签名的参数（如timestamp）
 * @returns {string} - 生成的md5签名
 */
export const generateSign = (params = {}) => {
    // 步骤1：按文档要求拼接参数（示例：参数key按字母排序 + 固定秘钥）
    const secretKey = 'your_backend_secret' // 后端给的固定秘钥（文档里会写）
    // 1.1 提取参数并按key升序排序（文档最常见规则）
    const sortedKeys = Object.keys(params).sort()
    // 1.2 拼接成 "key1=value1&key2=value2" 格式
    const paramStr = sortedKeys.map(key => `${key}=${params[key]}`).join('&')
    // 1.3 末尾拼接秘钥（文档规则：比如 paramStr + secretKey）
    const signStr = `${paramStr}${secretKey}`

    // 步骤2：md5加密（文档要求的签名算法，一般是md5）
    return md5(signStr)
}


/**
 * 时间格式化：转换为 YYYY-MM-DD HH:mm 格式（适配创建时间、发布时间等）
 * @param {string/Date} time - 原始时间
 * @returns {string} 格式化后的时间（如2025-01-15 10:30）
 */
export const formatDateTime = (time) => {
    if (!time) return '';
    const date = new Date(time);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${minute}`;
};

/**
 * 金额格式化：保留2位小数（适配稿费金额字段）
 * @param {number/string} amount - 原始金额（如150、120.5）
 * @returns {string} 格式化后的金额（如150.00、120.50）
 */
export const formatMoney = (amount) => {
    if (!amount && amount !== 0) return '0.00';
    const num = Number(amount);
    // 非数字返回默认值
    if (isNaN(num)) return '0.00';
    // 保留2位小数，四舍五入
    return num.toFixed(2);
};

/**
 * 部门ID转名称（适配部门表数据）
 * @param {number} departmentId - 部门ID（1-新闻部、2-编辑部、3-运营部）
 * @returns {string} 部门名称
 */
export const formatDepartment = (departmentId) => {
    const departmentMap = {
        1: '新闻部',
        2: '编辑部',
        3: '运营部'
    };
    // 无匹配ID返回"未知部门"
    return departmentMap[departmentId] || '未知部门';
};