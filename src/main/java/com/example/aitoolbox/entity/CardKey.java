package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("card_key")
public class CardKey {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("key_code")
    private String keyCode;
    private BigDecimal points;
    private Integer status;
    @TableField("expire_time")
    private LocalDateTime expireTime;
    @TableField("user_id")
    private Long userId;
    @TableField("used_time")
    private LocalDateTime usedTime;
    @TableField("created_at")
    private LocalDateTime createdAt;
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}