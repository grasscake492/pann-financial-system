import md5 from 'js-md5';
const SECRET_KEY = '文档指定的秘钥'; // 替换为接口文档中的秘钥

// 生成签名：按字段拼接后MD5加密
export const generateSign = (data) => {
    // 按字母序排序字段（文档要求）
    const sortedKeys = Object.keys(data).sort();
    let signStr = '';
    sortedKeys.forEach(key => {
        signStr += data[key];
    });
    signStr += SECRET_KEY;
    return md5(signStr);
};