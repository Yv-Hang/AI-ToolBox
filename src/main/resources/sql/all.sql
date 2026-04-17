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
  `points` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '积分余额',
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
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`membership_id`) REFERENCES `membership_info` (`id`) ON DELETE CASCADE
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
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`membership_id`) REFERENCES `membership_info` (`id`) ON DELETE CASCADE
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
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`) COMMENT '用户ID索引，加速查询用户的对话历史',
  INDEX `idx_created_at` (`created_at`) COMMENT '创建时间索引，加速按时间查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话历史表';

-- ===============================================
-- 积分变动记录表
-- 记录用户积分的增减历史
-- ===============================================
CREATE TABLE IF NOT EXISTS `points_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `type` INT(11) NOT NULL COMMENT '类型：1-卡密兑换，2-订单支付，3-AI服务扣费',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '变动金额',
  `balance` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_points_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动记录表';

-- ===============================================
-- 小红书文案生成记录表
-- 记录用户生成的小红书文案
-- ===============================================
CREATE TABLE IF NOT EXISTS `xiaohongshu_copywriting` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `original_prompt` TEXT NOT NULL COMMENT '原始提示词',
  `optimized_prompt` TEXT NOT NULL COMMENT '优化后提示词',
  `generated_content` TEXT COMMENT '生成的文案内容',
  `status` INT(11) NOT NULL DEFAULT 0 COMMENT '状态：0-失败，1-成功',
  `error_message` VARCHAR(255) DEFAULT NULL COMMENT '错误信息',
  `total_tokens` BIGINT(20) DEFAULT NULL COMMENT '总Token数',
  `prompt_tokens` BIGINT(20) DEFAULT NULL COMMENT '输入Token数',
  `completion_tokens` BIGINT(20) DEFAULT NULL COMMENT '输出Token数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_xiaohongshu_copywriting_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小红书文案生成记录表';

-- ===============================================
-- 卡密表
-- 存储可兑换积分的卡密
-- ===============================================
CREATE TABLE IF NOT EXISTS `card_key` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `key_code` VARCHAR(100) NOT NULL COMMENT '卡密编码',
  `points` DECIMAL(10,2) NOT NULL COMMENT '积分值',
  `status` INT(11) NOT NULL DEFAULT 1 COMMENT '状态：1-未使用，2-已使用，3-已过期',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '使用用户ID',
  `used_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key_code` (`key_code`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_card_key_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡密表';

-- ===============================================
-- AI模型表
-- 存储可用的AI模型信息
-- ===============================================
CREATE TABLE IF NOT EXISTS `ai_model` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '模型名称',
  `code` VARCHAR(50) NOT NULL COMMENT '模型代码',
  `description` TEXT COMMENT '模型描述',
  `price_per_1000_tokens` DECIMAL(10,6) NOT NULL COMMENT '每1000 tokens的价格',
  `status` INT(11) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型表';

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
INSERT INTO `user` (`username`, `email`, `password`, `status`, `points`) VALUES
('testuser', 'test@example.com', 'e10adc3949ba59abbe56e057f20f883e', 1, 100.00);

-- 初始化AI模型数据
INSERT INTO `ai_model` (`name`, `code`, `description`, `price_per_1000_tokens`, `status`) VALUES
('GPT-3.5 Turbo', 'gpt-3.5-turbo', 'OpenAI的GPT-3.5 Turbo模型，适合一般的文案生成任务', 0.0015, 1),
('GPT-4', 'gpt-4', 'OpenAI的GPT-4模型，适合更复杂的文案生成任务', 0.03, 1),
('GPT-4o', 'gpt-4o', 'OpenAI的GPT-4o模型，结合了GPT-4的能力和更快的速度', 0.005, 1),
('豆包', 'doubao', '字节跳动的豆包模型，适合中文文案生成', 0.002, 1);

-- ===============================================
-- 脚本执行完成
-- ===============================================
-- 执行此脚本后，数据库结构和初始数据已准备就绪
-- 可以开始部署和运行AI-ToolBox应用