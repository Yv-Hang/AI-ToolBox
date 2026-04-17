package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("xiaohongshu_copywriting")
public class XiaohongshuCopywriting {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("original_prompt")
    private String originalPrompt;
    @TableField("optimized_prompt")
    private String optimizedPrompt;
    @TableField("generated_content")
    private String generatedContent;
    private Integer status;
    @TableField("error_message")
    private String errorMessage;
    @TableField("total_tokens")
    private Long totalTokens;
    @TableField("prompt_tokens")
    private Long promptTokens;
    @TableField("completion_tokens")
    private Long completionTokens;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}