// 部门模块（department.js）- Pinia 结构
import { defineStore } from 'pinia'
//导入接口
import api from '@/api/index.js'
// 导入util统一入口（工具都从这拿）
import { storage, format, permission } from '@/utils/index.js'
export const useDepartmentStore = defineStore('department', {
    // state：与数据库表字段一致，存储部门相关数据
    state: () => ({
        // 部门列表（结构对应 departments 表字段）
        department_list: [
            {
                department_id: '', // 部门ID（表主键）
                department_name: '' // 部门名称（表字段）
            }
        ],
        // 当前选中/所属部门ID（用于筛选稿费、代领记录等）
        current_department_id: '',
        // 部门总数（分页查询时使用）
        total: 0
    }),

    // getter：后续有需要再添加（如根据ID查部门名称）
    getters: {},

    // action：部门相关操作（方法体空着，仅留结构）
    actions: {
        // 存储查询到的部门列表
        setDepartmentList(list) {},

        // 设置当前选中的部门ID
        setCurrentDepartment(id) {},

        // 清空部门相关缓存（如退出登录时）
        clearDepartmentInfo() {},

        // 分页查询部门列表（如需分页时使用）
        fetchDepartmentList(page, size) {}
    }
})