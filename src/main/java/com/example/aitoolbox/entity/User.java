package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.INPUT)
    private Long id;

    private String username;
    private String email;
    private String password;
    private Integer status;
    private BigDecimal points;

    @TableField(fill = FieldFill.INSERT, value = "created_at")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "updated_at")
    private LocalDateTime updatedAt;
}