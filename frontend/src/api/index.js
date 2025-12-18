// 导入所有模块的接口函数
import * as auth from './auth'
import * as user from './user'
import * as royalty from './royalty'
import * as proxy from './proxy'
import * as feedback from './feedback'
import * as announcement from './announcement'
//给index加具名导出
export { auth, user, royalty, proxy, feedback, announcement }
// 统一导出，形成接口总对象
export default {
    auth,
    user,
    royalty,
    proxy,
    feedback,
    announcement
}