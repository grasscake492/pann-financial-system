// 反馈状态枚举（与接口约定一致，避免硬编码）
export const FEEDBACK_STATUS = {
    PENDING: 'pending', // 待处理（默认状态）
    REPLIED: 'replied', // 已回复（管理员回复后）
}

// 状态中文映射（用于页面展示）
export const FEEDBACK_STATUS_LABEL = {
    [FEEDBACK_STATUS.PENDING]: '待处理',
    [FEEDBACK_STATUS.REPLIED]: '已回复',
}

// 状态对应的样式类（用于页面标签颜色区分）
export const FEEDBACK_STATUS_CLASS = {
    [FEEDBACK_STATUS.PENDING]: 'status-pending',
    [FEEDBACK_STATUS.REPLIED]: 'status-replied',
}