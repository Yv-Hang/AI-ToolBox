package com.example.aitoolbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String email;
    private String membershipLevel;
    private LocalDateTime membershipExpireTime;
    private Boolean isMember;
    private BigDecimal points;
}
