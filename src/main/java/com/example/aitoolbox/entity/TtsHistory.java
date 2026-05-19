package com.example.aitoolbox.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tts_history")
public class TtsHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskId;

    private Long userId;

    private String model;

    private String mode;

    private String text;

    private String voice;

    private String voiceDescription;

    private String styleInstruction;

    private String audioFormat;

    private BigDecimal duration;

    private Integer textLength;

    private Integer sampleRate;

    private Integer tokensConsumed;

    private BigDecimal pointsDeducted;

    private Integer status;

    private String errorMessage;

    private LocalDateTime createdAt;
}
