// src/util/index.js
// 导入所有工具文件，统一导出
import { request } from './request.js';   // axios封装
import * as storage from './storage.js'   // 本地存储
import * as format from './format.js'     // 格式化
import * as permission from './permission.js' // 权限
import * as validate from './validate.js' // 校验
// 统一导出（核心：后续所有文件都从这里导入util）
export {
    request,
    storage,
    format,
    permission,
    validate
}