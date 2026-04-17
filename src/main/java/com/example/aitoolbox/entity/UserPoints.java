package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_points")
public class UserPoints {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}