-- ============================================
-- PANN财务系统 - 数据库初始化脚本
-- 数据库名称: pann_financial_system
-- 创建日期: 2024-01-09
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS \pann_financial_system\
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE \pann_financial_system\;

-- 1. 用户表 (users)
CREATE TABLE IF NOT EXISTS \users\ (
    \user_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \student_number\ VARCHAR(20) NOT NULL UNIQUE,
    \eal_name\ VARCHAR(50) NOT NULL,
    \password\ VARCHAR(100) NOT NULL,
    \email\ VARCHAR(100) NOT NULL,
    INDEX \idx_email\ (\email\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 管理员表 (admins)
CREATE TABLE IF NOT EXISTS \dmins\ (
    \dmin_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_id\ BIGINT NOT NULL,
    \student_number\ VARCHAR(20) NOT NULL UNIQUE,
    \eal_name\ VARCHAR(50) NOT NULL,
    \password\ VARCHAR(100) NOT NULL,
    \email\ VARCHAR(100) NOT NULL,
    \department_id\ BIGINT,
    \is_super_admin\ BOOLEAN DEFAULT FALSE,
    INDEX \idx_user_id\ (\user_id\),
    INDEX \idx_department_id\ (\department_id\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 部门表 (departments)
CREATE TABLE IF NOT EXISTS \departments\ (
    \department_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \department_name\ VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 新闻部稿费月表 (news_department_monthly)
CREATE TABLE IF NOT EXISTS \
ews_department_monthly\ (
    \ecord_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_ids\ JSON NOT NULL,
    \eal_names\ JSON NOT NULL,
    \student_numbers\ JSON NOT NULL,
    \rticle_title\ VARCHAR(200) NOT NULL,
    \rticle_type\ VARCHAR(50) NOT NULL,
    \ee_amount\ DECIMAL(10,2) NOT NULL,
    \statistical_month\ VARCHAR(7) NOT NULL,
    \department_id\ BIGINT NOT NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX \idx_statistical_month\ (\statistical_month\),
    INDEX \idx_department_id\ (\department_id\),
    INDEX \idx_created_at\ (\created_at\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 编辑部稿费月表 (editorial_department_monthly)
CREATE TABLE IF NOT EXISTS \editorial_department_monthly\ (
    \ecord_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_ids\ JSON NOT NULL,
    \eal_names\ JSON NOT NULL,
    \student_numbers\ JSON NOT NULL,
    \rticle_title\ VARCHAR(200) NOT NULL,
    \rticle_type\ VARCHAR(50) NOT NULL,
    \ee_amount\ DECIMAL(10,2) NOT NULL,
    \statistical_month\ VARCHAR(7) NOT NULL,
    \department_id\ BIGINT NOT NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX \idx_statistical_month\ (\statistical_month\),
    INDEX \idx_department_id\ (\department_id\),
    INDEX \idx_created_at\ (\created_at\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 稿费月汇总表 (fee_monthly_summary)
CREATE TABLE IF NOT EXISTS \ee_monthly_summary\ (
    \summary_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_id\ BIGINT NOT NULL,
    \eal_name\ VARCHAR(50) NOT NULL,
    \student_number\ VARCHAR(20) NOT NULL,
    \department_id\ BIGINT NOT NULL,
    \	otal_amount\ DECIMAL(10,2) NOT NULL,
    \statistical_month\ VARCHAR(7) NOT NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX \idx_user_month\ (\user_id\, \statistical_month\),
    INDEX \idx_department_month\ (\department_id\, \statistical_month\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 代领记录表 (proxy_receipt_records)
CREATE TABLE IF NOT EXISTS \proxy_receipt_records\ (
    \proxy_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \ee_record_id\ BIGINT NOT NULL,
    \department_id\ BIGINT NOT NULL,
    \original_user_id\ BIGINT NOT NULL,
    \original_real_name\ VARCHAR(50) NOT NULL,
    \original_student_number\ VARCHAR(20) NOT NULL,
    \proxy_user_id\ BIGINT NOT NULL,
    \proxy_real_name\ VARCHAR(50) NOT NULL,
    \proxy_student_number\ VARCHAR(20) NOT NULL,
    \rticle_title\ VARCHAR(200) NOT NULL,
    \ee_amount\ DECIMAL(10,2) NOT NULL,
    \proxy_month\ VARCHAR(7) NOT NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX \idx_original_user_id\ (\original_user_id\),
    INDEX \idx_proxy_user_id\ (\proxy_user_id\),
    INDEX \idx_proxy_month\ (\proxy_month\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 问题反馈表 (feedbacks)
CREATE TABLE IF NOT EXISTS \eedbacks\ (
    \eedback_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_id\ BIGINT NOT NULL,
    \content\ TEXT NOT NULL,
    \eply_content\ TEXT,
    \eplied_at\ TIMESTAMP NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 公告表 (announcements)
CREATE TABLE IF NOT EXISTS \nnouncements\ (
    \nnouncement_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \	itle\ VARCHAR(200) NOT NULL,
    \content\ TEXT NOT NULL,
    \publisher_id\ BIGINT NOT NULL,
    \published_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 登录日志表 (login_logs)
CREATE TABLE IF NOT EXISTS \login_logs\ (
    \log_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_id\ BIGINT NOT NULL,
    \ip_address\ VARCHAR(45) NOT NULL,
    \login_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \status\ VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. 权限表 (permissions)
CREATE TABLE IF NOT EXISTS \permissions\ (
    \permission_id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \permission_code\ VARCHAR(50) NOT NULL UNIQUE,
    \permission_name\ VARCHAR(100) NOT NULL,
    \description\ TEXT,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    \updated_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. 用户权限关联表 (user_permissions)
CREATE TABLE IF NOT EXISTS \user_permissions\ (
    \id\ BIGINT PRIMARY KEY AUTO_INCREMENT,
    \user_id\ BIGINT NOT NULL,
    \permission_id\ BIGINT NOT NULL,
    \created_at\ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY \uk_user_permission\ (\user_id\, \permission_id\)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始数据
-- 权限数据
INSERT INTO \permissions\ (\permission_code\, \permission_name\, \description\) VALUES
('USER_READ', '用户查看', '查看用户信息权限'),
('USER_WRITE', '用户管理', '管理用户信息权限'),
('FEE_READ', '稿费查看', '查看稿费信息权限'),
('FEE_WRITE', '稿费管理', '管理稿费信息权限'),
('REPORT_READ', '报表查看', '查看统计报表权限'),
('SYSTEM_MANAGE', '系统管理', '系统管理权限');

-- 部门数据
INSERT INTO \departments\ (\department_name\) VALUES
('新闻部'),
('编辑部'),
('运营部');

-- 用户数据（注意：实际密码应该使用bcrypt加密，这里使用示例数据）
INSERT INTO \users\ (\student_number\, \eal_name\, \password\, \email\) VALUES
('202311110001', '系统管理员', 'xt123456', '123456789@qq.com'),
('202311110002', '张三', 'zs123456', '234567891@qq.com');

-- 管理员数据
INSERT INTO \dmins\ (\user_id\, \student_number\, \eal_name\, \password\, \email\, \department_id\, \is_super_admin\) VALUES
(1, '202311110001', '系统管理员', 'xt123456', '123456789@qq.com', NULL, TRUE);

-- 用户权限关联数据
INSERT INTO \user_permissions\ (\user_id\, \permission_id\) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);  -- 系统管理员拥有所有权限

-- 新闻部稿费月表数据
INSERT INTO \
ews_department_monthly\ (\user_ids\, \eal_names\, \student_numbers\, \rticle_title\, \rticle_type\, \ee_amount\, \statistical_month\, \department_id\) VALUES
('[2]', '[\"张三\"]', '[\"202311110002\"]', '测试更新删除稿件', '测试类型', 100.00, '2025-12', 1),
('[2]', '[\"张三\"]', '[\"202311110002\"]', '测试新增稿件', '新闻稿', 500.00, '2025-12', 1);

-- 稿费月汇总表数据
INSERT INTO \ee_monthly_summary\ (\user_id\, \eal_name\, \student_number\, \department_id\, \	otal_amount\, \statistical_month\) VALUES
(2, '张三', '202311110002', 1, 600.00, '2025-12');

-- 显示创建的表结构信息
SHOW TABLES;

-- 显示各表记录数
SELECT 
    TABLE_NAME, 
    TABLE_ROWS 
FROM 
    INFORMATION_SCHEMA.TABLES 
WHERE 
    TABLE_SCHEMA = 'pann_financial_system' 
ORDER BY 
    TABLE_NAME;
