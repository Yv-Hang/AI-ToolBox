-- ===============================================
-- AI-ToolBox 数据库初始化脚本
-- 版本: 1.0
-- 描述: 创建完整的数据库结构和初始数据
-- ===============================================

-- 创建数据库（如果不存在）
-- 使用 utf8mb4 字符集，支持表情符号等特殊字符
CREATE DATABASE IF NOT EXISTS `ToolBox` 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- 使用创建的数据库
USE `ToolBox`;

-- ===============================================
-- 用户表
-- 存储系统用户信息
-- ===============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，自增主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，登录使用',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱，用于登录和找回密码',
  `password` VARCHAR(100) NOT NULL COMMENT '密码，MD5加密存储',
  `status` INT(11) NOT NULL DEFAULT 1 COMMENT '用户状态：1-正常，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`) COMMENT '邮箱唯一索引',
  UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ===============================================
-- 会员信息表
-- 存储会员套餐信息
-- ===============================================
CREATE TABLE IF NOT EXISTS `membership_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '会员ID，自增主键',
  `name` VARCHAR(50) NOT NULL COMMENT '会员名称，如月度会员、年度会员',
  `price` DECIMAL(10,2) NOT NULL COMMENT '会员价格，精确到两位小数',
  `duration` INT(11) NOT NULL COMMENT '会员有效期，单位：天',
  `description` TEXT COMMENT '会员权益描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';

-- ===============================================
-- 用户会员表
-- 存储用户的会员状态信息
-- ===============================================
CREATE TABLE IF NOT EXISTS `user_membership` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID，自增主键',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，关联user表',
  `membership_id` BIGINT(20) NOT NULL COMMENT '会员ID，关联membership_info表',
  `start_time` DATETIME NOT NULL COMMENT '会员开始时间',
  `end_time` DATETIME NOT NULL COMMENT '会员结束时间',
  `status` INT(11) NOT NULL DEFAULT 1 COMMENT '会员状态：1-有效，0-过期',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '每个用户只能有一条会员记录',
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE COMMENT '用户删除时级联删除',
  FOREIGN KEY (`membership_id`) REFERENCES `membership_info` (`id`) ON DELETE CASCADE COMMENT '会员套餐删除时级联删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表';

-- ===============================================
-- 订单表
-- 存储用户购买会员的订单信息
-- ===============================================
CREATE TABLE IF NOT EXISTS `order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID，自增主键',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号，唯一标识',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，关联user表',
  `membership_id` BIGINT(20) NOT NULL COMMENT '会员ID，关联membership_info表',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额，精确到两位小数',
  `status` INT(11) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消',
  `payment_time` DATETIME COMMENT '支付时间，已支付状态时填写',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`) COMMENT '订单号唯一索引',
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE COMMENT '用户删除时级联删除',
  FOREIGN KEY (`membership_id`) REFERENCES `membership_info` (`id`) ON DELETE CASCADE COMMENT '会员套餐删除时级联删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ===============================================
-- AI对话历史表
-- 存储用户与AI的对话记录
-- ===============================================
CREATE TABLE IF NOT EXISTS `ai_chat_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID，自增主键',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，关联user表',
  `prompt` TEXT NOT NULL COMMENT '用户输入的提示词',
  `response` TEXT NOT NULL COMMENT 'AI生成的回复',
  `model` VARCHAR(50) NOT NULL COMMENT '使用的AI模型名称',
  `tokens` INT(11) NOT NULL DEFAULT 0 COMMENT '消耗的token数量',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE COMMENT '用户删除时级联删除',
  INDEX `idx_user_id` (`user_id`) COMMENT '用户ID索引，加速查询用户的对话历史',
  INDEX `idx_created_at` (`created_at`) COMMENT '创建时间索引，加速按时间查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话历史表';

-- ===============================================
-- 用户每日使用次数表
-- 存储普通用户每日使用AI的次数限制
-- ===============================================
CREATE TABLE IF NOT EXISTS `user_daily_usage` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID，自增主键',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，关联user表',
  `date` DATE NOT NULL COMMENT '日期，按天统计',
  `usage_count` INT(11) NOT NULL DEFAULT 0 COMMENT '当日使用次数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `date`) COMMENT '每个用户每天只能有一条记录',
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE COMMENT '用户删除时级联删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日使用次数表';

-- ===============================================
-- 初始化数据
-- ===============================================

-- 初始化会员套餐数据
INSERT INTO `membership_info` (`name`, `price`, `duration`, `description`) VALUES
('月度会员', 99.00, 30, '每月无限次使用AI文案生成服务，适合短期使用'),
('季度会员', 269.00, 90, '每季度无限次使用AI文案生成服务，性价比更高'),
('年度会员', 999.00, 365, '每年无限次使用AI文案生成服务，最经济实惠');

-- 初始化测试用户数据
-- 密码：123456（MD5加密后的值）
INSERT INTO `user` (`username`, `email`, `password`, `status`) VALUES
('testuser', 'test@example.com', 'e10adc3949ba59abbe56e057f20f883e', 1);

-- ===============================================
-- 脚本执行完成
-- ===============================================
-- 执行此脚本后，数据库结构和初始数据已准备就绪
-- 可以开始部署和运行AI-ToolBox应用