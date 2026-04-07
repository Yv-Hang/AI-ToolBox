package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long membershipId;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime paymentTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
