-- ===============================================
-- TTS语音合成历史记录表
-- 版本: 1.0
-- 描述: 存储用户的语音合成历史记录
-- ===============================================

USE `ToolBox`;

-- ===============================================
-- TTS语音合成历史表
-- ===============================================
CREATE TABLE IF NOT EXISTS `tts_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID，自增主键',
  `task_id` VARCHAR(64) NOT NULL COMMENT '任务ID，唯一标识',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，关联user表',
  `model` VARCHAR(50) NOT NULL COMMENT '使用的模型：mimo-v2.5-tts/mimo-v2.5-tts-voicedesign/mimo-v2.5-tts-voiceclone',
  `mode` VARCHAR(20) NOT NULL COMMENT '模式：preset-预置音色/design-音色设计/clone-音色克隆',
  `text` TEXT NOT NULL COMMENT '合成的文本内容',
  `voice` VARCHAR(100) DEFAULT NULL COMMENT '音色ID（预置音色模式）',
  `voice_description` TEXT DEFAULT NULL COMMENT '音色描述（音色设计模式）',
  `style_instruction` TEXT DEFAULT NULL COMMENT '风格指令',
  `audio_format` VARCHAR(10) NOT NULL DEFAULT 'wav' COMMENT '音频格式：wav/mp3/pcm16',
  `duration` DECIMAL(10,2) DEFAULT NULL COMMENT '音频时长（秒）',
  `text_length` INT(11) DEFAULT NULL COMMENT '合成文本长度',
  `sample_rate` INT(11) DEFAULT NULL COMMENT '采样率',
  `tokens_consumed` INT(11) DEFAULT 0 COMMENT '消耗的token数量（如果可获取）',
  `points_deducted` DECIMAL(10,2) DEFAULT 0.00 COMMENT '扣除的积分',
  `status` INT(11) NOT NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`) COMMENT '任务ID唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_created_at` (`created_at`) COMMENT '创建时间索引',
  KEY `idx_mode` (`mode`) COMMENT '模式索引',
  CONSTRAINT `fk_tts_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='TTS语音合成历史表';

-- ===============================================
-- 脚本执行完成
-- ===============================================
